package com.warningrc.test.learnspring.module.springcache.user.dao;

import java.util.List;

import com.warningrc.test.learnspring.module.springcache.user.entity.User;

/**
 * The Interface UserDao.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 */
public interface UserDao {

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
     * @return the user
     */
    User updateUser(long userId, String userName, int userAge);
}
