package com.jogging.tracker.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@EnableAsync
@Slf4j
public class MailSenderUtil {

    private final JavaMailSender mailSender;
    private final String senderMail;
    private final String applicationDomain;
    private final String verifyMailTemplate;
    private final String inviteMailTemplate;

    public MailSenderUtil(JavaMailSender mailSender,
                          @Value("${spring.mail.username}") String senderMail,
                          @Value("${application.domain}") String applicationDomain,
                          @Value("classpath:templates/mail_template_verify_account.html") Resource verifyMailTemplate,
                          @Value("classpath:templates/mail_template_invite.html") Resource inviteMailTemplate) {
        this.mailSender = mailSender;
        this.senderMail = senderMail;
        this.applicationDomain = applicationDomain;
        this.verifyMailTemplate = resourceToString(verifyMailTemplate);
        this.inviteMailTemplate = resourceToString(inviteMailTemplate);
    }

    @Async
    public void sendVerifyMail(String to, String verifyToken) {
        if (verifyMailTemplate != null) {
            sendInternal(
                    to,
                    "Verify Your Account",
                    verifyMailTemplate.replace("##{token}##", verifyToken).replace("##{domain}##", applicationDomain),
                    true
            );

        } else {
            log.error("Could not load mail body");
        }
    }

    @Async
    public void sendInviteMail(String to, String inviteToken) {
        if (inviteMailTemplate != null) {
            sendInternal(
                    to,
                    "Come on Join us!",
                    inviteMailTemplate
                            .replace("##{domain}##", applicationDomain)
                            .replace("##{email}##", to)
                            .replace("##{inviteToken}##", inviteToken),
                    true
            );


        } else {
            log.error("Could not load mail body");
        }
    }

    private void sendInternal(String to, String subject, String body, boolean isHtml) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(senderMail);
            helper.setText(body, isHtml);
            helper.setTo(to);
            helper.setSubject(subject);
            mailSender.send(mimeMessage);

            log.info("Message successfully sent: {}", to);
        } catch (Exception e) {
            log.error("Message sending failed to: {}", to, e);
        }
    }

    private String resourceToString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            log.error("Failed to load: {}", resource.getFilename(), e);
            return null;
        }
    }
}