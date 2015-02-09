package com.warningrc.test.learnspring.module.springcache.user.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class User.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 */
@Data
public class User implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long userId;
    private String userName;
    private int userAge;
}
