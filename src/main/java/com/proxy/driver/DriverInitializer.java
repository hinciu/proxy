package com.proxy.driver;

import com.proxy.utils.MatcherUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DriverInitializer {

    public DriverInitializer() {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
    }


    public WebDriver initProxyBrowser(String proxy) {
        Map<String, Object> preferences = new HashMap<String, Object>();
        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        preferences.put("enable_do_not_track", true);
        options.setExperimentalOption("prefs", preferences);
        options.addArguments("--lang=en");
        options.addArguments("--proxy-server=http://" + proxy);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability("pageLoadStrategy", "none");
        return new ChromeDriver(capabilities);
    }

    public WebDriver initSimpleBrowser() {
        return new ChromeDriver();
    }

    public static void main(String[] args) {
        DriverInitializer driverInitializer = new DriverInitializer();
        WebDriver driver = driverInitializer.initProxyBrowser("204.14.154.126:39596");
        driver.get("https://www.myip.com/");
        driver.quit();
        System.out.printf("sDDDDDDDDDDDDDD");
    }
}
