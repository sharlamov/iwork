package com.orders.services;

import javax.mail.MessagingException;
import java.io.File;

public interface MailService {

    void sendEmail(String to, String sub, String msgBody, File file) throws MessagingException;

    void sendEmail(String to, String sub, String msgBody);

    void sendEmail(String to, String msg);

    void createReport() throws Exception;

    void dbLog(String e);
}
