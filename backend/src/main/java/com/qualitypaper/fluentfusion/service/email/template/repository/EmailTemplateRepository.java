package com.qualitypaper.fluentfusion.service.email.template.repository;

import com.qualitypaper.fluentfusion.service.email.template.model.EmailTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class EmailTemplateRepository {

  @Value("${microservice.frontend.host}")
  private String frontendUrl;

  @Value("${app.name}")
  private String appName;

  private final Map<String, EmailTemplate> templates = new HashMap<>();

  @PostConstruct
  public void init() {
    initializeTemplates();
  }

  public EmailTemplate getTemplate(EmailTemplate.TemplateType templateType) {
    return templates.get(templateType.getTemplateName());
  }

  private void initializeTemplates() {
    templates.put("notification", EmailTemplate.builder()
            .name("notification")
            .subject("${subject}")
            .htmlContent(getNotificationTemplate())
            .defaultVariables(getDefaultVariables())
            .build());

    // Welcome email template
    templates.put("welcome", EmailTemplate.builder()
            .name("welcome")
            .subject("Welcome to ${appName}")
            .htmlContent(getWelcomeTemplate())
            .defaultVariables(getDefaultVariables())
            .build());

    // Password reset template
    templates.put("password-reset", EmailTemplate.builder()
            .name("password-reset")
            .subject("Reset Your Password - ${appName}")
            .htmlContent(getPasswordResetTemplate())
            .defaultVariables(getDefaultVariables())
            .build());

    // Verification template
    templates.put("verification", EmailTemplate.builder()
            .name("verification")
            .subject("Verify Your Email - ${appName}")
            .htmlContent(getVerificationTemplate())
            .defaultVariables(getDefaultVariables())
            .build());
  }

  private Map<String, Object> getDefaultVariables() {
    Map<String, Object> variables = new HashMap<>();
    variables.put("appName", appName);
    variables.put("frontendUrl", frontendUrl);
    variables.put("logoUrl", frontendUrl + "/logo.png");
    variables.put("currentYear", String.valueOf(java.time.Year.now().getValue()));
    return variables;
  }

  // Add this method to EmailTemplateRepository
  private String getNotificationTemplate() {
    return """
            ${header}
            ${hero}
            <tr>
                <td align="center" bgcolor="#e9ecef">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                        <tr>
                            <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                                ${customBody}
                            </td>
                        </tr>
                        ${actionButton}
                    </table>
                </td>
            </tr>
            ${footer}
            """;
  }


  private String getWelcomeTemplate() {
    return """
            ${header}
            ${hero}
            
            <tr>
                <td align="center" bgcolor="#e9ecef">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                        <tr>
                            <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                                <p class="greeting">Hello ${userName},</p>
                                <p class="lead">Welcome to out platform — we are pleased to have you with us.</p>
                                <p class="lead">Learning a new language is a meaningful investment. With our structured lessons, tailored practice, and progress tracking, you will build confidence and measurable results.</p>
                                <ol class="steps">
                                  <li class="step">Log in and set your learning goals.</li>
                                  <li class="step">Complete your first lesson to establish momentum.</li>
                                  <li class="step">Monitor your progress and adjust your schedule as needed.</li>
                                </ol>
                                <p style="margin:18px 0 22px 0;">Every word you learn moves you closer to fluency. We are committed to supporting your progress at every step.</p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            ${footer}
            """;
  }

  private String getPasswordResetTemplate() {
    return """
            ${header}
            ${hero}
            <tr>
                <td align="center" bgcolor="#e9ecef">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                        <tr>
                            <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                                <p style="margin: 0;">${message}</p>
                            </td>
                        </tr>
                        ${button}
                        <tr>
                            <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                                <p style="margin: 0;">This link will expire in 24 hours for security reasons.</p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            ${footer}
            """;
  }

  private String getVerificationTemplate() {
    return """
            ${header}
            ${hero}
            <tr>
                <td align="center" bgcolor="#e9ecef">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                        <tr>
                            <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                                <p style="margin: 0;">${message}</p>
                            </td>
                        </tr>
                        ${button}
                    </table>
                </td>
            </tr>
            ${footer}
            """;
  }
}