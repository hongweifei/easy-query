package net.cyue.web.easyquery.core.provider.api;


import net.cyue.web.easyquery.core.service.api.IService;

/**
 * 服务提供者接口
 * <br>
 * 服务提供者，针对 prototype 提供相关 service
 * @param <TPrototype> 原型
 * @param <TService> 服务
 */
public interface IServiceProvider<TPrototype, TService extends IService<TPrototype>> extends IProvider {

    /**
     * 获取原型
     * @return 原型实例
     */
    TPrototype getPrototype();

    /**
     * 获取服务
     * @return 服务实例
     */
    TService getService();
}
