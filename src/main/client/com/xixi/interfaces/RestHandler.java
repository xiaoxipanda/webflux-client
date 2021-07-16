package com.xixi.interfaces;

import com.xixi.dto.MethodInfo;
import com.xixi.dto.ServerInfo;

/**
 * rest请求调用handler
 *
 * @author markingWang
 * @date 2021/7/16 6:37 下午
 */
public interface RestHandler {

    /**
     * 初始化服务器信息
     *
     * @param serverInfo 服务器信息
     */
    void init(ServerInfo serverInfo);


    /**
     * 调用rest接口，返回结果
     *
     * @param methodInfo 方法信息
     * @return 结果
     */
    Object invokeRest(MethodInfo methodInfo);
}
