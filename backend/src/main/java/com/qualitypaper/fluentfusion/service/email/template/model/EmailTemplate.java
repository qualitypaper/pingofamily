package com.qualitypaper.fluentfusion.service.email.template.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
@Builder
public class EmailTemplate {
  private String name;
  private String subject;
  private String htmlContent;
  private String textContent;
  private Map<String, Object> defaultVariables;

  @Getter
  public enum TemplateType {
    WELCOME("welcome"),
    PASSWORD_RESET("password-reset"),
    VERIFICATION("verification"),
    NOTIFICATION("notification");

    private final String templateName;

    TemplateType(String templateName) {
      this.templateName = templateName;
    }

  }
}
