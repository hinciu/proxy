package com.proxy.main;


import com.proxy.actions.ProxyActions;
import com.proxy.checker.ProxyResultModel;
import com.proxy.db.service.ProxyService;
import com.proxy.driver.DriverHolder;
import com.proxy.driver.DriverInitializer;
import com.proxy.parser.ProxyParser;
import com.proxy.utils.UIUtils;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;


@Component
public class App {

    private JPanel panel;
    private JButton getProxyList;
    private JList uiProxyList;
    private JButton checkProxy;
    private JScrollPane jscrollPane;
    private JPanel leadForm;
    private JTextField offerField;
    private JTextField emailField;
    private JButton submitLeadButton;
    private JComboBox leadState;
    private JButton checkEmail;
    private JButton cancel;
    private JButton getProxy;
    private JTextArea getProxyValue;


    @Autowired
    ProxyService proxyService;

    @Autowired
    private ProxyParser proxyParser;

    @Autowired
    private ProxyActions proxyActions;

    @Autowired
    DriverInitializer driverInitializer;

    @Autowired
    DriverHolder driverHolder;

    List<String> proxyList;


    public App() {
        init();
        getProxyList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leadForm.setVisible(false);
                proxyList = proxyParser.collectProxy();
                String[] proxyArr = proxyList.toArray(new String[proxyList.size()]);
                uiProxyList.setListData(proxyArr);
                uiProxyList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);


            }
        });

        checkProxy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    leadForm.setVisible(false);
                    String proxy = (String) uiProxyList.getSelectedValue();
                    int selectedIndex = uiProxyList.getSelectedIndex();
                    if (!proxyList.isEmpty()) {
                        proxyList.remove(selectedIndex);
                        String[] proxyArr = proxyList.toArray(new String[proxyList.size()]);
                        uiProxyList.setListData(proxyArr);
                        doAction(proxy);
                    }
                } catch (Exception ex) {
                    System.out.println(e);
                }


            }
        });

        checkEmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String email = emailField.getText();
                    if (!proxyActions.checkEmail(email)) {
                        emailField.setText("");
                    }
                } catch (Exception ex) {
                    System.out.println(e);
                }

            }
        });
        getProxy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String proxy = getProxyValue.getText().trim();
                doAction(proxy);
            }
        });
    }

    private void doAction(String proxy) {
        ProxyResultModel resultModel = proxyActions.checkProxy(proxy);
        if (resultModel.getResult()) {
            if (proxyService.insertProxy(proxy)) {
                WebDriver driver = driverInitializer.initProxyBrowser(proxy);
                driverHolder.addDriver(driver);
                proxyActions.openAnonymityChecker(driver);
                if (!proxyActions.openMapsIfAgree(resultModel.getLocation(), proxy)) {
                    proxyService.deleteProxy(proxy);
                    driverHolder.quitAll();
                    return;
                }
                if (!proxyActions.openLeadFormIfAgree(driver, leadForm, proxy)) {
                    proxyService.deleteProxy(proxy);
                    driverHolder.quitAll();
                    return;
                }
                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        leadForm.setVisible(false);
                        proxyService.deleteProxy(proxy);
                        driverHolder.quitAll();
                    }
                });
                submitLeadButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String offer = offerField.getText();
                        String email = emailField.getText();
                        String state = (String) leadState.getSelectedItem();
                        if (offer.equalsIgnoreCase("") || email.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "Please specify required fields");
                        } else {
                            proxyActions.saveLead(offer, email, state, proxy);
                            proxyService.saveUsedEmail(email);
                            leadForm.setVisible(false);
                            driverHolder.quitAll();
                        }

                    }
                });


            } else {
                JOptionPane.showMessageDialog(null, "Proxy already in use.Please select other");
            }

        } else {
            JOptionPane.showMessageDialog(null, resultModel.getComparisionResult());
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public void init() {
        leadForm.setVisible(false);
        UIUtils.setLeadStates(this.leadState);
    }


}
