package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.EmailConstants;
import com.example.aditya_resume_backend.core.port.service.IEmailService;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements IEmailService {

    @Value("${spring.mail.noreply-email}")
    private String NO_REPLY_EMAIL;

    @Value("${spring.mail.admin-email}")
    private String ADMIN_EMAIL;

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfig;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, Configuration freemarkerConfig) {
        this.javaMailSender = javaMailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    private String getDateString(LocalDateTime scheduleTime) {
        return String.format("%s, %s-%s-%s",
            scheduleTime.getDayOfWeek().toString(),
            scheduleTime.getDayOfMonth(),
            scheduleTime.getMonth(),
            scheduleTime.getYear()
        );
    }

    private String getTimeString(LocalDateTime scheduleTime) {
        ZoneId istZone = ZoneId.of(EmailConstants.IST);
        ZonedDateTime istTime = scheduleTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(istZone);
        return istTime.toLocalTime().toString();
    }

    @Override
    public void sendMeetRequestEmail(UUID meetingId, ScheduleMeetRequest scheduleMeetRequest) throws IOException, TemplateException, MessagingException {
        Template template = freemarkerConfig.getTemplate(EmailConstants.REQUEST_TEMPLATE_FILE);
        StringWriter writer = new StringWriter();

        Map<String, Object> model = Map.of(
                "meeting_id", meetingId,
                "required_users", scheduleMeetRequest.getRequiredUsers().stream()
                        .map(user -> user.getFirstName() + " " + user.getLastName())
                        .collect(Collectors.joining(", ")),
                "description", scheduleMeetRequest.getDescription(),
                EmailConstants.SCHEDULE_DATE, getDateString(scheduleMeetRequest.getScheduleTime()),
                EmailConstants.SCHEDULE_TIME, getTimeString(scheduleMeetRequest.getScheduleTime())
        );
        template.process(model, writer);
        String htmlBody = writer.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstants.DEFAULT_ENCODING);
        helper.setFrom(NO_REPLY_EMAIL);
        helper.setTo(ADMIN_EMAIL);
        helper.setSubject(EmailConstants.REQUEST_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Override
    public void sendMeetScheduleEmail(List<String> recipients, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException {
        Template template = freemarkerConfig.getTemplate(EmailConstants.INITIATE_TEMPLATE_FILE);
        StringWriter writer = new StringWriter();

        Map<String, Object> model = Map.of(
                EmailConstants.SCHEDULE_DATE, getDateString(scheduleTime),
                EmailConstants.SCHEDULE_TIME, getTimeString(scheduleTime)
        );
        template.process(model, writer);
        String htmlBody = writer.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstants.DEFAULT_ENCODING);
        helper.setFrom(NO_REPLY_EMAIL);
        helper.setTo(recipients.toArray(new String[0]));
        helper.setSubject(EmailConstants.INITIATE_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Override
    public void sendConfirmationEmail(List<String> recipients, String meetLink, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException {
        Template template = freemarkerConfig.getTemplate(EmailConstants.ACCEPT_TEMPLATE_FILE);
        StringWriter writer = new StringWriter();

        Map<String, Object> model = Map.of(
                "meeting_link", meetLink,
                EmailConstants.SCHEDULE_DATE, getDateString(scheduleTime),
                EmailConstants.SCHEDULE_TIME, getTimeString(scheduleTime)
        );
        template.process(model, writer);
        String htmlBody = writer.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstants.DEFAULT_ENCODING);
        helper.setFrom(NO_REPLY_EMAIL);
        helper.setTo(recipients.toArray(new String[0]));
        helper.setSubject(EmailConstants.ACCEPT_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Override
    public void sendRejectionEmail(List<String> recipients, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException {
        Template template = freemarkerConfig.getTemplate(EmailConstants.REJECT_TEMPLATE_FILE);
        StringWriter writer = new StringWriter();

        Map<String, Object> model = Map.of(
                EmailConstants.SCHEDULE_DATE, getDateString(scheduleTime),
                EmailConstants.SCHEDULE_TIME, getTimeString(scheduleTime)
        );
        template.process(model, writer);
        String htmlBody = writer.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstants.DEFAULT_ENCODING);
        helper.setFrom(NO_REPLY_EMAIL);
        helper.setTo(recipients.toArray(new String[0]));
        helper.setSubject(EmailConstants.REJECT_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

}
