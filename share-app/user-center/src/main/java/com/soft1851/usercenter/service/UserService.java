package com.soft1851.usercenter.service;

import com.soft1851.usercenter.domain.dto.UserAddBonusMsgDto;
import com.soft1851.usercenter.domain.entity.User;

public interface UserService {
    /**
     * 根据id获得用户详情
     * @param id
     * @return User
     */
    User findById(Integer id);

    /**
     * 给用户增加积分
     * @param userAddBonusMsgDto
     * @return
     */
    int addBonusToUser(UserAddBonusMsgDto userAddBonusMsgDto);
}