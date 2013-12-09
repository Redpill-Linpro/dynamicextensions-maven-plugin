package org.redpill.maven.alfresco;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RestClient {

  public static final String DEFAULT_HOST = "localhost";

  public static final String DEFAULT_PORT = "8080";

  public static final String DEFAULT_SERVICE_PATH = "/alfresco/service";

  public static final String DEFAULT_USERNAME = "admin";

  public static final String DEFAULT_PASSWORD = "admin";

  private String _host = DEFAULT_HOST;

  private String _port = DEFAULT_PORT;

  private String _servicePath;

  private String _username;

  private String _password;

  private Log _log;

  public RestClient(Log log) {
    this(log, DEFAULT_USERNAME, DEFAULT_PASSWORD);
  }

  public RestClient(Log log, String username, String password) {
    this(log, username, password, DEFAULT_HOST, DEFAULT_PORT, DEFAULT_SERVICE_PATH);
  }

  public RestClient(Log log, String username, String password, String host, String port, String servicePath) {
    _log = log;
    _username = username;
    _password = password;
    _host = host;
    _port = port;
    _servicePath = servicePath;
  }

  public void postFile(String path, File file, String mimetype) {
    // then do a ping to see if the server is up, if not, log and just exit
    if (!ping()) {
      _log.info("Can't contact " + _host + " on port " + _port + ", exiting...");
      return;
    }

    HttpHost targetHost = new HttpHost(_host, Integer.parseInt(_port), "http");

    CredentialsProvider credsProvider = new BasicCredentialsProvider();

    credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(_username, _password));

    CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

    try {
      // Create AuthCache instance
      AuthCache authCache = new BasicAuthCache();

      // Generate BASIC scheme object and add it to the local auth cache
      BasicScheme basicAuth = new BasicScheme();
      authCache.put(targetHost, basicAuth);

      // Add AuthCache to the execution context
      HttpClientContext localContext = HttpClientContext.create();
      localContext.setAuthCache(authCache);

      String url = "http://" + _host + ":" + _port + _servicePath + path;

      HttpPost httppost = new HttpPost(url);

      InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);

      reqEntity.setContentType(mimetype);

      reqEntity.setChunked(true);

      httppost.setEntity(reqEntity);

      CloseableHttpResponse response = null;

      try {
        response = httpclient.execute(targetHost, httppost, localContext);

        HttpEntity resEntity = response.getEntity();

        int statusCode = response.getStatusLine().getStatusCode();

        String content = EntityUtils.toString(resEntity);

        JSONObject json = (JSONObject) JSONValue.parse(content);

        String message = content;

        if (json.containsKey("message")) {
          message = (String) json.get("message");
        }

        if (statusCode != 200) {
          throw new MojoFailureException(message);
        }

        _log.info("");

        _log.info(message);

        if (json.containsKey("bundleId")) {
          _log.info("Bundle ID: " + json.get("bundleId").toString());
        }

        _log.info("");

        EntityUtils.consume(resEntity);
      } finally {
        closeQuietly(response);
      }
    } catch (Exception ex) {
      _log.error("Can't contact " + _host + " on port " + _port + ", exiting...");
    } finally {
      closeQuietly(httpclient);
    }
  }

  private void closeQuietly(Closeable closeable) {
    try {
      closeable.close();
    } catch (Exception ex) {
    }
  }

  private boolean ping() {
    try {
      TelnetClient telnetClient = new TelnetClient();
      telnetClient.setDefaultTimeout(500);
      telnetClient.connect(_host, Integer.parseInt(_port));

      return true;
    } catch (Exception ex) {
      return false;
    }
  }

}
