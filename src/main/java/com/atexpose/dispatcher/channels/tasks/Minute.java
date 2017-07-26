package com.atexpose.dispatcher.channels.tasks;

/**
 * The purpose of this enum is to represent the minutes in an hour.
 */
public enum Minute {
    _00, _01, _02, _03, _04, _05, _06, _07, _08, _09,
    _10, _11, _12, _13, _14, _15, _16, _17, _18, _19,
    _20, _21, _22, _23, _24, _25, _26, _27, _28, _29,
    _30, _31, _32, _33, _34, _35, _36, _37, _38, _39,
    _40, _41, _42, _43, _44, _45, _46, _47, _48, _49,
    _50, _51, _52, _53, _54, _55, _56, _57, _58, _59;


    public int getAsInt() {
        return Integer.valueOf(this.name().substring(1));
    }
}
