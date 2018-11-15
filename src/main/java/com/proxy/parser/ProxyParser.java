package com.proxy.parser;

import com.proxy.db.service.ProxyService;
import com.proxy.utils.MatcherUtils;
import com.proxy.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//
//show.full.list.button=//input[@value='Show Full List']
//        select.elite=//select[@id='Filter']/.//option[text()="Elite"]
//        filter.button=//input[@value='Filter']
//        pagination.pages=//div[@class='pagenavi']/.//a
@Component
public class ProxyParser {

    @Autowired
    Environment env;

    @Autowired
    ProxyService proxyService;

    public List<String> collectProxy() {
        WebDriver driver = new ChromeDriver();
        try {
            List<String> result = new ArrayList<>();
            System.setProperty("webdriver.chrome.driver", env.getProperty("webdriver.chrome.driver"));

            String showFullList = env.getProperty("show.full.list.button");
            String selectEliteButton = env.getProperty("select.elite");
            String filterButton = env.getProperty("filter.button");
            String pagination = env.getProperty("pagination.pages");

            driver.get(env.getProperty("proxy.resource.url"));
            WaitUtils.waitUntilCondition(() -> driver.findElement(By.xpath(showFullList)).isDisplayed(), true, 30);
            driver.findElement(By.xpath(showFullList)).click();
            WaitUtils.waitUntilCondition(() -> driver.findElement(By.xpath(selectEliteButton)).isDisplayed(), true, 30);
            driver.findElement(By.xpath(selectEliteButton)).click();
            WaitUtils.waitUntilCondition(() -> driver.findElement(By.xpath(filterButton)).isDisplayed(), true, 30);
            driver.findElement(By.xpath(filterButton)).click();


            int pagesSize = driver.findElements(By.xpath(pagination)).size();

            List<WebElement> firstPageIpElements = driver.findElements(By.xpath(env.getProperty("ip.element.path")));
            List<WebElement> firstPagePortElements = driver.findElements(By.xpath(env.getProperty("port.element.path")));

            result.addAll(collect(firstPageIpElements, firstPagePortElements));
            for (int i = 1; i < pagesSize - 1; i++) {
                List<WebElement> page = driver.findElements(By.xpath(pagination));
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,+document.body.scrollHeight);");
                page.get(i).click();
                WaitUtils.waitUntilCondition(() -> driver.findElement(By.xpath(filterButton)).isDisplayed(), true, 30);
                firstPageIpElements = driver.findElements(By.xpath(env.getProperty("ip.element.path")));
                firstPagePortElements = driver.findElements(By.xpath(env.getProperty("port.element.path")));
                result.addAll(collect(firstPageIpElements, firstPagePortElements));
            }
            driver.quit();
            return result;
        } catch (Exception e) {
            driver.quit();
            return new ArrayList<>();
        }

    }

    private boolean checkIfNextButtonExists(WebDriver driver, String nextButtonPath) {
        try {
            driver.findElement(By.xpath(nextButtonPath));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private List<String> collect(List<WebElement> elementsIp, List<WebElement> elementsPort) {
        List<String> dbIps = proxyService.getAllProxies().stream().map(s -> s.getIp()).collect(Collectors.toList());

        List<String> result = new ArrayList<>();
        for (int i = 0; i <= elementsPort.size() - 1; i++) {
            if (!MatcherUtils.isMatches("\\d+.\\d+.\\d+.\\d+", elementsIp.get(i).getText())) {
                continue;
            }
            if (!dbIps.contains(elementsIp.get(i).getText() + ":" + elementsPort.get(i).getText())) {
                result.add(elementsIp.get(i).getText() + ":" + elementsPort.get(i).getText());
            }


        }
        return result;
    }
}
