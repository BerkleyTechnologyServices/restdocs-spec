package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.model.OpenApiModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.MojoExecutionException;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Named
public class SnippetReader {

  private final ObjectMapper objectMapper;

  public SnippetReader() {
    this(new ObjectMapper());
  }

  public SnippetReader(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public List<OpenApiModel> getModels(File snippetDirectory) throws MojoExecutionException {
    try (DirectoryStream<Path> stream = getModelFiles(snippetDirectory)) {
      return getModels(stream);
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to read snippet files: " + snippetDirectory);
    }
  }

  private DirectoryStream<Path> getModelFiles(File snippetDirectory) throws IOException {
    return Files.newDirectoryStream(snippetDirectory.toPath(), "*.json");
  }

  private List<OpenApiModel> getModels(DirectoryStream<Path> stream) throws MojoExecutionException {
    List<OpenApiModel> models = new ArrayList<>();
    for (Path path : stream) {
      models.add(getModel(path));
    }
    return models;
  }

  private OpenApiModel getModel(Path path) throws MojoExecutionException {
    try {
      return this.objectMapper.readValue(path.toFile(), OpenApiModel.class);
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to parse snippet file: " + path, e);
    }
  }
}
