package com.xixi.interfaces;

/**
 * 代理类创建接口
 *
 * @author markingWang
 * @date 2021/7/16 5:34 下午
 */
public interface ProxyCreator {

    /**
     * 创建代理类
     *
     * @param type 需要创建代理类的class类型
     * @return 代理类
     */
    Object createProxy(Class<?> type);
}
