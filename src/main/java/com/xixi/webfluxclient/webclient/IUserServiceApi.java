package com.xixi.webfluxclient.webclient;

import com.xixi.ApiServer;
import com.xixi.webfluxclient.vo.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户接口
 *
 * @author markingWang
 * @date 2021/7/16 5:15 下午
 */
@ApiServer("http://localhost:8080/user")
public interface IUserServiceApi {

    /**
     * 获取所有的用户信息
     *
     * @return user flux
     */
    @GetMapping("/")
    Flux<User> getAllUsers();

    /**
     * 根据id获取用户信息
     *
     * @param id 用户id
     * @return mono user
     */
    @GetMapping("/{id}")
    Mono<User> getUserById(@PathVariable("id") String id);

    /**
     * 根据id删除用户信息
     *
     * @param id 用户id
     * @return mono user
     */
    @DeleteMapping("/{id}")
    Mono<User> deleteUserById(@PathVariable("id") String id);

    /**
     * 创建用户信息
     *
     * @param user 用户信息
     * @return mono user
     */
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    Mono<User> createUser(@RequestBody User user);


}
