package com.proxy.locator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.utils.MatcherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
@Component
public class AddressLocator {

    @Autowired
    Environment env;

    @Autowired
    ObjectMapper om;

//    private Properties p = new Properties();
//
//    public AddressLocator()  {
//        om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        try {
//            p.load(new FileInputStream("resources/ui.properties"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public IpLocation locateAddress(String address) throws IOException {
        String addressToLocate = MatcherUtils.getMatchValue("\\d+.\\d+.\\d+.\\d+", address, 0);
        String api = env.getProperty("ip.locator.url") + addressToLocate;
        URL url = new URL(api);
        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        return om.readValue(response.toString(), IpLocation.class);
    }
}
