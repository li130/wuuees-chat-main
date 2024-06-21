package com.wuuees.chat.holder.impl;

import com.wuuees.chat.common.constant.BusinessConstant;
import com.wuuees.chat.common.constant.RedisKeyPrefix;
import com.wuuees.chat.common.model.UserGlobalInfoDto;
import com.wuuees.chat.holder.IUserHolder;
import com.wuuees.chat.redis.starter.RedisCacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserHolderImpl implements IUserHolder {

    @Autowired
    private RedisCacheHelper redisCacheHelper;

    @Override
    public UserGlobalInfoDto getUserInfo(String userId) {
        return redisCacheHelper.getCacheObject(RedisKeyPrefix.AQ_USER_INFO_PREFIX + userId, UserGlobalInfoDto.class);
    }

    @Override
    public void saveUserInfo(UserGlobalInfoDto userGlobalInfoDto) {
        redisCacheHelper.setCacheObject(RedisKeyPrefix.AQ_USER_INFO_PREFIX + userGlobalInfoDto.getUserId(),
                userGlobalInfoDto, BusinessConstant.USER_INFO_CACHE_TIME, TimeUnit.SECONDS);
    }
}
