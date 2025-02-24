package com.backend.golvia.app.services;

import com.backend.golvia.app.controllers.email.ChrsitmasEmail;
import com.backend.golvia.app.dtos.CustomEmailRequest;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.models.UserInfo;
import com.backend.golvia.app.models.request.EmailDetails;
import com.backend.golvia.app.repositories.auth.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Autowired
    private Environment env;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    private TemplateEngine templateEngine;
    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    public EmailService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }


    public void sendEmailAlerts(EmailDetails emailDetails, String templateName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        Map<String, Object> variables = Map.of(
                "name", emailDetails.getFullName(),
                "link", emailDetails.getLink()

        );
        context.setVariables(variables);

        messageHelper.setFrom(senderEmail);
        messageHelper.setTo(emailDetails.getRecipient());
        messageHelper.setSubject(emailDetails.getSubject());

        String html = templateEngine.process(templateName, context);
        messageHelper.setText(html, true);

        mailSender.send(message);
        // log.info("Sending email: to {}", emailDetails.getRecipient());

    }
    //test purpose
    public void sendWelcomeEmailToSpecificUsers(List<String> emailAddresses) {
        // Fetch users with matching email addresses
        List<User> users = userRepository.findByEmailIn(emailAddresses);

        // Loop through fetched users and send emails
        for (User user : users) {
            try {
                sendWelcomeEmail(user.getEmail(), "Welcome to Golvia!", prepareContext(user));
                System.out.println("Email sent to: " + user.getEmail());
            } catch (MessagingException e) {
                System.err.println("Failed to send email to: " + user.getEmail());
                e.printStackTrace();
            }
        }
    }
    //test purpose
    public void sendWelcomeEmailToAllUsers() {
        // Fetch all users from the database
        List<User> users = userRepository.findAll();

        // Loop through users and send emails
        for (User user : users) {
            try {
                sendWelcomeEmail(user.getEmail(), "Welcome to Golvia!", prepareContext(user));
                System.out.println("Email sent to: " + user.getEmail());
            } catch (MessagingException e) {
                System.err.println("Failed to send email to: " + user.getEmail());
                e.printStackTrace();
            }
        }
    }
    private Context prepareContext(User user) {
        Context context = new Context();
        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        return context;
    }

    public void sendWelcomeEmail(String toEmail, String subject, Context context) throws MessagingException {
        // Prepare the email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());


        // Set email details
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(templateEngine.process("welcome-email", context), true);  // Render Thymeleaf template

        // Send the email
        mailSender.send(message);
    }

    public Context prepareContext(UserInfo userInfo) {
        // Set the context for Thymeleaf template
        Context context = new Context();
        context.setVariable("name", userInfo.getUser().getFirstName() + " " + userInfo.getUser().getLastName());
        return context;
    }

    public void  sendMail(String to, String subjct, String htmlContent, boolean isHtml) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setSubject(subjct);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSentDate(new Date());

        if (isHtml) {
            mimeMessageHelper.setText( htmlContent, true);
        } else {
            mimeMessageHelper.setText(htmlContent);
        }


        mailSender.send(mimeMessage);
    }

    public void sendCustomMail(CustomEmailRequest request){

        if (request.getAllUsers()){
            List<User> users = userRepository.findAll();

            for (User user : users) {
                try {
                    sendMail(user.getEmail(), request.getSubject(), request.getHtmlContent(), true);
                    System.out.println("Email sent to: " + user.getEmail());
                } catch (MessagingException e) {
                    System.err.println("Failed to send email to: " + user.getEmail());
                    e.printStackTrace();
                }
            }
        } else if (request.getSpecifiedUsers()){
            List<String> emails = request.getEmails();

            for (String email : emails) {
                try {
                    sendMail(email, request.getSubject(), request.getHtmlContent(), true);
                    System.out.println("Email sent to: " + email);
                } catch (MessagingException e) {
                    System.err.println("Failed to send email to: " + email);
                    e.printStackTrace();
                }
            }
        } else{
            try {
                sendMail(request.getTo(), request.getSubject(), request.getHtmlContent(), true);
                System.out.println("Email sent to: " + request.getTo());
            } catch (MessagingException e) {
                System.err.println("Failed to send email to: " + request.getTo());
                e.printStackTrace();
            }
        }




    }
}