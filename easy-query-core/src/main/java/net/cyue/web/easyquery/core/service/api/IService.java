package net.cyue.web.easyquery.core.service.api;

/**
 * 服务接口
 * @param <T> 原型的类型
 */
public interface IService<T> {
    /**
     * 获取原型实例
     * @return 原型实例
     */
    T getPrototype();
}
