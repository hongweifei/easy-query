package net.cyue.web.easyquery.core;

import net.cyue.util.ResourceUtil;

import java.io.InputStream;

public class TestApplication {
    public static void main(String[] args) {
        try {
            InputStream is = ResourceUtil.getResourceAsStream("example.properties");
            new EasyQueryApplication(null).runByProperties(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
