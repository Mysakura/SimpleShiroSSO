package com.shirosso.appone.service;

import com.example.app.common.entity.User;

import java.util.List;


public interface UserService {

    void addTokenInfo(String token, User user);

    void addUser(User user);

    User login(User user);

    List<User> getUsers();

}
