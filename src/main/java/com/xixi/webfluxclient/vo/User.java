package com.xixi.webfluxclient.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 *
 * @author markingWang
 * @date 2021/7/16 5:15 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;

    private String name;

    private int age;
}
