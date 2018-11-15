package com.proxy.db.service;

import com.proxy.db.model.EmailMapper;
import com.proxy.db.model.EmailModel;
import com.proxy.db.model.ProxyMapper;
import com.proxy.db.model.ProxyModel;
import com.proxy.main.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Component
public class ProxyService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Environment env;

    private String INSERT = "INSERT INTO `processed` (`OFFER`, `EMAIL`, `IP`, `DATE`, `STATE`, `USER`) VALUES ('{offer}', '{email}', '{ip}', '{date}', '{done}', '{user}');";
    private String UPDATE = "UPDATE `processed` SET `OFFER`='{offer}',`EMAIL`='{email}',`STATE`='{state}' WHERE `IP`='{ip}' AND `USER`='{user}';";
    private String DELETE = "DELETE FROM `processed` WHERE `IP`='{ip}' AND `USER`='{user}';";


    @Transactional
    public List<ProxyModel> getAllProxies() {
        return jdbcTemplate.query("select * from processed", new ProxyMapper());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean insertProxy(String pr) {
        if (jdbcTemplate.query("SELECT * from `processed` WHERE  IP='" + pr + "';", new ProxyMapper()).isEmpty()) {
            try {
                jdbcTemplate.execute(getInsertQuery(pr));
                return true;
            } catch (DeadlockLoserDataAccessException e) {
                return false;
            }

        } else {
            return false;
        }
    }

    public int updateProxy(String offer, String email, String state, String ip) {
        String query = getUpdateQuery(offer, email, state, ip);
        return jdbcTemplate.update(query);
    }

    public void deleteProxy(String ip) {
        String query = getDeleteQuery(ip);
        jdbcTemplate.update(query);
    }

    public List<ProxyModel> getEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM `processed` WHERE  EMAIL='" + email + "'", new ProxyMapper());
    }

    public List<EmailModel> getEmailFromEmailsTable(String email) {
        return jdbcTemplate.query("SELECT * FROM `mails` WHERE  EMAIL='" + email + "'", new EmailMapper());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void clearProcessedTable() {
        LocalDate date = LocalDate.now().minusDays(Integer.parseInt(env.getProperty("clear.period.days")));
        jdbcTemplate.execute("DELETE FROM processed where date < '" + Date.valueOf(date).toString() + "';");
    }


    public void saveUsedEmail(String email) {
        jdbcTemplate.execute("INSERT INTO `mails` (`email`) VALUES ('" + email + "');");
    }

    private String getInsertQuery(String ip) {
        return INSERT.replaceAll("\\{ip\\}", ip)
                .replaceAll("\\{date\\}", Date.valueOf(LocalDate.now()).toString())
                .replaceAll("\\{user\\}", env.getProperty("user"));
    }

    private String getUpdateQuery(String offer, String email, String state, String ip) {
        return UPDATE.replaceAll("\\{email\\}", email)
                .replaceAll("\\{offer\\}", offer)
                .replaceAll("\\{user\\}", env.getProperty("user"))
                .replaceAll("\\{ip\\}", ip)
                .replaceAll("\\{state\\}", state);
    }

    private String getDeleteQuery(String ip) {
        return DELETE.replaceAll("\\{ip\\}", ip)
                .replaceAll("\\{user\\}", env.getProperty("user"));

    }

    public  void test() {
        int i = 0;
        while (true){
            String s = String.valueOf(i++);
            jdbcTemplate.execute(getInsertQuery(s));
            System.out.println(s);
        }
    }
}
