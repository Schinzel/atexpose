package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.dispatcher.channels.IChannel;
import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to execute a custom command at a regular
 * scheduled interval.
 * This class is essentially a sleeper. It sleeps either:
 * 1) a number of minutes
 * 2) until a certain time of day
 *
 * @author Schinzel
 */
@Accessors(prefix = "m")
public class ScheduledTaskChannel implements IChannel {
    /** Pattern for time for daily tasks. */
    private static final Pattern TIME_PATTERN = Pattern.compile("^[0-2][0-9]:[0-5][0-9]");
    /** The name of this tasks. */
    final String mTaskName;
    /** The task to run. Is a request in the text format. E.g. "echo hi" */
    @Getter
    private final String mTaskRequest;
    /** Human readable string for when the task is to execute. */
    private final String mTaskTime;
    /** The interval size. For minute tasks this is the number of minutes between tasks. */
    private final int mIntervalAmount;
    /** The interval unit. Is days for daily tasks, and minutes for minute tasks. */
    private final TemporalUnit mIntervalUnit;
    /** A flag indicating if an explicit shutdown has been invoked. */
    @Getter(AccessLevel.PACKAGE)
    private Boolean mShutdownWasInvoked = false;
    /** When to fire the task the next time. */
    ZonedDateTime mTimeToFireNext;
    final IWatch mWatch;
    //------------------------------------------------------------------------
    // CONSTRUCTORS AND SHUTDOWN
    //------------------------------------------------------------------------


    /**
     * Sets up a tasks that executes every argument amount of minutes.
     *
     * @param taskName          The name of the task.
     * @param request           The request to execute.
     * @param intervalInMinutes The interval in minutes.
     */
    public ScheduledTaskChannel(String taskName, String request, int intervalInMinutes) {
        this(taskName, request, intervalInMinutes, Watch.create());
    }


    ScheduledTaskChannel(String taskName, String request, int intervalInMinutes, IWatch watch) {
        this(taskName, request, ChronoUnit.MINUTES, intervalInMinutes,
                "Every " + intervalInMinutes + " minutes",
                ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(validateMinuteInterval(intervalInMinutes)),
                watch);
    }


    /**
     * Sets up a daily task that executes once per days.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute.
     * @param timeOfDay What time of day to execute. Format HH:mm, e.g. 23:55
     */
    public ScheduledTaskChannel(String taskName, String request, String timeOfDay) {
        this(taskName, request, timeOfDay, Watch.create());
    }


    ScheduledTaskChannel(String taskName, String request, String timeOfDay, IWatch watch) {
        this(taskName, request, ChronoUnit.DAYS, 1,
                "Every day at " + timeOfDay,
                LocalTime.parse(validateTimeOfDay(timeOfDay))
                        .atDate(LocalDate.now(ZoneId.of("UTC")))
                        .atZone(ZoneId.of("UTC")),
                watch);
    }


    /**
     * Sets up a monthly task that executes once per month.
     *
     * @param taskName   The name of the task.
     * @param request    The request to execute.
     * @param timeOfDay  What time of day to execute. Format HH:mm, e.g. 23:55
     * @param dayOfMonth Day of month to execute. Min 1 and max 28.
     */
    public ScheduledTaskChannel(String taskName, String request, String timeOfDay, int dayOfMonth) {
        this(taskName, request, timeOfDay, dayOfMonth, Watch.create());
    }


    ScheduledTaskChannel(String taskName, String request, String timeOfDay, int dayOfMonth, IWatch watch) {
        this(taskName, request, ChronoUnit.MONTHS, 1,
                "Once a month at " + timeOfDay + " on month day " + dayOfMonth,
                LocalTime.parse(validateTimeOfDay(timeOfDay))
                        .atDate(LocalDate.now(ZoneId.of("UTC")))
                        .withDayOfMonth(validateDayOfMonth(dayOfMonth))
                        .atZone(ZoneId.of("UTC")),
                watch);
    }


    /**
     * @param taskName       The name of the task.
     * @param request        The command to execute.
     * @param intervalUnit   The interval unit. E.g. Min, Months and so on. Used in
     *                       conjunction with intervalAmount.
     * @param intervalAmount The amount to wait. Used in conjunction with
     *                       intervalUnit.
     */
    ScheduledTaskChannel(String taskName, String request, ChronoUnit intervalUnit, int intervalAmount, String taskTime, ZonedDateTime initialTaskFireTime, IWatch watch) {
        mTaskName = taskName;
        mTaskRequest = request;
        mIntervalUnit = intervalUnit;
        mIntervalAmount = intervalAmount;
        mTaskTime = taskTime;
        mTimeToFireNext = getNextTaskTime(initialTaskFireTime, 1, ChronoUnit.MONTHS);
        mWatch = watch;
    }


    @Override
    public void shutdown(Thread thread) {
        mShutdownWasInvoked = true;
        //Call interrupt to wake from sleep
        thread.interrupt();
    }


    @Override
    public IChannel getClone() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName()
                + " does not support multithreaded dispatchers.");
    }


    //------------------------------------------------------------------------
    // MESSAGING
    //------------------------------------------------------------------------
    public boolean getRequest(ByteStorage request) {
        //Get the number of nanoseconds the executing thread should sleep.
        long nanosToSleep = Duration.between(LocalDateTime.now(ZoneId.of("UTC")), mTimeToFireNext).toNanos();
        //Put executing thread to sleep. 
        boolean wasNormalWakeUp = this.sleep(nanosToSleep);
        //Convert request to byte array and add to request argument.
        request.add(mTaskRequest);
        //Calc the next time to fire task
        mTimeToFireNext = getNextTaskTime(mTimeToFireNext, mIntervalAmount, mIntervalUnit);
        //Return true if was normal wake up. False if this thread has been instructed to shutdown.
        return wasNormalWakeUp;
    }


    /**
     * Puts the executing thread to sleep the argument amount of time.
     *
     * @param nanosToSleep The number of nano seconds to sleep.
     * @return True if it was time to wake up. False this thread was requested
     * to shutdown.
     */
    boolean sleep(long nanosToSleep) {
        try {
            //Good night...
            TimeUnit.NANOSECONDS.sleep(nanosToSleep);
        } catch (InterruptedException ex) {
            //Good morning! If got here, hopefully shutdown was invoked. If so the shutdown variable should be true
            if (mShutdownWasInvoked) {
                return false;
            } //Interruption was caused by something else than shutdown being invoked. Which is not right.
            else {
                throw new RuntimeException("Invocation error in scheduled task. " + ex.getMessage());
            }
        }
        return true;
    }


    @Override
    public void writeResponse(byte[] response) {
        //Do nothing. The default event log is where one will look for the response. 
    }


    @Override
    public long responseWriteTime() {
        return 0;
    }


    //------------------------------------------------------------------------
    // STATIC UTIL
    //------------------------------------------------------------------------
    static ZonedDateTime getNextTaskTime(ZonedDateTime time, int intervalAmount, TemporalUnit intervalUnit) {
        //Note, Instant.now().isBefore(mTimeToFireNext) does not work.
        return (!time.isAfter(ZonedDateTime.now(ZoneId.of("UTC"))))
                ? getNextTaskTime(time.plus(intervalAmount, intervalUnit), intervalAmount, intervalUnit)
                : time;
    }


    /**
     * @param minuteInterval The interval in minutes to validate
     * @return The argument minutes
     */
    private static int validateMinuteInterval(int minuteInterval) {
        Thrower.throwIfVarOutsideRange(minuteInterval, "Mintues", 1, 1440);
        return minuteInterval;
    }


    /**
     * Validates argument day of time string. Throws exception if not valid.
     *
     * @param timeOfDay Time-string to validate. E.g. "23:55"
     * @return The argument time of day
     */
    private static String validateTimeOfDay(String timeOfDay) {
        Thrower.throwIfFalse(TIME_PATTERN.matcher(timeOfDay).matches())
                .message("Incorrect task time: '" + timeOfDay + "'. Correct format is HH:mm, e.g. 09:00 or 23:55.");
        return timeOfDay;
    }


    /**
     * @param dayOfMonth The day of the month. A number between 1 and 28.
     * @return The argument day of month
     */
    private static int validateDayOfMonth(int dayOfMonth) {
        Thrower.throwIfVarOutsideRange(dayOfMonth, "dayOfMonth", 1, 28);
        return dayOfMonth;
    }


    //------------------------------------------------------------------------
    // LOGGING & STATS
    //------------------------------------------------------------------------
    @Override
    public long requestReadTime() {
        //Hardcoded to zero as the read time is there is no read time for scheduled tasks
        return 0;
    }


    @Override
    public String senderInfo() {
        return "ScheduledTask: " + mTaskName;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("task_name", mTaskName)
                .add("request", mTaskRequest)
                .add("task_time", mTaskTime)
                .add("next_task_time", mTimeToFireNext.toString())
                .build();
    }


}
