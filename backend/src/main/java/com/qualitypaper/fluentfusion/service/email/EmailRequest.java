package com.qualitypaper.fluentfusion.service.email;

import com.qualitypaper.fluentfusion.service.email.template.model.EmailTemplate;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class EmailRequest {
  private String[] recipients;
  private String subject;
  private EmailTemplate.TemplateType templateType;
  private Map<String, Object> variables;
  private String buttonUrl;
  private String buttonText;
  private String message;

  public static class EmailRequestBuilder {
    private Map<String, Object> variables = new HashMap<>();

    public EmailRequestBuilder variable(String key, Object value) {
      this.variables.put(key, value);
      return this;
    }

    public EmailRequestBuilder variables(Map<String, Object> variables) {
      this.variables.putAll(variables);
      return this;
    }
  }
}
