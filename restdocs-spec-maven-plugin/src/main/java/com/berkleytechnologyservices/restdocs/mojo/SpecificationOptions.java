package com.berkleytechnologyservices.restdocs.mojo;

import com.berkleytechnologyservices.restdocs.spec.Specification;
import com.berkleytechnologyservices.restdocs.spec.SpecificationFormat;

public class SpecificationOptions {

  private Specification type;
  private SpecificationFormat format;
  private String filename;

  public Specification getType() {
    return this.type;
  }

  public void setType(Specification type) {
    this.type = type;
  }

  public SpecificationFormat getFormat() {
    return this.format != null ? this.format : this.type.getDefaultFormat();
  }

  public void setFormat(SpecificationFormat format) {
    this.format = format;
  }

  public String getFilename() {
    return this.filename != null ? this.filename : this.type.getDefaultFilename();
  }

  public String getPublicFilename() {
    return this.getFilename() + "-public";
  }

  public String getFilenameWithExtension() {
    return this.getFilename() + this.getFormat().getExtension();
  }

  public String getPublicFilenameWithExtension() {
    return this.getPublicFilename() + this.getFormat().getExtension();
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
}
