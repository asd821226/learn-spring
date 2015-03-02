package com.warningrc.test.learnspring.module.springcache.user.service;

import java.util.List;

import com.warningrc.test.learnspring.module.springcache.user.entity.User;

/**
 * The Interface UserService.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 */
public interface UserService {

    /**
     * Gets the user by id.
     *
     * @param userId the user id
     * @return the user by id
     */
    User getUserById(long userId);

    /**
     * Gets the all user.
     *
     * @return the all user
     */
    List<User> getAllUser();


    /**
     * Update user.
     *
     * @param userId the user id
     * @param userName the user name
     * @param userAge the user age
     */
    void updateUser(long userId, String userName, int userAge);
}
