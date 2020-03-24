package com.smart.future.common.constant;

import java.util.stream.Stream;

public enum  RoleCode {
    ROOT("ROOT","系统管理员","系统管理员"),
    ADMINISTRATOR("ADMINISTRATOR","管理员","管理员"),
    SUPER_VIP_USER("SUPER_VIP_USER","超级VIP","超级VIP"),
    VIP_USER("VIP_USER","VIP","VIP"),
    GENERAL_USER("GENERAL_USER","普通用户","普通用户");

    String code;
    String name;
    String description;

    RoleCode(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static String[] getAllCode(){
        return Stream.of(values()).map(RoleCode::getCode).toArray(String[]::new);
    }
}
