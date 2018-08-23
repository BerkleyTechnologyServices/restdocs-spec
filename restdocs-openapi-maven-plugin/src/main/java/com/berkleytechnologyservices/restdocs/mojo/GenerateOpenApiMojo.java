package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Goal which generates OpenAPI Specification
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateOpenApiMojo extends AbstractMojo {

  /**
   * Restdocs Snippets directory
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-snippets/openapi", property = "sourceDir", required = true)
  private File snippetDirectory;

  /**
   * Output directory
   */
  @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
  private File outputDirectory;

  /**
   * OpenAPI spec file name
   */
  @Parameter(defaultValue = "openapi-spec.yml", property = "filename", required = true)
  private String filename;

  /**
   *
   */
  @Parameter(defaultValue = "false", property = "restdocs-openapi.skip")
  private boolean skip;

  private final ObjectMapper objectMapper;

  public GenerateOpenApiMojo() {
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Execute the mojo
   *
   * @throws MojoExecutionException if execution fails
   */
  public void execute() throws MojoExecutionException {
    if (skip) {
      getLog().info("Skipping generation of OpenAPI document.");
    } else {
      precheck();

      try (DirectoryStream<Path> stream = getModelFiles()) {
        writeYaml(createOpenApi(stream));
      } catch (IOException e) {
        throw new MojoExecutionException("Unable to read OpenApiModel files: " + snippetDirectory);
      }
    }
  }

  private void precheck() throws MojoExecutionException {
    if (outputDirectory.mkdirs()) {
      getLog().info("Creating output directory: " + outputDirectory);
    }
    if (!outputDirectory.exists() || !outputDirectory.canWrite()) {
      throw new MojoExecutionException("Unable to create output directory: " + outputDirectory);
    }
    if (!snippetDirectory.exists() || !snippetDirectory.canRead()) {
      throw new MojoExecutionException("Unable to read from snippet directory: " + snippetDirectory);
    }
  }

  private DirectoryStream<Path> getModelFiles() throws IOException {
    return Files.newDirectoryStream(snippetDirectory.toPath(), "*.json");
  }

  private OpenApiModel readOpenApiModel(Path path) throws MojoExecutionException {
    try {
      return this.objectMapper.readValue(path.toFile(), OpenApiModel.class);
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to parse OpenApiModel file: " + path, e);
    }
  }

  private OpenAPI createOpenApi(DirectoryStream<Path> stream) throws MojoExecutionException {
    OpenApiBuilder builder = new OpenApiBuilder();

    for (Path path : stream) {
      OpenApiModel model = readOpenApiModel(path);
      builder.request(model.getRequest());
    }

    return builder.build();
  }

  private void writeYaml(OpenAPI openAPI) throws MojoExecutionException {
    String yamlString = Yaml.pretty(openAPI);
    Path filePath = new File(outputDirectory, filename).toPath();
    try {
      Files.write(filePath, yamlString.getBytes());
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to write OpenAPI spec file: " + filePath);
    }
  }
}
