package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.dispatcher.channels.IChannel;
import com.atexpose.util.ByteStorage;
import com.atexpose.util.DateTimeStrings;
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
    @Getter private final String mTaskRequest;
    /** Human readable string for when the task is to execute. */
    private final String mTaskTime;
    /** The interval size. For minute tasks this is the number of minutes between tasks. */
    final int mIntervalAmount;
    /** The interval unit. Is days for daily tasks, and minutes for minute tasks. */
    final TemporalUnit mIntervalUnit;
    /** A flag indicating if an explicit shutdown has been invoked. */
    @Getter(AccessLevel.PACKAGE) private Boolean mShutdownWasInvoked = false;
    /** When to fire the task the next time. */
    LocalDateTime mTimeToFireNext;
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
        this(taskName, request, ChronoUnit.MINUTES, intervalInMinutes, "Every " + intervalInMinutes + " minutes");
        Thrower.throwIfVarOutsideRange(intervalInMinutes, "Mintues", 1, 1440);
        //Set start time to be one interval in the future
        mTimeToFireNext = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(mIntervalAmount);
    }


    /**
     * Sets up a daily task that executes once per days.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute.
     * @param timeOfDay What time of day to execute. Format HH:mm, e.g. 23:55
     */
    public ScheduledTaskChannel(String taskName, String request, String timeOfDay) {
        this(taskName, request, ChronoUnit.DAYS, 1, "Every day at " + timeOfDay);
        validateTimeOfDay(timeOfDay);
        mTimeToFireNext = LocalTime.parse(timeOfDay).atDate(LocalDate.now(ZoneOffset.UTC));
        //Call this so that mTimeToFireNext is set to next day if fire time already passed today
        this.getNanosUntilNextTask();
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
        this(taskName, request, ChronoUnit.MONTHS, 1, "Once a month at " + timeOfDay + " on month day " + dayOfMonth);
        validateTimeOfDay(timeOfDay);
        validateDayOfMonth(dayOfMonth);
        //Check that the argument task time is valid
        mTimeToFireNext = LocalTime.parse(timeOfDay)
                .atDate(LocalDate.now(ZoneOffset.UTC))
                .withDayOfMonth(dayOfMonth);
        //Call this so that mTimeToFireNext is set to next month if fire time
        //already passed this month
        this.getNanosUntilNextTask();
    }


    /**
     * @param taskName       The name of the task.
     * @param request        The command to execute.
     * @param intervalUnit   The interval unit. E.g. Min, Months and so on. Used in
     *                       conjunction with intervalAmount.
     * @param intervalAmount The amount to wait. Used in conjunction with
     *                       intervalUnit.
     */
    private ScheduledTaskChannel(String taskName, String request, ChronoUnit intervalUnit, int intervalAmount, String taskTime) {
        mTaskName = taskName;
        mTaskRequest = request;
        mIntervalUnit = intervalUnit;
        mIntervalAmount = intervalAmount;
        mTaskTime = taskTime;
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
        //Convert request to byte array and add to request argument.
        request.add(mTaskRequest);
        //Get the number of nanoseconds the executing thread should sleep. 
        long nanosToSleep = this.getNanosUntilNextTask();
        //Put executing thread to sleep. 
        boolean wasNormalWakeUp = this.sleep(nanosToSleep);
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


    /**
     * Calculates and returns the number of milliseconds until is to run the
     * task again.
     * <p>
     * There is three cases when the while loop is used 1) Most common. Iterate
     * once. Just get the next time to run. 2) For minute tasks, when the tasks
     * to longer time to execute than the interval is. It will add intervals
     * until the next time is in the future. 3) For daily task. When starting
     * up. When the time of day has already passed one more day is added.
     *
     * @return The number of nanoseconds until next task should run.
     */
    long getNanosUntilNextTask() {
        //While the time to fire task has already passed
        //Note, Instant.now().isBefore(mTimeToFireNext) does not work. 
        while (!mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
            //Add another interval
            mTimeToFireNext = mTimeToFireNext.plus(mIntervalAmount, mIntervalUnit);
        }
        return Duration.between(LocalDateTime.now(ZoneOffset.UTC), mTimeToFireNext).toNanos();
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


    /**
     * Validates argument day of time string. Throws exception if not valid.
     *
     * @param timeOfDay Time-string to validate. E.g. "23:55"
     */
    private static void validateTimeOfDay(String timeOfDay) {
        Thrower.throwIfFalse(TIME_PATTERN.matcher(timeOfDay).matches())
                .message("Incorrect task time: '" + timeOfDay + "'. Correct format is HH:mm, e.g. 09:00 or 23:55.");
    }


    /**
     * @param dayOfMonth The day of the month. A number between 1 and 28.
     */
    private static void validateDayOfMonth(int dayOfMonth) {
        Thrower.throwIfVarOutsideRange(dayOfMonth, "dayOfMonth", 1, 28);
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
                .add("next_task_time_utc", DateTimeStrings.getDateTimeUTC(mTimeToFireNext.toInstant(ZoneOffset.UTC)))
                .build();
    }


}
