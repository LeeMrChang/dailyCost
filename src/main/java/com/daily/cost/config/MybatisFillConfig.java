package com.daily.cost.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MybatisFillConfig implements MetaObjectHandler {

    public static final String CREATE_ID = "createId";
    public static final String CREATE_TIME = "createTime";
    public static final String UPDATE_ID = "updateId";
    public static final String UPDATE_TIME = "updateTime";

    /**
     * 默认填充删除标识，创建人，创建时间，更新人，更新时间
     * 删除标识 -- 如果无值，默认删除标识为false
     * 创建人 -- 如果无值，默认填充为小程序的当前用户。管理后台需手动给值
     * 创建时间 -- 如果无值，默认填充为当前时间。（有场景需要自定义创建时间，下单）
     * 更新人 -- 如果无值，默认填充为小程序的当前用户。管理后台需手动给值
     * 更新时间 -- 强制更新为当前时间
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (ObjectUtil.isEmpty(getFieldValByName(CREATE_ID, metaObject))) {
            setFieldValByName(CREATE_ID, 0L, metaObject);
        }
        if (ObjectUtil.isEmpty(getFieldValByName(CREATE_TIME, metaObject))) {
            setFieldValByName(CREATE_TIME, DateUtil.date(), metaObject);
        }
        if (ObjectUtil.isEmpty(getFieldValByName(UPDATE_ID, metaObject))) {
            setFieldValByName(UPDATE_ID, 0L, metaObject);
        }
        setFieldValByName(UPDATE_TIME, DateUtil.date(), metaObject);
    }

    /**
     * 默认填充更新人和更新时间
     * 更新人 -- 如果无值，默认填充为小程序的当前用户。管理后台需手动给值
     * 更新时间 -- 强制更新为当前时间
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (ObjectUtil.isEmpty(getFieldValByName(UPDATE_ID, metaObject))) {
            setFieldValByName(UPDATE_ID, 0L, metaObject);
        }
        setFieldValByName(UPDATE_TIME, DateUtil.date(), metaObject);
    }

}
