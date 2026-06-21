package com.gucardev.springreactboilerplate.infra.config.mail;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Sends HTML emails rendered from Thymeleaf templates under {@code resources/templates/email/}.
 * Delivery is asynchronous (the {@code asyncExecutor}) so request threads never block on SMTP.
 *
 * <p>Outbound mail is gated by {@code app.mail.enabled}; when it is off, or when no SMTP host is
 * configured (no {@link JavaMailSender} bean), the message is logged and skipped instead of sent —
 * the app runs fine without mail configured. Templates still render the same way in every env.
 */
@Slf4j
@Service
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final TemplateEngine templateEngine;
    private final boolean enabled;
    private final String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        TemplateEngine templateEngine,
                        @Value("${app.mail.enabled:false}") boolean enabled,
                        @Value("${app.mail.from:no-reply@example.com}") String from) {
        this.mailSenderProvider = mailSenderProvider;
        this.templateEngine = templateEngine;
        this.enabled = enabled;
        this.from = from;
    }

    /**
     * Render {@code templates/email/<template>.html} with {@code variables} and send it to {@code to}.
     * Runs on the async executor; failures are logged, never propagated to the caller.
     */
    @Async("asyncExecutor")
    public void sendHtml(String to, String subject, String template, Map<String, Object> variables) {
        String html = render(template, variables);

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (!enabled || mailSender == null) {
            log.info("[MAIL] (disabled) to={} subject='{}' template='{}' — not sent", to, subject, template);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("[MAIL] sent to={} subject='{}'", to, subject);
        } catch (Exception e) {
            log.error("[MAIL] failed to send to={} subject='{}'", to, subject, e);
        }
    }

    /** Renders an email template to HTML (exposed for testing and previews). */
    public String render(String template, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }
        return templateEngine.process("email/" + template, context);
    }
}
