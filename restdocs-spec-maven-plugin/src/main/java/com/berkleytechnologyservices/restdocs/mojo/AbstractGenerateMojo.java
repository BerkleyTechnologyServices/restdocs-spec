package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.AuthConfig;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.SpecificationFormat;
import com.berkleytechnologyservices.restdocs.spec.Tag;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorException;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorFactory;
import com.epages.restdocs.apispec.model.ResourceModel;
import com.google.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract class for implementing a mojo for generating an api specification using snippet files.
 */
public abstract class AbstractGenerateMojo extends AbstractMojo {

  private static final String FORMAT_UNSUPPORTED = "%s does not support '%s' format. Supported formats include the following: %s";

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
   * Description of the API
   */
  @Parameter(defaultValue = "${project.description}", required = true)
  private String description;

  /**
   * Host
   */
  @Parameter(defaultValue = "localhost", required = true)
  private String host;

  /**
   * Base path
   */
  @Parameter
  private String basePath;

  /**
   * Schemes
   */
  @Parameter
  @SuppressWarnings("FieldMayBeFinal")
  private List<String> schemes = Collections.emptyList();

  /**
   * Output directory
   */
  @Parameter(defaultValue = "${project.build.directory}/restdocs-spec", property = "outputDir", required = true)
  private File outputDirectory;

  /**
   * Skip specification generation
   */
  @Parameter(defaultValue = "false", property = "skipSpecGenerator")
  private boolean skip;

  /**
   * The type of specification to generate
   */
  @Parameter(defaultValue = "OPENAPI_V2", property = "specification", required = true)
  @SuppressWarnings("FieldMayBeFinal")
  private Specification specification = Specification.OPENAPI_V2;

  /**
   * Format
   */
  @Parameter(property = "format")
  private SpecificationFormat format;

  /**
   * OpenAPI spec file name
   */
  @Parameter(property = "filename")
  private String filename;

  @Parameter(defaultValue = "false", property = "separatePublicApi", required = true)
  private boolean separatePublicApi;

  @Parameter
  private List<SpecificationOptions> specifications = Collections.emptyList();

  @Parameter
  private AuthConfig oauth2 = new AuthConfig();

  /**
   * Mapping of tag names to descriptions. These are populated into the top level.
   * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#tag-object>https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#tag-object</a>
   * No default - if not provided no tags will be created.
   */
  @Parameter
  private List<Tag> tags;

  private final SpecificationGeneratorFactory specificationGeneratorFactory;

  @Inject
  public AbstractGenerateMojo(SpecificationGeneratorFactory specificationGeneratorFactory) {
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
      validateOutputDirectory();

      List<SpecificationOptions> allSpecificationOptions = getAllSpecificationOptions();
      validateOptions(allSpecificationOptions);
      generateSpecifications(allSpecificationOptions);
    }
  }

  private void validateOutputDirectory() throws MojoExecutionException {
    if (outputDirectory.mkdirs()) {
      getLog().info("Creating output directory: " + outputDirectory);
    }
    if (!outputDirectory.exists() || !outputDirectory.canWrite()) {
      throw new MojoExecutionException("Unable to create output directory: " + outputDirectory);
    }
  }

  private void validateOptions(List<SpecificationOptions> allSpecificationOptions) throws MojoExecutionException {
    for (SpecificationOptions options : allSpecificationOptions) {
      validateOptions(options);
    }
  }

  private void validateOptions(SpecificationOptions options) throws MojoExecutionException {
    if (!options.getType().supportsFormat(options.getFormat())) {
      throw new MojoExecutionException(String.format(
          FORMAT_UNSUPPORTED,
          options.getType(),
          options.getFormat(),
          options.getType().getSupportedFormats()
      ));
    }
  }

  protected abstract List<ResourceModel> getModels() throws MojoExecutionException;

  private void generateSpecifications(List<SpecificationOptions> allSpecificationOptions) throws MojoExecutionException {
    List<ResourceModel> snippets = getModels();
    for (SpecificationOptions options : allSpecificationOptions) {
      writeSpecificationToFile(options.getFilenameWithExtension(), generateSpecification(options, snippets));

      if (separatePublicApi) {
        List<ResourceModel> publicResources = snippets.stream()
            .filter(resource -> !resource.getPrivateResource())
            .collect(Collectors.toList());

        writeSpecificationToFile(options.getPublicFilenameWithExtension(), generateSpecification(options, publicResources));
      }
    }
  }

  private String generateSpecification(SpecificationOptions options, List<ResourceModel> models) throws MojoExecutionException {
    try {
      return specificationGeneratorFactory.createGenerator(options.getType()).generate(createApiDetails(options), models);
    } catch (SpecificationGeneratorException e) {
      throw new MojoExecutionException("Unable to generate specification.", e);
    }
  }

  private void writeSpecificationToFile(String filename, String outputString) throws MojoExecutionException {
    Path filePath = new File(outputDirectory, filename).toPath();
    try {
      Files.write(filePath, outputString.getBytes());
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to write outputString file: " + filePath);
    }
  }

  private ApiDetails createApiDetails(SpecificationOptions options) {
    return new ApiDetails()
        .name(name)
        .version(version)
		.description(description)
        .host(host)
        .basePath(basePath)
        .schemes(schemes)
        .format(options.getFormat())
        .authConfig(oauth2)
        .tags(tags);
  }

  private List<SpecificationOptions> getAllSpecificationOptions() {
    List<SpecificationOptions> options = new ArrayList<>(this.specifications);

    if (options.isEmpty()) {
      SpecificationOptions defaultOptions = new SpecificationOptions();
      defaultOptions.setType(this.specification);
      defaultOptions.setFormat(this.format);
      defaultOptions.setFilename(this.filename);
      options.add(defaultOptions);
    }

    return options;
  }
}
