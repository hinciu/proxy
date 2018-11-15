package com.proxy.actions;

import com.proxy.checker.ProxyChecker;
import com.proxy.checker.ProxyResultModel;
import com.proxy.db.model.EmailModel;
import com.proxy.db.model.ProxyModel;
import com.proxy.db.service.ProxyService;
import com.proxy.driver.DriverHolder;
import com.proxy.driver.DriverInitializer;
import com.proxy.locator.AddressLocator;
import com.proxy.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

@Component
public class ProxyActions {
    @Autowired
    Environment env;

    @Autowired
    ProxyChecker proxyChecker;

    @Autowired
    AddressLocator addressLocator;

    @Autowired
    ProxyService proxyService;

    @Autowired
    DriverInitializer driverInitializer;

    @Autowired
    DriverHolder driverHolder;

    public ProxyResultModel checkProxy(String proxy) {
        ProxyResultModel proxyResultModel = null;
        try {
            proxyResultModel = proxyChecker.checkProxyForProviderAndCountry(addressLocator.locateAddress(proxy));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return proxyResultModel;
    }

    public void openAnonymityChecker(WebDriver driver) {
        driver.get(env.getProperty("anonymity.url"));
    }

    public boolean openMapsIfAgree(String location, String proxy) {

        try {
            int response = JOptionPane.showConfirmDialog(null, "Do you want to locate this address?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
                proxyService.deleteProxy(proxy);
                return false;
            } else {
                WebDriver driver = driverInitializer.initSimpleBrowser();
                driverHolder.addDriver(driver);
                driver.get(env.getProperty("google.maps"));
                WaitUtils.waitUntilCondition(() -> {
                    try {
                        return driver.findElement(By.xpath(env.getProperty("google.map.input"))).isDisplayed();
                    } catch (NoSuchElementException e) {
                        return false;
                    }
                }, true, 20);
                driver.findElement(By.xpath(env.getProperty("google.map.input"))).sendKeys(location);
                driver.findElement(By.xpath(env.getProperty("google.map.search"))).click();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean openLeadFormIfAgree(WebDriver driver, JPanel jPanel, String proxy) {
        int response = JOptionPane.showConfirmDialog(null, "Do you want to save a lead?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
            proxyService.deleteProxy(proxy);
            return false;
        } else {
            jPanel.setVisible(true);
            return true;
        }
    }

    public int saveLead(String offer, String email, String state, String ip) {
        return proxyService.updateProxy(offer, email, state, ip);
    }

    public boolean checkEmail(String email) {
        StringJoiner joiner = new StringJoiner("\n");
        List<ProxyModel> usage = proxyService.getEmail(email);
        List<EmailModel> usage2 = proxyService.getEmailFromEmailsTable(email);
        if (usage.isEmpty() && usage2.isEmpty()) {
            JOptionPane.showMessageDialog(null, "NOT USED");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "EMAIL WAS USED. PLEASE USE OTHER");
            return false;
        }

    }
}
