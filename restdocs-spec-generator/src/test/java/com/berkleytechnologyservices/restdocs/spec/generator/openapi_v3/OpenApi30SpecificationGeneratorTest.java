package com.berkleytechnologyservices.restdocs.spec.generator.openapi_v3;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.Contact;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.generator.SpecificationGeneratorException;
import com.epages.restdocs.apispec.model.HTTPMethod;
import com.epages.restdocs.apispec.model.ResourceModel;
import com.epages.restdocs.apispec.model.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.berkleytechnologyservices.restdocs.spec.generator.test.ResourceModels.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.list;

@ExtendWith(MockitoExtension.class)
public class OpenApi30SpecificationGeneratorTest {

  private final OpenApi30SpecificationGenerator generator = new OpenApi30SpecificationGenerator();

  @Test
  public void testGetSpecification() {
    assertThat(generator.getSpecification()).isEqualTo(Specification.OPENAPI_V3);
  }

  @Test
  public void testGenerateWithDefaults() throws SpecificationGeneratorException {

    ApiDetails apiDetails = new ApiDetails();


    String rawOutput = generator.generate(apiDetails, list(getMockResource()));

    assertThat(rawOutput)
            .isEqualToNormalizingNewlines(contentOfResource("/mock-specs/openapi3/default-settings.yml"));
  }

  @Test
  public void testGenerateHostWithPort() throws SpecificationGeneratorException {

    ApiDetails apiDetails = new ApiDetails().hosts(Collections.singleton("example.com:8080"));

    String rawOutput = generator.generate(apiDetails, list(getMockResource()));

    assertThat(rawOutput)
      .isEqualToNormalizingNewlines(contentOfResource("/mock-specs/openapi3/host-with-port.yml"));
  }

  @Test
  public void testGenerateContact() throws SpecificationGeneratorException {
    Contact contact = new Contact();
    contact.setName("John Doe");
    contact.setEmail("john@example.com");
    contact.setUrl("https://john.example.com");

    ApiDetails apiDetails = new ApiDetails().contact(contact);

    String rawOutput = generator.generate(apiDetails, list(getMockResource()));

    assertThat(rawOutput)
        .isEqualToNormalizingNewlines(contentOfResource("/mock-specs/openapi3/contact.yml"));
  }

  private ResourceModel getMockResource() {
    return resource(
      "book-get",
      "Get a book by id",
      "book",
      request(
        "/book/{id}",
        HTTPMethod.GET,
        list(
          requiredParam("id", "The unique identifier for the book.", "NUMBER")
        ),
        emptyList()
      ),
      response(
        200,
        "application/hal+json",
        list(),
        list(
          field("title", "Title of the book", "STRING"),
          field("author", "Author of the book", "STRING"),
          field("pages", "Number of pages in the book", "NUMBER")
        ),
        "The example response.",
        new Schema("MyCustomSchemaName")

      )
    );
  }

  private static String contentOfResource(String resourceName) {
    return contentOf(OpenApi30SpecificationGeneratorTest.class.getResource(resourceName));
  }
}