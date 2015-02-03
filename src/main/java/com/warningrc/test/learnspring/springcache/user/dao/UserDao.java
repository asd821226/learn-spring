package com.warningrc.test.learnspring.springcache.user.dao;

import com.warningrc.test.learnspring.springcache.user.entity.User;

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
}
