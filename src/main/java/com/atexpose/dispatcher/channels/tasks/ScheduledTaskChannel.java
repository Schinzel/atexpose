package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.dispatcher.channels.IChannel;
import com.atexpose.util.ByteStorage;
import com.atexpose.util.DateTimeStrings;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.state.StateBuilder;

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
public class ScheduledTaskChannel implements IChannel {
    /** Pattern for time for daily tasks. */
    private static final Pattern TIME_PATTERN = Pattern.compile("^[0-2][0-9]:[0-5][0-9]");
    /** Flag for that day of month is not set, i.e. not used. */
    private static final int DAY_OF_MONTH_NOT_SET = -1;
    /** Flag for that time of day is not set, i.e. not used. */
    private static final String TIME_OF_DAY_NOT_SET = "NOT USED";
    /** The name of this tasks. */
    final String mTaskName;
    /** The task to run. Is a request in the text format. E.g. "echo hi" */
    final String mRequest;
    /** The time of day to run the task. Format HH:mm. E.g. 15:30 */
    final String mTaskTime;
    /** The day of month to fire the task. Min 1, max 28. */
    private final int mDayOfMonth;
    /**
     * The size of the interval. For daily tasks this is 1. For minute tasks
     * this is the number of minutes between tasks.
     */
    final int mIntervalAmount;
    /** The interval unit is days for daily tasks, and minute for minute tasks. */
    final TemporalUnit mIntervalUnit;
    /** A flag indicating if an explicit shutdown has been invoked. */
    private boolean mShutdownWasInvoked = false;
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
        this(taskName, request, ChronoUnit.MINUTES, intervalInMinutes, TIME_OF_DAY_NOT_SET, DAY_OF_MONTH_NOT_SET);
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
        this(taskName, request, ChronoUnit.DAYS, 1, timeOfDay, DAY_OF_MONTH_NOT_SET);
        mTimeToFireNext = LocalTime.parse(mTaskTime).atDate(LocalDate.now(ZoneOffset.UTC));
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
        this(taskName, request, ChronoUnit.MONTHS, 1, timeOfDay, dayOfMonth);
        mTimeToFireNext = LocalTime.parse(mTaskTime)
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
     * @param timeOfDay      What time of day to execute. Format HH:mm, e.g. 23:55
     * @param dayOfMonth     Day of month to execute. Min 1 and max 28.
     */
    private ScheduledTaskChannel(String taskName, String request, ChronoUnit intervalUnit, int intervalAmount, String timeOfDay, int dayOfMonth) {
        //Check that the argument task time is valid
        if (!ScheduledTaskChannel.isValidTime(timeOfDay)) {
            throw new RuntimeException("Incorrect task time: '" + timeOfDay + "'. Correct format is HH:mm, e.g. 09:00 or 23:55.");
        }
        //Check that the argument task time is valid
        if (!ScheduledTaskChannel.isValidDayOfMonth(dayOfMonth)) {
            throw new RuntimeException("Incorrect day of month: '" + dayOfMonth + "'. Needs to be min 1 or max 28.");
        }
        mTaskName = taskName;
        mRequest = request;
        mIntervalUnit = intervalUnit;
        mIntervalAmount = intervalAmount;
        mTaskTime = timeOfDay;
        mDayOfMonth = dayOfMonth;
    }


    @Override
    public void shutdown(Thread thread) {
        //Set the class scope shutdown variable to true
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
    @Override
    public boolean getRequest(ByteStorage request) {
        //Convert request to byte array and add to request argument.
        request.add(mRequest);
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
    final long getNanosUntilNextTask() {
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
     * @param time Time-string to validate.
     * @return True if the argument was a valid time of day, else false.
     */
    private static boolean isValidTime(String time) {
        return time.equalsIgnoreCase(TIME_OF_DAY_NOT_SET) || TIME_PATTERN.matcher(time).matches();
    }


    /**
     * @param dayOfMonth The day of the month. A number between 1 and 28 or -1 if is not-set-flag.
     * @return True if the argument was a valid day of month, else false.
     */
    private static boolean isValidDayOfMonth(int dayOfMonth) {
        return dayOfMonth == DAY_OF_MONTH_NOT_SET || (dayOfMonth >= 1 && dayOfMonth <= 28);
    }


    //------------------------------------------------------------------------
    // LOGGING & STATS
    //------------------------------------------------------------------------
    @Override
    public long requestReadTime() {
        //Hardcoded to one as the read time does not really exist
        return 0;
    }


    @Override
    public String senderInfo() {
        return "ScheduledTask: " + mTaskName;
    }


    public String getRequestAsString() {
        return mRequest;
    }


    @Override
    public State getState() {
        StateBuilder builder = State.getBuilder()
                .add("task_name", mTaskName)
                .add("request", mRequest);
        //If time of day has not been set
        if (mTaskTime.equalsIgnoreCase(TIME_OF_DAY_NOT_SET)) {
            builder.add("minutes", mIntervalAmount);
        } else {
            builder.add("time_of_day", mTaskTime);
        }
        //If day of month has been set
        if (mDayOfMonth != DAY_OF_MONTH_NOT_SET) {
            builder.add("day_of_month", mDayOfMonth);
        }
        builder.add("next_task_time_utc", DateTimeStrings.getDateTimeUTC(mTimeToFireNext.toInstant(ZoneOffset.UTC)));
        return builder.build();
    }


}
