package com.qualitypaper.fluentfusion.service.email;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.qualitypaper.fluentfusion.service.email.template.TemplateEngine;
import com.qualitypaper.fluentfusion.service.email.template.components.EmailTemplateComponents;
import com.qualitypaper.fluentfusion.service.email.template.model.EmailTemplate;
import com.qualitypaper.fluentfusion.service.email.template.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final TemplateEngine templateEngine;
  private final EmailTemplateRepository templateRepository;
  private final EmailTemplateComponents templateComponents;

  @Value("${app.name}")
  private String appName;

  @Value("${service.mailersend.api-key}")
  private String mailerSendToken;

  @Async
  public CompletableFuture<Boolean> sendEmail(EmailRequest emailRequest) {
    try {
      EmailTemplate template = templateRepository.getTemplate(emailRequest.getTemplateType());

      if (template == null) {
        log.error("Template not found: {}", emailRequest.getTemplateType());
        return CompletableFuture.completedFuture(false);
      }

      Map<String, Object> subjectVariables = template.getDefaultVariables();
      subjectVariables.putAll(emailRequest.getVariables());

      String processedSubject = templateEngine.process(template.getSubject(), subjectVariables);
      template.setSubject(processedSubject);
      String processedHtml = buildEmailContent(template, emailRequest);

      Email email = buildEmail(emailRequest.getRecipients(), processedSubject, processedHtml);

      return sendEmailAsync(email);
    } catch (Exception e) {
      log.error("Error sending email: ", e);
      return CompletableFuture.completedFuture(false);
    }
  }

  private String buildEmailContent(EmailTemplate template, EmailRequest request) {
    Map<String, Object> variables = new HashMap<>(template.getDefaultVariables());
    variables.putAll(request.getVariables());

    // Add component templates to variables
    variables.put("subject", template.getSubject());
    variables.put("styles", templateComponents.getStyles());
    variables.put("header", templateEngine.process(templateComponents.getHeader(), variables));
    variables.put("hero", templateEngine.process(templateComponents.getHero(), variables));
    variables.put("footer", templateEngine.process(templateComponents.getFooter(), variables));

    if (request.getButtonUrl() != null) {
      variables.put("buttonUrl", request.getButtonUrl());
      variables.put("buttonText", request.getButtonText() != null ? request.getButtonText() : "Click Here");
      variables.put("button", templateEngine.process(templateComponents.getButton(), variables));
    } else {
      variables.put("button", "");
    }

    return templateEngine.process(template.getHtmlContent(), variables);
  }

  private Email buildEmail(String[] recipients, String subject, String htmlContent) {
    Email email = new Email();
    email.setFrom(appName, "noreply@pingo.family");

    for (String recipient : recipients) {
      email.addRecipient("User", recipient);
    }

    email.setSubject(subject);
    email.setHtml(htmlContent);

    return email;
  }

  private CompletableFuture<Boolean> sendEmailAsync(Email email) {
    MailerSend mailerSend = new MailerSend();
    mailerSend.setToken(mailerSendToken);

    try {
      MailerSendResponse response = mailerSend.emails().send(email);
      log.info("Email sent successfully. Message ID: {}", response.messageId);
      return CompletableFuture.completedFuture(true);
    } catch (MailerSendException e) {
      log.error("Failed to send email: {}", e.getMessage());
      return CompletableFuture.completedFuture(false);
    }
  }
}
