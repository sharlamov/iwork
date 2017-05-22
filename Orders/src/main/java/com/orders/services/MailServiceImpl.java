package com.orders.services;


import com.dao.model.DataSet;
import com.orders.dao.QueryFactory;
import com.orders.model.CustomUser;
import com.orders.report.bin.ReportGear;
import com.orders.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Service
@EnableScheduling
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private QueryFactory factory;

    private String username = "anna.scolscaia@transoilcorp.com,serghei.harlamov@transoilcorp.com,cfo@transoilcorp.com";

    private ReportGear reporter = new ReportGear();


    @Override
    public void sendEmail(String to, String sub, String msgBody, File file) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to.split(","));
            helper.setSubject(sub);
            helper.setText(msgBody);
            helper.addAttachment(file.getName(), file);
            mailSender.send(message);
        } catch (Exception e) {
            dbLog(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmail(String to, String sub, String msgBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to.split(","));
            message.setSubject(sub);
            message.setText(msgBody);
            mailSender.send(message);
        } catch (Exception e) {
            dbLog(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmail(String to, String msg) {
        sendEmail(to, msg, msg);
    }

    public void createReport() throws Exception {
        String query = "call yorders_scheduling_reps.goods_moving(:out_sqlHeader,:out_sqlMaster)";
        DataSet queries = factory.exec(query);
        String sql0 = queries.getString("sqlHeader");
        String sql1 = queries.getString("sqlMaster");

        DataSet ds0 = nvl(sql0, null);
        DataSet ds1 = nvl(sql1, ds0);

        File file = reporter.createReport(getResFile("templates/goods_moving.xls"), ds0, ds1);
        sendEmail(username, "Report System", "Движение сырья и готовой продукции", file);
        System.out.println("Письмо отправлено!");
/**/
        query = "call yorders_scheduling_reps.prepack_check(:out_sqlHeader,:out_sqlMaster)";
        queries = factory.exec(query);
        sql0 = queries.getString("sqlHeader");
        sql1 = queries.getString("sqlMaster");

        ds0 = nvl(sql0, null);
        ds1 = nvl(sql1, ds0);

        file = reporter.createReport(getResFile("templates/fas1.xls"), ds0, ds1);
        sendEmail(username, "Report System", "Сверка фасованной продукции", file);
        System.out.println("Письмо отправлено!");
    }

    @Override
    public void dbLog(String msg) {
        try {
            CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            factory.exec("insert into web_system_log (event_date, event_user, event_text) values(sysdate, :event_user, :event_text) ", user.getId(), msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Scheduled(cron = "0 0 7-21 * * *")
    public void test(){
        System.out.println("test every hour: " + new Date());
    }

    @Scheduled(cron = "0 0 7-21 * * *")
    public void createReport1() throws Exception {
        long t = System.currentTimeMillis();

        Date cDate = new Date();
        DataSet jobList = factory.exec("select * from tmdb_orders_scheduling g where :d1 >= next_time and step is not null and next_time is not null", new Timestamp(cDate.getTime()));

        for (int i = 0; i < jobList.size(); i++) {
            Object sql = jobList.getObject(i, "proc");
            DataSet queries = factory.exec(sql.toString());

            Object template = jobList.getObject(i, "templ");
            if (template == null)
                continue;

            String sql0 = queries.getString("sqlHeader");
            String sql1 = queries.getString("sqlMaster");
            DataSet ds0 = nvl(sql0, null);
            DataSet ds1 = nvl(sql1, ds0);

            File file = reporter.createReport(getResFile("templates/" + template), ds0, ds1);

            String emails = factory.value("select wm_concat(email) from vmdb_user_emails where user_id in (" + jobList.getObject(i, "senders") + ")", String.class);

            if (!emails.isEmpty()) {
                sendEmail(emails, jobList.getObject(i, "subject").toString(), jobList.getObject(i, "text").toString(), file);
                System.out.println("Письмо отправлено - " + emails);
            }

            Date nDate = getNextDate((Date) jobList.getObject(i, "next_time"), jobList.getObject(i, "step").toString());
            factory.exec("update tmdb_orders_scheduling set next_time = :d1 where jid = :jid", new Timestamp(nDate.getTime()), jobList.getObject(i, "jid"));
        }
        System.out.println("Jobs executed: " + (System.currentTimeMillis() - t) + "    " + new Date());
    }

    private DataSet nvl(String sql, DataSet params) throws Exception {
        return sql.isEmpty() ? new DataSet() : factory.exec(sql, params);
    }

    private File getResFile(String path) {
        return new File(getClass().getClassLoader().getResource(path).getFile());
    }

    private Date getNextDate(Date lastDate, String value) {
        if (lastDate == null)
            return null;

        String[] params = value.split(":");
        int months = Integer.parseInt(params[2]);
        int days = Integer.parseInt(params[1]);
        int hours = Integer.parseInt(params[0]);

        Calendar cur = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.setTime(lastDate);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        do {
            if (months > 0) {
                cal.add(Calendar.MONTH, months);
                if (days > 0)
                    cal.set(Calendar.DAY_OF_MONTH, days);
                cal.set(Calendar.HOUR_OF_DAY, hours);
            } else if (days > 0) {
                cal.add(Calendar.DATE, days);
                cal.set(Calendar.HOUR_OF_DAY, hours);
            } else if (hours > 0) {
                cal.add(Calendar.HOUR, hours);
            }
        } while (!cal.after(cur));

        return cal.getTime();
    }
}
