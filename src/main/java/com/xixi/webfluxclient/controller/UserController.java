package com.xixi.webfluxclient.controller;

import com.xixi.webfluxclient.vo.User;
import com.xixi.webfluxclient.webclient.IUserServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * 用户处理Controller层
 */
@RestController
@RequestMapping("/webflux-client/user")
public class UserController {

    @Autowired
    private IUserServiceApi userServiceApi;

    /**
     * 以数组形式一次性返回数据
     *
     * @return user flux
     */
    @GetMapping("/")
    public Flux<User> getAll() {
        return userServiceApi.getAllUsers();
    }

    /**
     * 以SSE形式多次返回数据
     *
     * @return steam user flux
     */
    @GetMapping(value = "/stream/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamGetAll() {
        return userServiceApi.getAllUsers();
    }

    /**
     * 新增数据
     *
     * @param user 用户数据
     * @return mono user
     */
    @PostMapping("/")
    public Mono<User> createUser(@Valid @RequestBody User user) {
        user.setId(null);
        return this.userServiceApi.createUser(user);
    }

    /**
     * 根据id删除用户 存在的时候返回200, 不存在返回404
     *
     * @param id 用户id
     * @return mono ResponseEntity
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(
            @PathVariable("id") String id) {
        return this.userServiceApi.getUserById(id)
                .flatMap(user -> this.userServiceApi.deleteUserById(id).then(
                        Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 修改数据 存在的时候返回200 和修改后的数据, 不存在的时候返回404
     *
     * @param id   用户id
     * @param user 用户信息
     * @return mono ResponseEntity
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable("id") String id,
                                                 @Valid @RequestBody User user) {
        return this.userServiceApi.getUserById(id)
                .flatMap(u -> {
                    u.setAge(user.getAge());
                    u.setName(user.getName());
                    return this.userServiceApi.createUser(u);
                })
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据ID查找用户 存在返回用户信息, 不存在返回404
     *
     * @param id 用户id
     * @return mono ResponseEntity
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> findUserById(
            @PathVariable("id") String id) {
        return this.userServiceApi.getUserById(id)
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
