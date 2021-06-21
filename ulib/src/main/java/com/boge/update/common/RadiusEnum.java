package com.boge.update.common;

/**
 * @Author ibshen@aliyun.com
 */
public enum RadiusEnum {

    UPDATE_RADIUS_0(0),
    UPDATE_RADIUS_5(5),
    UPDATE_RADIUS_10(10),
    UPDATE_RADIUS_15(15),
    UPDATE_RADIUS_20(20),
    UPDATE_RADIUS_25(25),
    UPDATE_RADIUS_30(30),
    UPDATE_RADIUS_35(35),
    UPDATE_RADIUS_40(40),
    ;
    int type;

    RadiusEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
