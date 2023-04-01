package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorFactory;
import com.epages.restdocs.apispec.model.ResourceModel;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.List;

/**
 * This mojo generates an api specification using snippet files.
 */
@Named
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class GenerateMojo extends AbstractGenerateMojo {

  /**
   * Restdocs Snippets directory
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-snippets", property = "sourceDir", required = true)
  private File snippetsDirectory;

  private final SnippetReader snippetReader;

  @Inject
  public GenerateMojo(SpecificationGeneratorFactory specificationGeneratorFactory,
                      SnippetReader snippetReader) {
    super(specificationGeneratorFactory);
    this.snippetReader = snippetReader;
  }

  @Override
  protected List<ResourceModel> getModels() throws MojoExecutionException {
    if (!snippetsDirectory.exists() || !snippetsDirectory.canRead()) {
      throw new MojoExecutionException("Unable to read from snippets directory: " + snippetsDirectory);
    }
    return snippetReader.getModels(snippetsDirectory);
  }
}
