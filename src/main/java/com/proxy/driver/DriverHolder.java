package com.proxy.driver;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DriverHolder {
    private List<WebDriver> drivers = new ArrayList<>();


    public void addDriver(WebDriver driver){
        drivers.add(driver);
    }
    public void quitAll(){
        for (WebDriver driver : drivers){
            driver.quit();
        }
    }
}
