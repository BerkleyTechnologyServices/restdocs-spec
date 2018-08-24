package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * This mojo generates an api specification using snippet files.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateMojo extends AbstractMojo {

  /**
   * Name of the API
   */
  @Parameter(defaultValue = "${project.artifactId}", required = true)
  private String name;

  /**
   * Version of the API
   */
  @Parameter(defaultValue = "${project.version}", required = true)
  private String version;

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
   * Skip specification generation
   */
  @Parameter(defaultValue = "false", property = "skipSpecGenerator")
  private boolean skip;

  /**
   * The type of specification to generate
   */
  @Parameter(defaultValue = "OPENAPI_V3", property="specification", required = true)
  private Specification specification = Specification.OPENAPI_V3;

  private final SnippetReader snippetReader;
  private final SpecificationGeneratorFactory specificationGeneratorFactory;

  @Inject
  public GenerateMojo(SnippetReader snippetReader,
                      SpecificationGeneratorFactory specificationGeneratorFactory) {
    this.snippetReader = snippetReader;
    this.specificationGeneratorFactory = specificationGeneratorFactory;
  }

  /**
   * Execute the mojo
   *
   * @throws MojoExecutionException if execution fails
   */
  public void execute() throws MojoExecutionException {
    if (skip) {
      getLog().info("Skipping generation of API specification document.");
    } else {
      validateParameters();
      generateSpecification();
    }
  }

  private void validateParameters() throws MojoExecutionException {
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

  private void generateSpecification() throws MojoExecutionException {
    List<OpenApiModel> models = snippetReader.getModels(snippetDirectory);
    SpecificationGenerator generator = specificationGeneratorFactory.createGenerator(specification);
    writeSpecificationToFile(generator.generate(new ApiDetails(name, version), models));
  }

  private void writeSpecificationToFile(String specification) throws MojoExecutionException {
    Path filePath = new File(outputDirectory, filename).toPath();
    try {
      Files.write(filePath, specification.getBytes());
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to write specification file: " + filePath);
    }
  }
}
