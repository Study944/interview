package com.zxc.interview.constant;

/**
 * Redis 常量
 */
public interface RedisConstant {

    static final String USER_SIGN_IN_KEY = "user:signIn:";

    static String getUserSignInKey(int year,Long userId) {
        return String.format("%s:%s:%s", USER_SIGN_IN_KEY,year,userId);
    }

}
