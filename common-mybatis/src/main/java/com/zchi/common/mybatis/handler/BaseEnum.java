package com.zchi.common.mybatis.handler;

/**
 * Created by zchi on 16/6/16.
 */


/***
 * mybatis中使用的enum 实现此接口, 可以使用CustomEnumTypeHandler
 */
public interface BaseEnum{

    /***
     * 定义与数据库字段对应接口
     * @return
     */
    public int getValue();

}
