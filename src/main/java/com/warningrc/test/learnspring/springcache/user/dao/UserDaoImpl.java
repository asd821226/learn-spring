package com.warningrc.test.learnspring.springcache.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.warningrc.test.learnspring.springcache.user.entity.User;

/**
 * The Class UserDaoImpl.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 */
@Repository
public class UserDaoImpl implements UserDao {

    /** slf4j Logger for UserDaoImpl. */
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Cacheable(value = "defaultCache", key = "'User.cache:'+#userId")
    public User getUserById(long userId) {
        logger.info("get User by Id from databases.");
        return jdbcTemplate.query("SELECT user_id , user_name , user_age FROM w_user WHERE user_id = ? ",
                new Object[] {userId}, new ResultSetExtractor<User>() {
                    @Override
                    public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (rs.next()) {
                            User user = new User();
                            user.setUserId(rs.getLong(1));
                            user.setUserName(rs.getString(2));
                            user.setUserAge(rs.getInt(3));
                            return user;
                        }
                        return null;
                    }
                });
    }
}
