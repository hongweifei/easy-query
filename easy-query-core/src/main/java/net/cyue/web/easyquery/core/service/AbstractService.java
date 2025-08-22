package net.cyue.web.easyquery.core.service;

import net.cyue.web.easyquery.core.service.api.IService;

/**
 * 抽象服务类
 * @param <TPrototype> 被服务的类型（原型）
 */
public class AbstractService<TPrototype> implements IService<TPrototype> {

    private final TPrototype prototype;

    /**
     * 构造函数
     * @param prototype 被服务的类型（原型）实例
     */
    public AbstractService(TPrototype prototype)
    {
        this.prototype = prototype;
    }

    /**
     * 获取被服务的类型（原型）实例
     * @return 被服务的类型（原型）实例
     */
    @Override
    public TPrototype getPrototype()
    {
        return this.prototype;
    }
}
