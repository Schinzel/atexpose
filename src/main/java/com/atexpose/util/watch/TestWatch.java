package com.atexpose.util.watch;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * The purpose of this class is return an instant to represents now and to be able to specify this
 * instant for testing purposes.
 */
public class TestWatch implements IWatch {
    /** The instant for now. */
    private Instant mInstant = null;


    /**
     * @return A new instance.
     */
    public static TestWatch create() {
        return new TestWatch();
    }


    /**
     * @return An instant representing now.
     */
    public Instant getInstant() {
        return (mInstant == null) ? Instant.now() : mInstant;
    }


    /**
     * For testing purposes only. Set a instant that will be returned by getInstant.
     *
     * @param instant The instant that this instance should return.
     * @return This for chaining.
     */
    public TestWatch setInstant(Instant instant) {
        mInstant = instant;
        return this;
    }


    /**
     * For testing purposes only. An instant representing the argument date will be returned with
     * getInstant.
     *
     * @param year       The year. E.g. 2015
     * @param month      The month-of-year to represent, from 1 (January) to 12
     *                   (December)
     * @param dayOfMonth The day-of-month. From 1 to 31
     * @param hour       The hour-of-day. From 0 to 23
     * @param minute     The minute-of-hour. From 0 to 59
     * @return This for chaining.
     */
    public TestWatch setDateTimeUtc(int year, int month, int dayOfMonth, int hour, int minute) {
        return this.setDateTime(year, month, dayOfMonth, hour, minute, ZoneId.of("UTC"));
    }


    /**
     * For testing purposes only. An instant representing the argument date will be returned with
     * getInstant.
     *
     * @param year       The year. E.g. 2015
     * @param month      The month-of-year to represent, from 1 (January) to 12
     *                   (December)
     * @param dayOfMonth The day-of-month. From 1 to 31
     * @param hour       The hour-of-day. From 0 to 23
     * @param minute     The minute-of-hour. From 0 to 59
     * @param zoneId     The id of the zone
     * @return An instance of this for chaining.
     */
    public TestWatch setDateTime(int year, int month, int dayOfMonth, int hour, int minute, ZoneId zoneId) {
        ZonedDateTime zdt = ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, zoneId);
        mInstant = zdt.toInstant();
        return this;
    }


    @Override
    public String toString() {
        return this.getInstant().toString();
    }
}
