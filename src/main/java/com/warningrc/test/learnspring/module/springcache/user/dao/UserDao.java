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
}
