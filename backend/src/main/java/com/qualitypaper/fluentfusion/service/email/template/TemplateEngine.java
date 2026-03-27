package com.qualitypaper.fluentfusion.service.email.template;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TemplateEngine {
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([\\w.]+)}");

  public String process(String template, Map<String, Object> variables) {
    if (template == null || variables == null) {
      return template;
    }

    Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
    StringBuilder result = new StringBuilder();

    while (matcher.find()) {
      String key = matcher.group(1);
      String replacement = resolveVariable(key, variables);
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
    }

    matcher.appendTail(result);
    return result.toString();
  }

  private String resolveVariable(String key, Map<String, Object> variables) {
    Object value = variables.get(key);
    return value != null ? value.toString() : "";
  }
}
