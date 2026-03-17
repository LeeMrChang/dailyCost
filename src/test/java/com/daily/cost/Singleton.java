package com.daily.cost;

/**
 *
 *  手写单例模式，要点
 *  1.定义属性与方法必须是私有的，原则之一
 *  2.构造方法需显示定义私有，不显式默认私有构造是public级别，要防止外部通过构造方法创建实例
 */
public  class Singleton {

    private Singleton() {}

    private static volatile Singleton INSTANCE = new Singleton();

    /**
     *  获取单例bean，返回唯一实例 饿汉式
     * @return ean
     */
    public Singleton getHungryInstance() {
        return INSTANCE;
    }

    /**
     * 获取单例bean，返回唯一实例 懒汉式
     * @return bean
     */
    public Singleton getLazyInstance() {
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 定义静态内部类
     */
    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    /**
     * 静态类，也是懒汉式
     * @return bean
     */
    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
