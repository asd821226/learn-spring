package com.warningrc.test.learnspring.module.springcache.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warningrc.test.learnspring.module.springcache.user.dao.UserDao;
import com.warningrc.test.learnspring.module.springcache.user.entity.User;

/**
 * The Class UserServiceImpl.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();
    }

}
