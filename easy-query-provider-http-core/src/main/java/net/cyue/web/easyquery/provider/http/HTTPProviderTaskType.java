package net.cyue.web.easyquery.provider.http;

public enum HTTPProviderTaskType {
    INIT("task-provider-http-init"),
    ADD_ROUTE("task-provider-http-add-route"),
    ;
    private final String name;

    HTTPProviderTaskType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public String getName(String id) {
        return id + "@" + name;
    }
    public String getName(Object obj) {
        return obj + "@" + name;
    }
}
