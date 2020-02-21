package com.berkleytechnologyservices.restdocs.spec.generator.test;

import com.epages.restdocs.apispec.model.Attributes;
import com.epages.restdocs.apispec.model.FieldDescriptor;
import com.epages.restdocs.apispec.model.HTTPMethod;
import com.epages.restdocs.apispec.model.HeaderDescriptor;
import com.epages.restdocs.apispec.model.ParameterDescriptor;
import com.epages.restdocs.apispec.model.RequestModel;
import com.epages.restdocs.apispec.model.ResourceModel;
import com.epages.restdocs.apispec.model.ResponseModel;
import com.epages.restdocs.apispec.model.Schema;
import com.epages.restdocs.apispec.model.SecurityRequirements;

import java.util.List;

import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Sets.newLinkedHashSet;

public final class ResourceModels {

  public static ResourceModel resource(String operationId,
                                       String description,
                                       String tag,
                                       RequestModel request,
                                       ResponseModel response) {
    return resource(
        operationId,
        description,
        description,
        tag,
        request,
        response
    );
  }

  public static ResourceModel resource(String operationId,
                                       String summary,
                                       String description,
                                       String tag,
                                       RequestModel request,
                                       ResponseModel response) {
    return resource(
        operationId,
        summary,
        description,
        false,
        false,
        tag,
        request,
        response
    );
  }

  public static ResourceModel resource(String operationId,
                                       String summary,
                                       String description,
                                       boolean privateResource,
                                       boolean deprecated,
                                       String tag,
                                       RequestModel request,
                                       ResponseModel response) {
    return new ResourceModel(
        operationId,
        summary,
        description,
        privateResource,
        deprecated,
        newLinkedHashSet(tag),
        request,
        response
    );
  }

  public static RequestModel request(String path,
                                     HTTPMethod method,
                                     List<ParameterDescriptor> pathParameters,
                                     List<ParameterDescriptor> requestParameters) {
    return request(
        path,
        method,
        null,
        null,
        emptyList(),
        pathParameters,
        requestParameters,
        emptyList(),
        null,
        null
    );
  }

  public static RequestModel request(String path,
                                     HTTPMethod method,
                                     String contentType,
                                     SecurityRequirements securityRequirements,
                                     List<HeaderDescriptor> headers,
                                     List<ParameterDescriptor> pathParameters,
                                     List<ParameterDescriptor> requestParameters,
                                     List<? extends FieldDescriptor> requestFields, String example, Schema schema) {
    return new RequestModel(
        path,
        method,
        contentType,
        securityRequirements,
        headers,
        pathParameters,
        requestParameters,
        requestFields,
        example,
        schema
    );
  }

  public static ResponseModel response(int status, String contentType, List<FieldDescriptor> fields) {
    return response(status, contentType, emptyList(), fields);
  }

  public static ResponseModel response(int status,
                                       String contentType,
                                       List<HeaderDescriptor> headers,
                                       List<FieldDescriptor> fields) {
    return response(status, contentType, headers, fields, null, null);
  }

  public static ResponseModel response(int status,
                                       String contentType,
                                       List<HeaderDescriptor> headers,
                                       List<FieldDescriptor> fields,
                                       String example,
                                       Schema schema) {
    return new ResponseModel(status, contentType, headers, fields, example, schema);
  }

  public static FieldDescriptor field(String path, String description, String type) {
    return new FieldDescriptor(path, description, type, false, false, new Attributes());
  }

  public static ParameterDescriptor requiredParam(String name, String description, String type) {
    return param(name, description, type, false);
  }

  public static ParameterDescriptor param(String name, String description, String type) {
    return param(name, description, type, true);
  }

  public static ParameterDescriptor param(String name, String description, String type, boolean optional) {
    return new ParameterDescriptor(name, description, type, optional, false, new Attributes());
  }
}
