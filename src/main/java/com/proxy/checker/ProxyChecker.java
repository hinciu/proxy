package com.proxy.checker;

import com.proxy.locator.IpLocation;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class ProxyChecker {
    @Autowired
    Environment env;

    public ProxyResultModel checkProxyForProviderAndCountry(IpLocation proxy) {
        ProxyResultModel result = new ProxyResultModel();
        List<String> providerBlackList = Arrays.asList(env.getProperty("provider.black.list").split(","));
        List<String> validCountry = Arrays.asList(env.getProperty("valid.country").split(","));
        result.setLocation(proxy.getLat() + "," + proxy.getLon());
        String actualCountry = proxy.getCountry();
        String actualProvider = proxy.getIsp();
        result.setResult(true);
        if (!isValidProvider(actualProvider)) {
            result.setResult(false);
            result.setComparisionResult("Current provider{" + actualProvider + "} is in black list");
        } else if (!validCountry.contains(actualCountry)) {
            result.setResult(false);
            result.setComparisionResult("Current country{" + actualCountry + "} is not in valid countries list" + validCountry);
        }

        return result;
    }


    public boolean isValidCountry(String actualCountry) {
        List<String> validCountries = Arrays.asList(env.getProperty("valid.country").split(","));
        for (String validCountry : validCountries) {
            return validCountry.equalsIgnoreCase(actualCountry);
        }
        return false;
    }

    public boolean isValidProvider(String actuaProvider) {
        List<String> providerBlackList = Arrays.asList(env.getProperty("provider.black.list").split(","));
        if (actuaProvider.equalsIgnoreCase("")) {
            return true;
        }
        for (String provider : providerBlackList) {
            if (provider.contains(actuaProvider)) {
                return false;
            }
        }
        return true;

    }
}
