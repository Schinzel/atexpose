package com.atexpose.dispatcher.channels.tasks;

/**
 * The purpose of this enum is to represent the days of month to occur in all months.
 */
public enum DayOfMonth {
    _1, _2, _3, _4, _5, _6, _7, _8, _9, _10,
    _11, _12, _13, _14, _15, _16, _17, _18, _19, _20,
    _21, _22, _23, _24, _25, _26, _27, _28;


    public int getAsInt() {
        return Integer.valueOf(this.name().substring(1));
    }
}
