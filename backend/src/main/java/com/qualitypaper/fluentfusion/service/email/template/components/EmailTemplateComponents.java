package com.qualitypaper.fluentfusion.service.email.template.components;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateComponents {

  public String getHeader() {
    return """
            <html>
            <head>
                <meta charset="utf-8">
                <meta http-equiv="x-ua-compatible" content="ie=edge">
                <title>${title}</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                ${styles}
            </head>
            <body style="background-color: #e9ecef;">
                <div class="preheader" style="display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;">
                    ${subject}
                </div>
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td align="center" bgcolor="#e9ecef">
                            <table border="0" cellpadding="10" cellspacing="0" width="100%" style="max-width: 600px;">
                                <tr>
                                    <td style="padding: 24px 0; vertical-align: bottom; text-align: end; width: 40%;">
                                        <a href="${frontendUrl}" target="_blank">
                                            <img src="${logoUrl}" alt="Logo" border="0" width="48" style="border-radius: 5px;">
                                        </a>
                                    </td>
                                    <td style="text-align: start;">
                                        <a href="${frontendUrl}" target="_blank" style="text-decoration: none; width: 100%;">
                                            <span style="text-transform: capitalize; color: #1a82e2; font-size:1.5rem; font-weight: bold;">${appName}</span>
                                        </a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
            """;
  }

  public String getHero() {
    return """
            <tr>
                <td align="center" bgcolor="#e9ecef">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                        <tr>
                            <td align="left" bgcolor="#ffffff" style="padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;">
                                <h1 style="margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;">${subject}</h1>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            """;
  }

  public String getButton() {
    return """
            <tr>
                <td align="left" bgcolor="#ffffff">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
                        <tr>
                            <td align="center" bgcolor="#ffffff" style="padding: 12px;">
                                <table border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td align="center" bgcolor="#1a82e2" style="border-radius: 6px;">
                                            <a href="${buttonUrl}" target="_blank" style="display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;">
                                                ${buttonText}
                                            </a>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                    <p style="margin: 0;">If the button doesn't work, copy and paste the following link:</p>
                    <p style="margin: 0;"><a href="${buttonUrl}" target="_blank">${buttonUrl}</a></p>
                </td>
            </tr>
            """;
  }

  public String getFooter() {
    return """
            <tr>
                <td align="center" bgcolor="#e9ecef" style="padding: 24px;">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                        <tr>
                            <td align="center" bgcolor="#e9ecef" style="padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;">
                                <p style="margin: 0;">You received this email from <a href="${frontendUrl}">${appName}</a>.</p>
                                <p style="margin: 8px 0 0;">© ${currentYear} ${appName}. All rights reserved.</p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            </table>
            </body>
            </html>
            """;
  }

  public String getStyles() {
    return """
            <style type="text/css">
            @media screen {
                @font-face {
                    font-family: 'Source Sans Pro';
                    font-style: normal;
                    font-weight: 400;
                    src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');
                }
                @font-face {
                    font-family: 'Source Sans Pro';
                    font-style: normal;
                    font-weight: 700;
                    src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');
                }
            }
            body, table, td, a { -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; }
            table, td { mso-table-rspace: 0pt; mso-table-lspace: 0pt; }
            img { -ms-interpolation-mode: bicubic; }
            a[x-apple-data-detectors] { font-family: inherit !important; font-size: inherit !important; font-weight: inherit !important; line-height: inherit !important; color: inherit !important; text-decoration: none !important; }
            div[style*="margin: 16px 0;"] { margin: 0 !important; }
            body { width: 100% !important; height: 100% !important; padding: 0 !important; margin: 0 !important; }
            table { border-collapse: collapse !important; }
            a { color: #1a82e2; }
            img { height: auto; line-height: 100%; text-decoration: none; border: 0; outline: none; }
            </style>
            """;
  }
}
