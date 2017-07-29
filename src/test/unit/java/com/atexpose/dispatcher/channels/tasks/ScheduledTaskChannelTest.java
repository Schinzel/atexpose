package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.FunnyChars;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


/**
 * @author Schinzel
 */
public class ScheduledTaskChannelTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void constructor_IntervalTooLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new ScheduledTaskChannel("theTaskName", "request", 0)
        );
    }


    @Test
    public void constructor_IntervalTooHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new ScheduledTaskChannel("theTaskName", "request", 1441)
        );
    }


    @Test
    public void constructor_InvalidTimeFormat_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new ScheduledTaskChannel("theTaskName", "request", "12345678"))
                .withMessageStartingWith("Incorrect task time: ");
    }


    @Test
    public void constructor_DayOfMonthTooLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new ScheduledTaskChannel("theTaskName", "request", "23:55", 0))
                .withMessageStartingWith("The value 0 in variable 'dayOfMonth' is too small.");
    }


    @Test
    public void constructor_DayOfMonthTooHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new ScheduledTaskChannel("theTaskName", "request", "23:55", 29))
                .withMessageStartingWith("The value 29 in variable 'dayOfMonth' is too large");
    }


    @Test
    public void getClone_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new ScheduledTaskChannel("theTaskName", "request", 15).getClone()
                );
    }


    @Test
    public void writeResponse_DoesNothingButDoesNotThrowException() {
        new ScheduledTaskChannel("theTaskName", "request", 15).writeResponse(null);
    }


    @Test
    public void testRequestReadTime() {
        long actualReadTime = new ScheduledTaskChannel("theTaskName", "request", 15)
                .requestReadTime();
        assertThat(actualReadTime).isEqualTo(0);
    }


    @Test
    public void testSenderInfo() {
        String actualSenderInfo = new ScheduledTaskChannel("TheTaskName", "request", 15)
                .senderInfo();
        assertThat(actualSenderInfo).isEqualTo("ScheduledTask: TheTaskName");
    }


    @Test
    public void getTaskRequest_SetInConstructorMyRequest_MyRequest() {
        String actualRequest = new ScheduledTaskChannel("The task 1", "MyRequest", 55)
                .getTaskRequest();
        assertThat(actualRequest).isEqualTo("MyRequest");
    }


    @Test
    public void shutdown_RunningThread_mWasNormalWakeUpTrue() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TheTaskName", "ThisIsTheTask", 1);
        Thread thread = new Thread(() -> stc.getRequest(new ByteStorage()));
        thread.start();
        //Interrupt the waiting task
        stc.shutdown(thread);
        //assert that false is returned as the wake-up was not normal, but a shutdown
        assertThat(stc.getShutdownWasInvoked()).isTrue();
    }


    @Test
    public void getRequest_ShutdownInvokedWhileWaiting_False() throws InterruptedException {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TheTaskName", "ThisIsTheTask", 1);
        AtomicBoolean bool = new AtomicBoolean(true);
        Thread thread = new Thread(() -> bool.set(stc.getRequest(new ByteStorage())));
        thread.start();
        new Thread(() -> stc.shutdown(thread)).start();
        thread.join();
        assertThat(bool).isFalse();
    }


    @Test
    public void getRequest_TaskTimeOccurs_True() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("The task 1", "ThisIsMyTask", 1);
        //override next-fire-time and set it to be a short time in the future
        long millisToSleep = 10;
        stc.mTimeToFireNext = ZonedDateTime.now(ZoneOffset.UTC).plusNanos(millisToSleep * 1_000_000);
        boolean wasNormalWakeUp = stc.getRequest(new ByteStorage());
        assertThat(wasNormalWakeUp).isTrue();
    }


    @Test
    public void getRequest_TaskTimeOccurs_ByteStorageReturnsSetRequest() {
        String request = FunnyChars.SERBO_CROATION_GAJ.getString();
        ScheduledTaskChannel stc = new ScheduledTaskChannel("The task 1", request, 1);
        //override next-fire-time and set it to be a short time in the future
        long millisToSleep = 10;
        stc.mTimeToFireNext = ZonedDateTime.now(ZoneOffset.UTC).plusNanos(millisToSleep * 1_000_000);
        ByteStorage byteStorage = new ByteStorage();
        stc.getRequest(byteStorage);
        assertThat(byteStorage.getAsString()).isEqualTo(request);
    }


    @Test
    public void getRequest_IntervalSetTo15Min_TimeToFireNextIs15Min() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("The task 1", "TheRequest", 15);
        new Thread(() -> stc.getRequest(new ByteStorage())).start();
        ZonedDateTime fifteenMinFromNow = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(15);
        assertThat(stc.mTimeToFireNext).isBetween(fifteenMinFromNow.minusSeconds(1), fifteenMinFromNow.plusSeconds(1));
    }


    @Test
    public void testSleep() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TheTaskName", "TheRequest", 15);
        long millisToSleep = 20;
        long nanosToSleep = millisToSleep * 1_000_000;
        long start = System.nanoTime();
        stc.sleep(nanosToSleep);
        //Calc the time to do all iterations
        long executionTimeInMS = (System.nanoTime() - start) / 1_000_000;
        assertThat(executionTimeInMS).isBetween(20L, 30L);
    }


    @Test
    public void getState_() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TaskName", "TheRequest", 55);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_name")).isEqualTo("TaskName");
        assertThat(status.getString("request")).isEqualTo("TheRequest");
        assertThat(status.has("next_task_time")).isTrue();
    }


    @Test
    public void getState_MinuteIntervalTask_TaskTimeContainsMinuteInterval() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TaskName", "TheRequest", 55);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).contains("55");
    }


    @Test
    public void getState_DailyTask_TaskTimeContainsSetTimeOfDay() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TaskName", "TheRequest", "23:57");
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).contains("23:57");
    }


    @Test
    public void getState_MonthlyTask_TaskTimeContainsTimeOfDayAndDayOfMonth() {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TaskName3", "TheRequest3", "23:53", 28);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).contains("23:53").contains("28");
    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount1_UnitMinutes__1MinAfterNow() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 1, ChronoUnit.MINUTES, Watch.create());
        assertThat(actual).isEqualToIgnoringSeconds(ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(1));
    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount17_UnitMinutes__17MinAfterNow() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 17, ChronoUnit.MINUTES, Watch.create());
        assertThat(actual).isEqualToIgnoringSeconds(ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(17));
    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount2_UnitDays__2DaysAfterNow() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 2, ChronoUnit.DAYS, Watch.create());
        assertThat(actual).isEqualToIgnoringSeconds(ZonedDateTime.now(ZoneId.of("UTC")).plusDays(2));

    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount1_UnitMonths__1MonthAfterNow() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 1, ChronoUnit.MONTHS, Watch.create());
        assertThat(actual).isEqualToIgnoringSeconds(ZonedDateTime.now(ZoneId.of("UTC")).plusMonths(1));
    }


    @Test
    public void getNextTaskTime__10SecondsInTheFuture_Amount13_UnitMinutes__ReturnTimeShouldBeArgumentTime() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(10);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 13, ChronoUnit.MINUTES, Watch.create());
        assertThat(actual).isEqualToIgnoringSeconds(ZonedDateTime.now(ZoneId.of("UTC")));
    }


    @Test
    public void getNextTaskTime__10SecondsInTheFuture_Amount1_UnitMonths__ReturnTimeShouldBeArgumentTime() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(10);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 1, ChronoUnit.MONTHS, Watch.create());
        assertThat(actual).isEqualToIgnoringSeconds(ZonedDateTime.now(ZoneId.of("UTC")));
    }




    /*    @Test
    public void test_FireTime_TimeOfDayTask() {
        String taskName = "theTaskName";
        String request = "request";
        String timeOfDay = "08:50";
        ScheduledTaskChannel stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTimeOfDay);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire should be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
        //
        timeOfDay = "14:55";
        stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTimeOfDay);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire should be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
        //
        timeOfDay = "21:00";
        stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTimeOfDay);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire should be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
        //
        timeOfDay = "04:06";
        stc = new ScheduledTaskChannel(taskName, request, timeOfDay);
        assertEquals(1, stc.mIntervalAmount);
        assertEquals(ChronoUnit.DAYS, stc.mIntervalUnit);
        assertEquals(timeOfDay, stc.mTimeOfDay);
        //Time to fire should be after now
        assertTrue(stc.mTimeToFireNext.isAfter(LocalDateTime.now(ZoneOffset.UTC)));
        //Time to fire should be within 24 hours
        assertTrue(stc.mTimeToFireNext.isBefore(LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS)));
    }*/


/*
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
        assertTrue(diff < 60_000);
    }
*/

}
