package com.soft1851.usercenter.controller;

import com.soft1851.usercenter.domain.dto.UserAddBonusMsgDto;
import com.soft1851.usercenter.domain.entity.User;
import com.soft1851.usercenter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable Integer id) {
        log.info("我被请求了...");
        return this.userService.findById(id);
    }

    @GetMapping("/q")
    public User query(User user){
        return user;
    }

    /**
     * 给用户增加积分（同步）
     * @param userAddBonusMsgDto
     * @return
     */
    @PutMapping(value = "/add/bonus")
    public Integer addBonusToUser(@RequestBody UserAddBonusMsgDto userAddBonusMsgDto){
        return this.userService.addBonusToUser(userAddBonusMsgDto);
    }

}