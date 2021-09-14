package com.berkleytechnologyservices.restdocs.spec.generator.postman;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGenerator;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorException;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorUtils;
import com.epages.restdocs.apispec.model.ResourceModel;
import com.epages.restdocs.apispec.postman.PostmanCollectionGenerator;
import com.epages.restdocs.apispec.postman.model.Collection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

import javax.inject.Named;
import java.net.MalformedURLException;
import java.util.List;

@Named
public class PostmanCollectionSpecificationGenerator implements SpecificationGenerator {

  private final PostmanCollectionGenerator generator;
  private final ObjectMapper objectMapper;

  public PostmanCollectionSpecificationGenerator() {
    this(PostmanCollectionGenerator.INSTANCE, createObjectMapper());
  }

  public PostmanCollectionSpecificationGenerator(PostmanCollectionGenerator generator, ObjectMapper objectMapper) {
    this.generator = generator;
    this.objectMapper = objectMapper;
  }

  @Override
  public Specification getSpecification() {
    return Specification.POSTMAN_COLLECTION;
  }

  @Override
  public String generate(ApiDetails details, List<ResourceModel> models) throws SpecificationGeneratorException {

    Collection collection = generator.generate(
      models,
      details.getName(),
      details.getVersion(),
      createBaseUrl(details)
    );

    try {
      return objectMapper.writeValueAsString(collection);
    } catch (JsonProcessingException e) {
      throw new SpecificationGeneratorException("Unable to generate Postman Collection specification.", e);
    }
  }

  private String createBaseUrl(ApiDetails details) throws SpecificationGeneratorException {
    if (details.getSchemes().isEmpty()) {
      throw new SpecificationGeneratorException("You must define a scheme in order to generate a Postman Collection specification.");
    }

    try {
      return SpecificationGeneratorUtils.createBaseUrl(details.getSchemes().get(0), details.getHosts().iterator().next(), details.getBasePath()).toString();
    } catch (MalformedURLException e) {
      throw new SpecificationGeneratorException("Unable to build base url.", e);
    }
  }

  private static ObjectMapper createObjectMapper() {
    return new ObjectMapper()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .enable(SerializationFeature.INDENT_OUTPUT)
      .registerModule(new KotlinModule());
  }

}
