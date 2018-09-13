# Restdocs Spec Generation Support

[![Build Status](https://travis-ci.org/BerkleyTechnologyServices/restdocs-spec.svg?branch=master)](https://travis-ci.org/BerkleyTechnologyServices/restdocs-spec)
[![Release](https://jitpack.io/v/berkleytechnologyservices/restdocs-spec.svg)](https://jitpack.io/#berkleytechnologyservices/restdocs-spec)

## Usage

First head over to the [ePages-de/restdocs-openapi](https://github.com/ePages-de/restdocs-openapi) project
and following the instructions for setting up the Spring REST Docs extension.  That extension will produce
`resource.json` files for each of your documented resources.  You'll also notice that project provides
a gradle plugin that can be used to read all the `resource.json` files and turn them into an OpenAPI spec
file.  That is exactly what this project does as well, only in the form of a maven plugin instead.

Here is a typical `pom.xml` configuration:

```xml
  <plugin>
    <groupId>com.github.berkleytechnologyservices.restdocs-spec</groupId>
    <artifactId>restdocs-spec-maven-plugin</artifactId>
    <version>${restdocs-spec.version}</version>
    <executions>
      <execution>
        <goals>
          <goal>generate</goal>
        </goals>
      </execution>
    </executions>
  </plugin>
``` 

That will read your `resource.json` snippet files found under the `${project.build.directory}/generated-snippets` 
directory and produce an OpenAPI 2.0 YAML file at `${project.build.directory}/restdocs-spec/openapi-2.0.yml`.

If you would prefer that the OpenAPI 2.0 document is in JSON format you can specify it like this:

```xml
  <plugin>
    <groupId>com.github.berkleytechnologyservices.restdocs-spec</groupId>
    <artifactId>restdocs-spec-maven-plugin</artifactId>
    <version>${restdocs-spec.version}</version>
    <executions>
      <execution>
        <goals>
          <goal>generate</goal>
        </goals>
        <configuration>
          <format>JSON</format>
        </configuration>
      </execution>
    </executions>
  </plugin>
```

There are several other aspects you can optionally configure.  Here is the full set of options with their default values:

```xml
  <plugin>
    <groupId>com.github.berkleytechnologyservices.restdocs-spec</groupId>
    <artifactId>restdocs-spec-maven-plugin</artifactId>
    <version>${restdocs-spec.version}</version>
    <executions>
      <execution>
        <goals>
          <goal>generate</goal>
        </goals>
        <configuration>
          <name>${project.artifactId}</name>
          <version>${project.version}</version>
          <host>localhost</host>
          <basePath></basePath>
          <schemes>
            <scheme>http</scheme>
          </schemes>
          <snippetDirectory>${project.build.directory}/generated-snippets</snippetDirectory>
          <outputDirectory>${project.build.directory}/restdocs-spec</outputDirectory>
          <skip>false</skip>
          <format>JSON</format>
          <filename>openapi-2.0</filename>
          <oauth2 />
        </configuration>
      </execution>
    </executions>
  </plugin>
```

### OAuth2 Configuration

By default the plugin will not generate any security information.  You can optionally provide
it using the `<oauth2 />` element.  Here is an example:

```xml
  <plugin>
    <groupId>com.github.berkleytechnologyservices.restdocs-spec</groupId>
    <artifactId>restdocs-spec-maven-plugin</artifactId>
    <version>${restdocs-spec.version}</version>
    <executions>
      <execution>
        <goals>
          <goal>generate</goal>
        </goals>
        <configuration>
          <oauth2>
            <tokenUrl>http://example.com/uaa/token</tokenUrl>
            <authorizationUrl>http://example.com/uaa/authorize</authorizationUrl>
            <flows>
              <flow>accessCode</flow>
              <flow>implicit</flow>
            </flows>
            <scopes>
              <scope>
                <name>read</name>
                <description>Access to read operations.</description>
              </scope>            
              <scope>
                <name>write</name>
                <description>Access to write operations.</description>
              </scope>
            </scopes>
          </oauth2>
        </configuration>
      </execution>
    </executions>
  </plugin>
```

## Still in development

* Support for additional specification formats is currently in development.  We plan to add 
  support for OpenAPI 3.0 and Postman Collections.
* Currently the plugin is not available in Maven Central.  However, it is available through
  [JitPack](https://jitpack.io/) and we do plan to eventually get it into Maven Central.