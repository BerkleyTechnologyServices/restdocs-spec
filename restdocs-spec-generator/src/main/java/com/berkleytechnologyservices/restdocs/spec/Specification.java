package com.berkleytechnologyservices.restdocs.spec;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum Specification {
  OPENAPI_V2("openapi-2.0", SpecificationFormat.YAML, SpecificationFormat.JSON);

  private final String defaultFilename;
  private final SpecificationFormat defaultFormat;
  private final Set<SpecificationFormat> formats;

  Specification(String defaultFilename, SpecificationFormat defaultFormat, SpecificationFormat ... formats) {
    this.defaultFilename = defaultFilename;
    this.defaultFormat = defaultFormat;
    this.formats = createFormatsSet(defaultFormat, formats);
  }

  public String getDefaultFilename() {
    return defaultFilename;
  }

  public Set<SpecificationFormat> getSupportedFormats() {
    return formats;
  }

  public boolean supportsFormat(SpecificationFormat format) {
    return this.formats.contains(format);
  }

  public SpecificationFormat getDefaultFormat() {
    return this.defaultFormat;
  }

  private static Set<SpecificationFormat> createFormatsSet(SpecificationFormat defaultFormat, SpecificationFormat ... formats) {
    Set<SpecificationFormat> formatSet = new HashSet<>();
    formatSet.add(defaultFormat);
    formatSet.addAll(Arrays.asList(formats));
    return Collections.unmodifiableSet(formatSet);
  }
}
