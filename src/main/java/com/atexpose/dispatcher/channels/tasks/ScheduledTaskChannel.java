package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.dispatcher.channels.IChannel;
import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

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
        mTimeToFireNext = getNextTaskTime(initialTaskFireTime, intervalAmount, intervalUnit, watch);
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


    @Override
    public boolean getRequest(ByteStorage request) {
        //Get the number of nanoseconds the executing thread should sleep.
        long nanosToSleep = Duration.between(mWatch.getInstant(), mTimeToFireNext).toNanos();
        try {
            //Put executing thread to sleep.
            TimeUnit.NANOSECONDS.sleep(nanosToSleep);
        } catch (InterruptedException ex) {
            //If another thread had invoked shutdown, i.e. this is an everything-is-order shutdown
            if (mShutdownWasInvoked) {
                return false;
            } else {
                throw new RuntimeException("Invocation error in scheduled task. Sleep was interrupted in a unexpected manner. " + ex.getMessage());
            }
        }
        //Convert request to byte array and add to request argument.
        request.add(mTaskRequest);
        //Calc the next time to fire task
        mTimeToFireNext = getNextTaskTime(mTimeToFireNext, mIntervalAmount, mIntervalUnit, mWatch);
        //Return true if was normal wake up. Else return false.
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


    static ZonedDateTime getNextTaskTime(ZonedDateTime time, int intervalAmount, TemporalUnit intervalUnit, IWatch watch) {
        //Note, Instant.now().isBefore(mTimeToFireNext) does not work.
        return (!time.isAfter(ZonedDateTime.ofInstant(watch.getInstant(), time.getZone())))
                ? getNextTaskTime(time.plus(intervalAmount, intervalUnit), intervalAmount, intervalUnit, watch)
                : time;
    }


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
