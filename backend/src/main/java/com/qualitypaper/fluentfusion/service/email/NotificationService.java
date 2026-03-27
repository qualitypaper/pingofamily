package com.qualitypaper.fluentfusion.service.email;

import com.qualitypaper.fluentfusion.service.email.template.model.EmailTemplate;
import com.qualitypaper.fluentfusion.service.email.template.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String MESSAGE = "message";

  private final EmailTemplateRepository templateRepository;
  private final EmailService emailService;

  @Value("${microservice.frontend.host}")
  private String frontendUrl;

  public void sendNotification(String[] to, String subject, String customBody) {
    sendNotification(EmailRequest.builder()
            .recipients(to)
            .buttonUrl(null)
            .buttonText(null)
            .subject(subject)
            .message(customBody)
            .templateType(EmailTemplate.TemplateType.NOTIFICATION)
            .build());
  }

  public void sendNotification(EmailRequest request) {
    EmailTemplate template = templateRepository.getTemplate(EmailTemplate.TemplateType.NOTIFICATION);

    Map<String, Object> variables = new HashMap<>(template.getDefaultVariables());
    variables.put("subject", request.getSubject());
    variables.put("customBody", formatCustomBody(request.getMessage()));

    if (request.getButtonText() != null && request.getButtonUrl() != null) {
      variables.put("actionButton", createActionButton(request.getButtonText(), request.getButtonUrl()));
    } else {
      variables.put("actionButton", "");
    }

    request.getVariables().putAll(variables);
    emailService.sendEmail(request);
  }

  public CompletableFuture<Boolean> sendWelcomeEmail(String recipient, String userName) {
    EmailRequest request = EmailRequest.builder()
            .recipients(new String[]{recipient})
            .templateType(EmailTemplate.TemplateType.WELCOME)
            .variable("userName", userName)
            .buttonText("Start learning now")
            .buttonUrl(frontendUrl + "/vocabularies")
            .build();

    return emailService.sendEmail(request);
  }

  public CompletableFuture<Boolean> sendPasswordResetEmail(String recipient, String resetUrl) {
    EmailRequest request = EmailRequest.builder()
            .recipients(new String[]{recipient})
            .templateType(EmailTemplate.TemplateType.PASSWORD_RESET)
            .buttonUrl(resetUrl)
            .buttonText("Reset Password")
            .variable(MESSAGE, "Click the button below to reset your password.")
            .build();

    return emailService.sendEmail(request);
  }

  public CompletableFuture<Boolean> sendVerificationEmail(String recipient, String verificationUrl) {
    EmailRequest request = EmailRequest.builder()
            .recipients(new String[]{recipient})
            .templateType(EmailTemplate.TemplateType.VERIFICATION)
            .buttonUrl(verificationUrl)
            .buttonText("Verify Email")
            .variable(MESSAGE, "Please verify your email address by clicking the button below.")
            .build();

    return emailService.sendEmail(request);
  }

  private String formatCustomBody(String body) {
    if (body == null || body.trim().isEmpty()) {
      return "<p style=\"margin: 0;\">No content provided.</p>";
    }

    // Split by newlines and wrap each line in paragraph tags
    String[] lines = body.split("\\r?\\n");
    StringBuilder formatted = new StringBuilder();

    for (String line : lines) {
      if (!line.trim().isEmpty()) {
        formatted.append("<p style=\"margin: 0 0 16px 0;\">")
                .append(line.trim())
                .append("</p>");
      }
    }

    return formatted.toString();
  }

  private String createActionButton(String buttonText, String buttonUrl) {
    return """
            <tr>
                <td align="left" bgcolor="#ffffff">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                        <tr>
                            <td align="center" bgcolor="#ffffff" style="padding: 12px;">
                                <table border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td align="center" bgcolor="#1a82e2" style="border-radius: 6px;">
                                            <a href="%s" target="_blank" style="display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;">
                                                %s
                                            </a>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            """.formatted(buttonUrl, buttonText);
  }
}
