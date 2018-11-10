package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.AuthConfig;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.SpecificationFormat;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorException;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorFactory;
import com.epages.restdocs.apispec.model.ResourceModel;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This mojo generates an api specification using snippet files.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateMojo extends AbstractMojo {

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
   * Restdocs Snippets directory
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-snippets", property = "sourceDir", required = true)
  private File snippetsDirectory;

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
      validateDirectories();

      List<SpecificationOptions> allSpecificationOptions = getAllSpecificationOptions();
      validateOptions(allSpecificationOptions);
      generateSpecifications(allSpecificationOptions);
    }
  }

  private void validateDirectories() throws MojoExecutionException {
    if (outputDirectory.mkdirs()) {
      getLog().info("Creating output directory: " + outputDirectory);
    }
    if (!outputDirectory.exists() || !outputDirectory.canWrite()) {
      throw new MojoExecutionException("Unable to create output directory: " + outputDirectory);
    }
    if (!snippetsDirectory.exists() || !snippetsDirectory.canRead()) {
      throw new MojoExecutionException("Unable to read from snippets directory: " + snippetsDirectory);
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

  private void generateSpecifications(List<SpecificationOptions> allSpecificationOptions) throws MojoExecutionException {
    List<ResourceModel> snippets = snippetReader.getModels(snippetsDirectory);
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
        .host(host)
        .basePath(basePath)
        .schemes(schemes)
        .format(options.getFormat())
        .authConfig(oauth2);
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
