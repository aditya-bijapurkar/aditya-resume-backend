package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.constants.EmailConstants;
import com.example.aditya_resume_backend.core.port.dto.NameEmailDTO;
import com.example.aditya_resume_backend.core.port.dto.UserDTO;
import com.example.aditya_resume_backend.core.port.service.IEmailService;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements IEmailService {

    @Value("${spring.mail.noreply-email}")
    private String noreplyEmail;
    @Value("${spring.mail.admin-email}")
    private String adminEmail;

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
        ZoneId istZone = ZoneId.of(ApplicationConstants.IST);
        ZonedDateTime istTime = scheduleTime.atZone(istZone);
        return istTime.toLocalTime().toString();
    }

    @Async
    @Override
    public void sendMeetRequestEmail(UUID meetingId, ScheduleMeetRequest scheduleMeetRequest) throws IOException, TemplateException, MessagingException {
        Template template = freemarkerConfig.getTemplate(EmailConstants.REQUEST_TEMPLATE_FILE);
        StringWriter writer = new StringWriter();

        Map<String, Object> model = Map.of(
                "meeting_id", meetingId,
                "required_users", scheduleMeetRequest.getRequiredUsers().stream()
                        .map(user -> user.getFirstName() + " " + user.getLastName())
                        .collect(Collectors.joining(", ")),
                "email_ids", scheduleMeetRequest.getRequiredUsers().stream()
                        .map(UserDTO::getEmailId)
                        .collect(Collectors.joining(", ")),
                "description", scheduleMeetRequest.getDescription(),
                EmailConstants.SCHEDULE_DATE, getDateString(scheduleMeetRequest.getScheduleTime()),
                EmailConstants.SCHEDULE_TIME, getTimeString(scheduleMeetRequest.getScheduleTime())
        );
        template.process(model, writer);
        String htmlBody = writer.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstants.DEFAULT_ENCODING);
        helper.setFrom(noreplyEmail);
        helper.setTo(adminEmail);
        helper.setSubject(EmailConstants.REQUEST_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Async
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
        helper.setFrom(noreplyEmail);
        helper.setTo(recipients.toArray(new String[0]));
        helper.setSubject(EmailConstants.INITIATE_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Async
    @Override
    public void sendConfirmationEmail(List<NameEmailDTO> recipients, String meetLink, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException {
        recipients.add(NameEmailDTO.builder().emailId(adminEmail).build());

        List<String> recipientsEmails = recipients.stream().map(NameEmailDTO::getEmailId).toList();
        List<String> recipientsNames = recipients.stream().map(NameEmailDTO::getFirstName).toList();

        Template template = freemarkerConfig.getTemplate(EmailConstants.ACCEPT_TEMPLATE_FILE);
        StringWriter writer = new StringWriter();

        Map<String, Object> model = Map.of(
                "meeting_link", meetLink,
                "participants", recipientsNames.stream().filter(Objects::nonNull).collect(Collectors.joining(",")),
                EmailConstants.SCHEDULE_DATE, getDateString(scheduleTime),
                EmailConstants.SCHEDULE_TIME, getTimeString(scheduleTime)
        );
        template.process(model, writer);
        String htmlBody = writer.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstants.DEFAULT_ENCODING);
        helper.setFrom(noreplyEmail);
        helper.setTo(recipientsEmails.toArray(new String[0]));
        helper.setSubject(EmailConstants.ACCEPT_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Async
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
        helper.setFrom(noreplyEmail);
        helper.setTo(recipients.toArray(new String[0]));
        helper.setSubject(EmailConstants.REJECT_SUBJECT);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Async
    @Override
    public void sendSimpleMail(String toEmail, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(noreplyEmail);
        mailMessage.setTo(toEmail != null ? toEmail : adminEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        javaMailSender.send(mailMessage);
    }

}
