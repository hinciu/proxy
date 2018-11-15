package com.proxy.db.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProxyMapper implements RowMapper<ProxyModel> {

    @Override
    public ProxyModel mapRow(ResultSet resultSet, int i) throws SQLException {
        ProxyModel res = new ProxyModel();
        res.setId(resultSet.getInt("ID"));
        res.setOffer(resultSet.getString("OFFER"));
        res.setEmail(resultSet.getString("EMAIL"));
        res.setIp(resultSet.getString("IP"));
        res.setDate(resultSet.getDate("DATE"));
        res.setState(resultSet.getString("STATE"));
        res.setUser(resultSet.getString("USER"));
        return res;
    }
}
