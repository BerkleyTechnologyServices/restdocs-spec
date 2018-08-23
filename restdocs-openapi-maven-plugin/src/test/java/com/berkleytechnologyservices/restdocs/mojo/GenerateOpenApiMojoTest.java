package com.berkleytechnologyservices.restdocs.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenerateOpenApiMojoTest {

  @InjectMocks
  private GenerateOpenApiMojo mojo;

  @Test
  public void testSomething() throws MojoExecutionException {
    mojo.execute();
  }
}

