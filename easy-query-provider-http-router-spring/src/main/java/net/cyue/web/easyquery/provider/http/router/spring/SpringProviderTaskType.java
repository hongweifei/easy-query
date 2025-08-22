package net.cyue.web.easyquery.provider.http.router.spring;

public enum SpringProviderTaskType {
    SET_CONTEXT("task-provider-spring-set-context"),
    ;
    private final String name;
    SpringProviderTaskType(String name) {
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
