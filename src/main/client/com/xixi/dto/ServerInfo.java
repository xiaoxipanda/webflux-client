package com.xixi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务端信息
 *
 * @author markingWang
 * @date 2021/7/16 5:43 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {

    private String url;
}
