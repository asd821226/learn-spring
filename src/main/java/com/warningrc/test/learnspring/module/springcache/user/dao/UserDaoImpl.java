package com.warningrc.test.learnspring.module.springcache.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.warningrc.test.learnspring.module.springcache.expiry.CacheExpiry;
import com.warningrc.test.learnspring.module.springcache.user.entity.User;

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
    @CacheExpiry(value = 5, timeUnit = TimeUnit.SECONDS)
    public User getUserById(long userId) {
        logger.info("get User by Id from databases.");
        return jdbcTemplate.query("SELECT user_id , user_name , user_age FROM w_user WHERE user_id = ? ",
                new Object[] {userId}, new ResultSetExtractor<User>() {
                    @Override
                    public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (rs.next()) {
                            return getUserFromResultSet(rs);
                        }
                        return null;
                    }
                });
    }

    @Override
    @Cacheable(value = "defaultCache")
    public List<User> getAllUser() {
        logger.info("get All User  from databases.");
        return jdbcTemplate.query("SELECT user_id , user_name , user_age FROM w_user ", new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getUserFromResultSet(rs);
            }
        });
    }

    private static User getUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setUserName(rs.getString("user_name"));
        user.setUserAge(rs.getInt("user_age"));
        return user;
    }

    @Override
    @CachePut(value = "defaultCache", key = "'User.cache:'+#userId")
    public User updateUser(long userId, String userName, int userAge) {
        jdbcTemplate.update("UPDATE w_user SET user_name = ? ,user_age = ? WHERE user_id = ?", userName, userAge,
                userId);
        return getUserById(userId);
    }
}
