package com.berkleytechnologyservices.restdocs.mojo;

import com.epages.restdocs.apispec.model.ResourceModel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.apache.maven.plugin.MojoExecutionException;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class SnippetReader {

  private static final String SNIPPET_FILENAME = "resource.json";

  private final ObjectMapper objectMapper;

  public SnippetReader() {
    this(new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .registerModule(new KotlinModule()));
  }

  public SnippetReader(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public List<ResourceModel> getModels(File snippetsDirectory) throws MojoExecutionException {
    try {
      return Files.walk(snippetsDirectory.toPath(), FileVisitOption.FOLLOW_LINKS)
          .filter(this::isResourceFile)
          .map(this::getModel)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to read snippets from " + snippetsDirectory, e);
    }
  }

  private ResourceModel getModel(Path path) {
    try {
      return this.objectMapper.readValue(path.toFile(), ResourceModel.class);
    } catch (IOException e) {
      // TODO: Need a better way to handle this rather than just throwing a RuntimeException
      throw new RuntimeException("Unable to parse snippet file: " + path, e);
    }
  }

  private boolean isResourceFile(Path path) {
    return path.toFile().getName().equals(SNIPPET_FILENAME);
  }
}
