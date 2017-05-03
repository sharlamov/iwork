package com.orders.services;


import com.dao.model.DataSet;
import com.orders.dao.QueryFactory;
import com.orders.report.bin.ReportGear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;

@Service
@EnableScheduling
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private QueryFactory factory;

    private String username = "serghei.harlamov@transoilcorp.com";

    @Override
    public void sendEmail(String to, String sub, String msgBody, File file) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("IT");
        helper.setTo(to);
        helper.setSubject(sub);
        helper.setText(msgBody);
        helper.addAttachment(file.getName(), file);
        mailSender.send(message);
    }

    @Override
    public void sendEmail(String to, String sub, String msgBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("IT");
        message.setTo(to);
        message.setSubject(sub);
        message.setText(msgBody);
        mailSender.send(message);
    }

    //@Scheduled(cron = "* 59 * * * MON-FRI")
    public void createReport() throws Exception {
        String query = "call libra_actions_pkg.outcomeRaport(:datastart,:dataend,:elevator,:div,:filt2,:filt3,:cb1,:out_sqlHeader,:out_sqlMaster)";

        DataSet params = DataSet.init("datastart", new Date(), "dataend", new Date(), "elevator", 30197, "div", 3343, "filt2", null, "filt3", null, "cb1", null);
        DataSet queries = factory.exec(query, params);
        String sql0 = queries.getString("sqlHeader");
        String sql1 = queries.getString("sqlMaster");


        DataSet ds0 = nvl(sql0, params);
        DataSet ds1 = nvl(sql1, ds0);

        ReportGear rg = new ReportGear();
        File file = rg.createReport(getResFile("templates/outcome.xls"), ds0, ds1);
        sendEmail(username, "Еженедельный отчет", "Test text !!!!!!", file);
        System.out.println("Письмо отправлено!");
    }

    private DataSet nvl(String sql, DataSet params) throws Exception {
        return sql.isEmpty() ? new DataSet() : factory.exec(sql, params);
    }

    private File getResFile(String path) {
        return new File(getClass().getClassLoader().getResource(path).getFile());
    }

    //@Scheduled(fixedDelay = 60000)
}
