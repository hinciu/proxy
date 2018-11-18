package com.proxy.main;

import com.proxy.Context;
import com.proxy.db.config.Config;
import com.proxy.db.service.ProxyService;

import com.proxy.parser.ProxyParser;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        ApplicationContext ac = new AnnotationConfigApplicationContext(Context.class, Config.class);
        App app = ac.getBean(App.class);
        JFrame frame = new JFrame("proxy");
        frame.setContentPane(app.getPanel());
        frame.setSize(510, 510);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
