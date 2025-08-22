package net.cyue.web.easyquery.core.provider;

import net.cyue.web.easyquery.core.provider.api.IServiceProvider;
import net.cyue.web.easyquery.core.service.api.IService;

/**
 * 服务提供者抽象类
 * @param <TPrototype> 原型类
 * @param <TService> 原型相关服务类
 */
public abstract class AbstractServiceProvider<TPrototype, TService extends IService<TPrototype>>
    implements IServiceProvider<TPrototype, TService>
{
    /**
     * 原型实例
     */
    protected final TPrototype prototype;
    /**
     * 原型相关服务实例
     */
    protected TService service;

    /**
     * 构造函数
     * @param prototype 原型实例
     */
    public AbstractServiceProvider(TPrototype prototype)
    {
        this.prototype = prototype;
        this.service = this.initService();
    }

    /**
     * 初始化服务实例
     * @return 服务实例
     */
    protected abstract TService initService();

    @Override
    public TPrototype getPrototype() {
        return this.prototype;
    }
    @Override
    public TService getService() {
        return this.service;
    }
}
