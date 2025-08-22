package net.cyue.web.easyquery.core;

import net.cyue.web.easyquery.core.http.data.DefaultWebResult;

public class TestClass {
    public static void main(String[] args) {
        DefaultWebResult result = DefaultWebResult.success("success");
        Object obj = result;
        System.out.println(obj.getClass());
    }
}
