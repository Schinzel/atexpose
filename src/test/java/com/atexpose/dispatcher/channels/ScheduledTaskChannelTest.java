package com.atexpose.dispatcher.channels;

import com.atexpose.util.ByteStorage;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

/**
 * @author Schinzel
 */
public class ScheduledTaskChannelTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testException_ConstructorToLowMinutes() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 0;
        exception.expect(RuntimeException.class);
        new ScheduledTaskChannel(taskName, request, interval);
    }


    @Test
    public void testException_ConstructorToHighMinutes() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 1441;
        exception.expect(RuntimeException.class);
        new ScheduledTaskChannel(taskName, request, interval);
    }


    @Test
    public void testException_InvalidTimeFormat() {
        String taskName = "theTaskName";
        String request = "request";
        String timeOfDay = "1441";
        exception.expect(RuntimeException.class);
        exception.expectMessage("Incorrect task time");
        new ScheduledTaskChannel(taskName, request, timeOfDay);
    }


    @Test
    public void testException_InvalidDayOfMonth1() {
        String taskName = "theTaskName";
        String request = "request";
        String timeOfDay = "23:55";
        int dayOfMonth = 0;
        exception.expect(RuntimeException.class);
        exception.expectMessage("Incorrect day of month");
        new ScheduledTaskChannel(taskName, request, timeOfDay, dayOfMonth);
    }


    @Test
    public void testException_InvalidDayOfMonth2() {
        String taskName = "theTaskName";
        String request = "request";
        String timeOfDay = "23:55";
        int dayOfMonth = 29;
        exception.expect(RuntimeException.class);
        exception.expectMessage("Incorrect day of month");
        new ScheduledTaskChannel(taskName, request, timeOfDay, dayOfMonth);
    }


    @Test
    public void test_FireTime_MinutesTask() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 15;
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, interval);
        assertEquals(interval, stc.mIntervalAmount);
        assertEquals(ChronoUnit.MINUTES, stc.mIntervalUnit);
        int timeInSeconds = 15 * 60;
        //Time to fire is after interval - 1 seconds
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC).plus(timeInSeconds - 1, ChronoUnit.SECONDS)));
        //Time to fire is before interval + 1 seconds
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(timeInSeconds + 1, ChronoUnit.SECONDS)));
    }


    @Test
    public void test_FireTime_TimeOfDayTask() {
        String taskName = "theTaskName";
        String request = "request";
        String timeOfDay = "08:50";
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTaskTime);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire shoulb be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
        //
        timeOfDay = "14:55";
        stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTaskTime);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire shoulb be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
        //
        timeOfDay = "21:00";
        stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTaskTime);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire shoulb be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
        //
        timeOfDay = "04:06";
        stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTaskTime);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire shoulb be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
    }


    @Test
    public void test_FireTime_MonthlyTask() {
        //Get day of month 25 days and 2 minutes from now
        LocalDateTime ldtFuture = LocalDateTime.now(ZoneOffset.UTC)
                .plus(25, ChronoUnit.DAYS)
                .plus(2, ChronoUnit.MINUTES);
        while (ldtFuture.getDayOfMonth() > 28) {
            ldtFuture = ldtFuture.minusDays(1);
        }
        //Get the time of the day in the future
        String timeOfDay = DateTimeFormatter.ofPattern("HH:mm").format(ldtFuture);
        //Get the day of month in the future
        int dayOfMonth = ldtFuture.getDayOfMonth();
        //Create the scheduled task
        ScheduledTaskChannel stc = new ScheduledTaskChannel("taskName", "request", timeOfDay, dayOfMonth);
        //Get the number of nanos the task will sleep until it fires
        long sleepTime = stc.getNanosUntilNextTask();
        //Add the sleep time to now
        LocalDateTime ldtWakeUpTime = LocalDateTime.now(ZoneOffset.UTC).plus(sleepTime, ChronoUnit.NANOS);
        //Get the diff between wake-up-time and the future
        long diff = Duration.between(ldtWakeUpTime, ldtFuture).toMillis();
        //Check that the diff between future and wake-up-time is less than 60 seconds
        //There is a diff as the wake-up-time is set to whole minutes
        assertTrue(diff < 60000);
    }


    @Test
    public void testGetClone() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 15;
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, interval);
        exception.expect(UnsupportedOperationException.class);
        stc.getClone();
    }


    @Test
    public void testSleep() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 15;
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, interval);
        long millisToSleep = 20;
        long nanosToSleep = millisToSleep * 1000000;
        long start = System.nanoTime();
        stc.sleep(nanosToSleep);
        //Calc the time to do all iterations
        long executionTimeInMS = (System.nanoTime() - start) / 1000000;
        Assert.assertThat(executionTimeInMS, Matchers.lessThan(30l));
        Assert.assertThat(executionTimeInMS, Matchers.greaterThan(20l));

    }


    @Test
    public void testWriteResponse() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 15;
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, interval);
        stc.writeResponse(null);
    }


    @Test
    public void testRequestReadTime() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 15;
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, interval);
        assertEquals(0, stc.requestReadTime());
    }


    @Test
    public void testSenderInfo() {
        String taskName = "theTaskName";
        String request = "request";
        int interval = 15;
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, interval);
        assertEquals("ScheduledTask: " + taskName, stc.senderInfo());
    }


    @Test
    public void testGetRequestAsString() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("The task 1", "time", 55);
        assertEquals("time", stc.getRequestAsString());
    }


    @Test
    public void testGetRequestAsBytes() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("The task 1", "thisIsAtask", 1);
        //override next-fire-time and set it to be one second from now
        stc.mTimeToFireNext = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);
        ByteStorage bs = new ByteStorage();
        boolean wasNormalWakeUp = stc.getRequest(bs);
        assertTrue(wasNormalWakeUp);
        assertEquals("thisIsAtask", bs.getAsString());
    }


    @Test
    public void testStatus() {
        ScheduledTaskChannel stc;
        JSONObject status;
        //Test minute interval task
        stc = new ScheduledTaskChannel("The task 1", "time", 55);
        status = stc.getState().getJson();
        assertEquals("The task 1", status.getString("task_name"));
        assertEquals("time", status.getString("request"));
        assertEquals(55, status.getInt("minutes"));
        assertFalse(status.has("time"));
        assertFalse(status.has("day_of_month"));
        assertTrue(status.has("next_task_time_utc"));
        //Test time of day task
        stc = new ScheduledTaskChannel("The task 2", "getStatus", "23:55");
        status = stc.getState().getJson();
        assertEquals("The task 2", status.getString("task_name"));
        assertEquals("getStatus", status.getString("request"));
        assertEquals("23:55", status.getString("time_of_day"));
        assertFalse(status.has("minutes"));
        assertFalse(status.has("day_of_month"));
        assertTrue(status.has("next_task_time_utc"));
        //Test day of month task
        stc = new ScheduledTaskChannel("The task 2", "getStatus", "23:55", 28);
        status = stc.getState().getJson();
        assertEquals("The task 2", status.getString("task_name"));
        assertEquals("getStatus", status.getString("request"));
        assertEquals("23:55", status.getString("time_of_day"));
        assertEquals(28, status.getInt("day_of_month"));
        assertFalse(status.has("minutes"));
        assertTrue(status.has("next_task_time_utc"));
    }


    @Test
    public void testShutdown() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("The task 1", "thisIsAtask", 1);
        //override next-fire-time and set it to be one second from now
        stc.mTimeToFireNext = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);
        //Start another thread that waits on scheduled task to finish
        TaskRunner tr = new TaskRunner(stc);
        Thread thread = new Thread(tr);
        thread.start();
        //Interrupt the waiting task but requesting a shutdown
        stc.shutdown(thread);
        //assert that false is returned as the wake-up was not normal, but a shutwon
        assertFalse(tr.mWasNormalWakeUp);
    }


    /**
     * Help class that waits for a task to wake up.
     */
    static class TaskRunner implements Runnable {
        private final ScheduledTaskChannel mStc;
        boolean mWasNormalWakeUp;


        TaskRunner(ScheduledTaskChannel stc) {
            mStc = stc;
        }


        @Override
        public void run() {
            ByteStorage byteStorage = new ByteStorage();
            mWasNormalWakeUp = mStc.getRequest(byteStorage);
        }

    }
}
