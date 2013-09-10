# Maven Plugin for [Dynamic Extensions for Alfresco](https://github.com/lfridael/dynamic-extensions-for-alfresco)

This is a Maven plugin for **Dynamic Extensions for Alfresco** that, if added to a projects POM file, enables the bundle to be hot-deployed to a Dynamic Extensions-enabled Alfresco Repository container as an OSGi module. It mainly does the same thing as the hot-deploy feature of the [Gradle plugin](https://github.com/lfridael/dynamic-extensions-for-alfresco/wiki/Building-Alfresco-repository-extensions-for-Dynamic-Extensions-using-Gradle) developed within the **Dynamic Extensions for Alfresco** project.

To use it, two things has to be done in the Maven POM file.

## Usage

### 1. Add a plugin repository

```xml
<pluginRepositories>
  <pluginRepository>
    <id>oakman-dynamicextensions-maven-plugin</id>
    <url>https://raw.github.com/oakman/dynamicextensions-maven-plugin/mvn-repo</url>
  </pluginRepository>
</pluginRepositories>
```

### 2. Add the plugin to the build

```xml
<plugin>
  <groupId>org.redpill-linpro.alfresco</groupId>
  <artifactId>dynamicextensions-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <goals>
        <goal>install-bundle</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Whenever a `mvn install` command is executed, the built artifact is uploaded to the repository. 

## Configuration options

There are some configuration options that can be set.

| Option      | Default value                   | Description
|:----------- |:------------------------------- |:-----------
| username    | admin                           | The username for the Alfresco Repository to upload to
| password    | admin                           | The password for the Alfresco Repository to upload to
| mimetype    | application/java-archive        | The mimetype that's used for uploading
| hostname    | localhost                       | The host of the Alfresco Repository
| port        | 8080                            | The port of the Alfresco Repository
| servicePath | /alfresco/service               | The service path of the Alfresco Repository
| path        | /dynamic-extensions/api/bundles | The API path to use for the upload