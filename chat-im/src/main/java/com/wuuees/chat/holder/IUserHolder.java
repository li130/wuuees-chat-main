package com.wuuees.chat.holder;

import com.wuuees.chat.common.model.UserGlobalInfoDto;

public interface IUserHolder {

    /**
     * 获取用户登录信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserGlobalInfoDto getUserInfo(String userId);

    /**
     * 保存用户登录信息
     *
     * @param userGlobalInfoDto 用户信息
     */
    void saveUserInfo(UserGlobalInfoDto userGlobalInfoDto);
}
