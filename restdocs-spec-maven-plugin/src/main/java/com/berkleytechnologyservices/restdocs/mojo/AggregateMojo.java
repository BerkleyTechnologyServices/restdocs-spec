package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorFactory;
import com.epages.restdocs.apispec.model.ResourceModel;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This mojo generates a single api specification by aggregating snippet files from all child projects.
 */
@Named
@Mojo(name = "aggregate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, aggregator = true, threadSafe = true)
public class AggregateMojo extends AbstractGenerateMojo {

  /**
   * Directories to look for Restdocs snippets. Defaults to <code>target/generated-snippets</code>.
   */
  @Parameter
  private List<String> snippetDirectories;

  @Parameter(property = "reactorProjects", readonly = true)
  private List<MavenProject> reactorProjects;

  private final SnippetReader snippetReader;

  @Inject
  public AggregateMojo(SpecificationGeneratorFactory specificationGeneratorFactory,
                       SnippetReader snippetReader) {
    super(specificationGeneratorFactory);
    this.snippetReader = snippetReader;
  }

  @Override
  protected List<ResourceModel> getModels() throws MojoExecutionException {
    List<ResourceModel> snippets = new ArrayList<>();
    for (MavenProject childProject : reactorProjects) {
      List<File> childSnippetDirectories;
      if (snippetDirectories == null) {
        childSnippetDirectories = getDefaultSnippetDirectories(childProject);
      } else {
        childSnippetDirectories = snippetDirectories.stream()
          .map(path -> new File(childProject.getBasedir(), path))
          .filter(snippetsDirectory -> snippetsDirectory.exists() && snippetsDirectory.canRead())
          .collect(Collectors.toList());
      }

      for (File snippetsDirectory : childSnippetDirectories) {
        snippets.addAll(snippetReader.getModels(snippetsDirectory));
      }
    }
    return snippets;
  }

  private List<File> getDefaultSnippetDirectories(MavenProject project) {
    File defaultSnippetDirectory = new File(project.getBuild().getDirectory(), "generated-snippets");
    return isValidDirectory(defaultSnippetDirectory) ? Collections.singletonList(defaultSnippetDirectory) : Collections.emptyList();
  }

  private static boolean isValidDirectory(File directory) {
    return directory.exists() && directory.canRead();
  }
}
