/*
 * dynamicextensions-maven-plugin - Maven plugin for building Dynamic Extension projects.
 * 
 * Copyright (c) 2013 held jointly by Niklas Ekman
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * > http://www.fsf.org/licensing/licenses/lgpl.html
 * > http://www.opensource.org/licenses/lgpl-license.php
 */

package org.redpill.maven.alfresco;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which installs a bundle in a Alfresco Repository server.
 */
@Mojo(name = "install-bundle", defaultPhase = LifecyclePhase.INSTALL, requiresProject = true)
public class InstallBundleMojo extends AbstractMojo {

  public static final String DEFAULT_MIMETYPE = "application/java-archive";

  public static final String DEFAULT_PATH = "/dynamic-extensions/api/bundles";

  /**
   * The username to authenticate the call for.
   */
  @Parameter(property = "admin", defaultValue = RestClient.DEFAULT_USERNAME)
  private String _username;

  /**
   * The password to authenticate the call for.
   */
  @Parameter(property = "admin", defaultValue = RestClient.DEFAULT_PASSWORD)
  private String _password;

  /**
   * The mimetype to use for the upload
   */
  @Parameter(property = "mimetype", defaultValue = DEFAULT_MIMETYPE)
  private String _mimetype;

  /**
   * The hostname to upload to
   */
  @Parameter(property = "hostname", defaultValue = RestClient.DEFAULT_HOST)
  private String _hostname;

  /**
   * The port of the host to upload to
   */
  @Parameter(property = "port", defaultValue = RestClient.DEFAULT_PORT)
  private String _port;

  /**
   * The servicePath to upload to
   */
  @Parameter(property = "servicePath", defaultValue = RestClient.DEFAULT_SERVICE_PATH)
  private String _servicePath;

  /**
   * The path to upload to
   */
  @Parameter(property = "path", defaultValue = DEFAULT_PATH)
  private String _path;

  @Parameter(defaultValue = "${project.artifact}", required = true, readonly = true)
  private Artifact _artifact;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    File file = _artifact.getFile();

    if (!file.exists()) {
      throw new MojoFailureException("File not found: " + file.getAbsolutePath());
    }

    if (!file.getName().endsWith(".jar")) {
      throw new IllegalArgumentException("Not a JAR file: " + file.getAbsolutePath());
    }

    RestClient _client = new RestClient(getLog(), _username, _password, _hostname, _port, _servicePath);

    _client.postFile(_path, file, _mimetype);
  }
}
