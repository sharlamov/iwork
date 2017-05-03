package com.orders.services;

import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

public interface MailService {

    void sendEmail(String to, String sub, String msgBody, File file) throws MessagingException;

    void sendEmail(String to, String sub, String msgBody);

    void createReport() throws Exception;

}
