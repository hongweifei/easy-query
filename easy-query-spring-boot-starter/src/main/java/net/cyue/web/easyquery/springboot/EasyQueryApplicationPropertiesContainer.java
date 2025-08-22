package net.cyue.web.easyquery.springboot;

public class EasyQueryApplicationPropertiesContainer {
    private final String[] propertiesPathArray;

    public EasyQueryApplicationPropertiesContainer(String[] propertiesPathArray)
    {
        this.propertiesPathArray = propertiesPathArray;
    }

    public String[] getPropertiesPathArray() {
        return this.propertiesPathArray;
    }
}
