package com.warningrc.test.learnspring.module.springcache.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.warningrc.test.learnspring.module.springcache.user.entity.User;
import com.warningrc.test.learnspring.module.springcache.user.service.UserService;

/**
 * The Class UserController.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public User getUserById(@PathVariable("userId") long userId) {
        return userService.getUserById(userId);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getUserByIdTitle(Model model) {
        model.addAttribute("allUser", userService.getAllUser());
        return "user/users";
    }
}
