package com.proxy.db.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailMapper implements RowMapper<EmailModel> {
    @Override
    public EmailModel mapRow(ResultSet resultSet, int i) throws SQLException {
        EmailModel res = new EmailModel();
        res.setId(resultSet.getInt("ID"));
        res.setEmail(resultSet.getString("EMAIL"));
        return res;
    }
}
