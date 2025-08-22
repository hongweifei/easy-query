package net.cyue.web.easyquery.core.provider;

/**
 * 默认实例提供者工厂
 */
public class SimpleInstanceProviderFactory {

    /**
     * 防止实例化
     */
    private SimpleInstanceProviderFactory() {}

    /**
     * 简单实例提供者
     */
    public static class SimpleInstanceProvider<T>  extends AbstractInstanceProvider<T> {
        private SimpleInstanceProvider() {
            super();
        }

        @Override
        protected T initInstance() {
            return null;
        }
    }

    /**
     * 创建简单实例提供者
     * @param instance 实例
     * @return 实例提供者
     * @param <T> 实例类型
     */
    public static <T> SimpleInstanceProvider<T> getInstanceProvider(T instance) {
        SimpleInstanceProvider<T> provider = new SimpleInstanceProvider<>();
        provider.instance = instance;
        return provider;
    }
}
