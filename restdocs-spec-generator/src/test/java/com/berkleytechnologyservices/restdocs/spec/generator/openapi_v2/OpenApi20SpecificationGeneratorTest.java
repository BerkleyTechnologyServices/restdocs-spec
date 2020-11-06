package com.berkleytechnologyservices.restdocs.spec.generator.openapi_v2;

import com.berkleytechnologyservices.restdocs.spec.ApiDetails;
import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.epages.restdocs.apispec.model.HTTPMethod;
import com.epages.restdocs.apispec.model.ResourceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.berkleytechnologyservices.restdocs.spec.generator.test.ResourceModels.field;
import static com.berkleytechnologyservices.restdocs.spec.generator.test.ResourceModels.request;
import static com.berkleytechnologyservices.restdocs.spec.generator.test.ResourceModels.requiredParam;
import static com.berkleytechnologyservices.restdocs.spec.generator.test.ResourceModels.resource;
import static com.berkleytechnologyservices.restdocs.spec.generator.test.ResourceModels.response;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.list;

@ExtendWith(MockitoExtension.class)
public class OpenApi20SpecificationGeneratorTest {

  private final OpenApi20SpecificationGenerator generator = new OpenApi20SpecificationGenerator();

  @Test
  public void testGetSpecification() {
    assertThat(generator.getSpecification()).isEqualTo(Specification.OPENAPI_V2);
  }

  @Test
  public void testGenerateWithDefaults() {

    ApiDetails apiDetails = new ApiDetails();

    ResourceModel model = resource(
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
            list(
                field("title", "Title of the book", "STRING"),
                field("author", "Author of the book", "STRING"),
                field("pages", "Number of pages in the book", "NUMBER")
            )
        )
    );

    String rawOutput = generator.generate(apiDetails, list(model));

    assertThat(rawOutput)
        .isEqualToNormalizingNewlines(contentOfResource("/mock-specs/openapi2/default-settings.yml"));
  }

  private static String contentOfResource(String resourceName) {
    return contentOf(OpenApi20SpecificationGeneratorTest.class.getResource(resourceName));
  }
}