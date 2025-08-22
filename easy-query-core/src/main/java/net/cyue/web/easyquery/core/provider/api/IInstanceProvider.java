package net.cyue.web.easyquery.core.provider.api;

/**
 * 实例提供者接口
 * @param <T> 实例类型
 */
public interface IInstanceProvider<T> extends IProvider {

    /**
     * 获取实例
     * @return 实例
     */
    T getInstance();
}
