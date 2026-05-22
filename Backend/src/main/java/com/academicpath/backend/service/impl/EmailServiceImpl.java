package com.academicpath.backend.service.impl;

import com.academicpath.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Academic Path}")
    private String appName;

    @Override
    public void enviarCorreoRecuperacion(String destinatario, String nombres, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(destinatario);
            helper.setSubject("Recuperación de contraseña — " + appName);
            helper.setText(buildHtmlEmail(nombres, resetLink), true);

            mailSender.send(message);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Error al enviar el correo de recuperación: " + e.getMessage());
        }
    }

    private String buildHtmlEmail(String nombres, String resetLink) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
              <title>Recuperación de contraseña</title>
            </head>
            <body style="margin:0;padding:0;background:#080c10;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0"
                     style="background:#080c10;padding:40px 20px;">
                <tr><td align="center">
                  <table width="560" cellpadding="0" cellspacing="0"
                         style="background:#0d1117;border:1px solid rgba(255,255,255,0.08);
                                border-radius:24px;overflow:hidden;max-width:560px;width:100%%;">

                    <!-- ── Header ── -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#0a1a15,#0a1520);
                                 padding:32px 40px;text-align:center;
                                 border-bottom:1px solid rgba(255,255,255,0.06);">
                        <div style="display:inline-flex;align-items:center;justify-content:center;
                                    width:52px;height:52px;background:#00c896;border-radius:14px;
                                    font-size:26px;margin-bottom:14px;line-height:52px;">
                          🎓
                        </div>
                        <h1 style="color:#f0f4f8;font-size:22px;font-weight:800;
                                   margin:0;letter-spacing:-0.02em;">
                          Academic Path
                        </h1>
                      </td>
                    </tr>

                    <!-- ── Body ── -->
                    <tr>
                      <td style="padding:36px 40px;">

                        <h2 style="color:#f0f4f8;font-size:20px;font-weight:700;
                                   margin:0 0 12px;letter-spacing:-0.01em;">
                          Hola, %s 👋
                        </h2>

                        <p style="color:#8b98a8;font-size:15px;line-height:1.65;margin:0 0 28px;">
                          Recibimos una solicitud para restablecer la contraseña de tu cuenta en
                          <strong style="color:#f0f4f8;">Academic Path</strong>.
                          Si no fuiste tú, puedes ignorar este mensaje con total seguridad.
                        </p>

                        <!-- Botón CTA -->
                        <table width="100%%" cellpadding="0" cellspacing="0"
                               style="margin-bottom:28px;">
                          <tr>
                            <td align="center">
                              <a href="%s"
                                 style="display:inline-block;padding:16px 40px;
                                        background:#00c896;color:#080c10;
                                        text-decoration:none;border-radius:99px;
                                        font-weight:800;font-size:15px;
                                        letter-spacing:-0.01em;">
                                🔑&nbsp; Restablecer contraseña
                              </a>
                            </td>
                          </tr>
                        </table>

                        <!-- Info box -->
                        <div style="background:rgba(255,255,255,0.03);
                                    border:1px solid rgba(255,255,255,0.07);
                                    border-radius:14px;padding:20px 22px;margin-bottom:24px;">
                          <p style="color:#8b98a8;font-size:13px;line-height:1.7;margin:0 0 8px;">
                            ⏱ &nbsp;Este enlace expira en
                            <strong style="color:#f0f4f8;">30 minutos</strong>.
                          </p>
                          <p style="color:#8b98a8;font-size:13px;line-height:1.7;margin:0 0 8px;">
                            🔒 &nbsp;El enlace solo puede usarse
                            <strong style="color:#f0f4f8;">una vez</strong>.
                          </p>
                          <p style="color:#8b98a8;font-size:13px;line-height:1.7;margin:0;">
                            🚫 &nbsp;Si no solicitaste este cambio,
                            <strong style="color:#f0f4f8;">ignora este correo</strong>.
                            Tu contraseña no será modificada.
                          </p>
                        </div>

                        <!-- URL de respaldo -->
                        <p style="color:#4e5b6a;font-size:12px;line-height:1.6;
                                  margin:0;word-break:break-all;">
                          Si el botón no funciona, copia y pega este enlace en tu navegador:<br/>
                          <a href="%s"
                             style="color:#00c896;text-decoration:none;">%s</a>
                        </p>

                      </td>
                    </tr>

                    <!-- ── Footer ── -->
                    <tr>
                      <td style="padding:20px 40px;
                                 border-top:1px solid rgba(255,255,255,0.06);
                                 text-align:center;">
                        <p style="color:#4e5b6a;font-size:12px;margin:0;line-height:1.6;">
                          © Academic Path &nbsp;·&nbsp;
                          Este correo fue generado automáticamente, no respondas a él.
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(nombres, resetLink, resetLink, resetLink);
    }
}
