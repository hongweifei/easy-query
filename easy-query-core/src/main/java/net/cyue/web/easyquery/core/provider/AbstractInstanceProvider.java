package net.cyue.web.easyquery.core.provider;


import net.cyue.web.easyquery.core.provider.api.IInstanceProvider;

/**
 * 实例提供者抽象类
 * @param <T> 实例类型
 */
public abstract class AbstractInstanceProvider<T> implements IInstanceProvider<T> {
    /**
     * 实例
     */
    protected T instance;

    /**
     * 构造函数
     */
    public AbstractInstanceProvider() {
        this.instance = this.initInstance();
    }

    /**
     * 初始化实例
     * @return 实例
     */
    protected abstract T initInstance();

    @Override
    public T getInstance() {
        return this.instance;
    }
}
