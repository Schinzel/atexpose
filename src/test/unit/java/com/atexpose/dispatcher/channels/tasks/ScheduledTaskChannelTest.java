package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.ByteStorage;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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


    static ScheduledTaskChannel getTestChannel() {
        return new ScheduledTaskChannel("TheTaskName", "MyRequest", ChronoUnit.MINUTES, 15, "", ZonedDateTime.now(), Watch.create());

    }


    @Test
    public void getClone_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                getTestChannel().getClone());
    }


    @Test
    public void writeResponse_DoesNothingButDoesNotThrowException() {
        getTestChannel().writeResponse(null);
    }


    @Test
    public void requestReadTime_NormalCase_0() {
        long actualReadTime = getTestChannel().requestReadTime();
        assertThat(actualReadTime).isEqualTo(0);
    }


    @Test
    public void senderInfo_NormalCase_TaskName() {
        String actualSenderInfo = getTestChannel().senderInfo();
        assertThat(actualSenderInfo).isEqualTo("ScheduledTask: TheTaskName");
    }


    @Test
    public void getTaskRequest_SetInConstructorMyRequest_MyRequest() {
        String actualRequest = getTestChannel().getTaskRequest();
        assertThat(actualRequest).isEqualTo("MyRequest");
    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount1_UnitMinutes__1MinAfterArgumentTime() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 1, ChronoUnit.MINUTES, Watch.create());
        assertThat(actual).isEqualToIgnoringSeconds(taskTime.plusMinutes(1));
    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount17_UnitMinutes__17MinAfterArgumentTime() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 17, ChronoUnit.MINUTES, Watch.create());
        assertThat(actual).isEqualTo(taskTime.plusMinutes(17));
    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount2_UnitDays__2DaysAfterArgumentTime() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 2, ChronoUnit.DAYS, Watch.create());
        assertThat(actual).isEqualTo(taskTime.plusDays(2));
    }


    @Test
    public void getNextTaskTime__1SecondAgo_Amount1_UnitMonths__1MonthAfterArgumentTime() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 1, ChronoUnit.MONTHS, Watch.create());
        assertThat(actual).isEqualTo(taskTime.plusMonths(1));
    }


    @Test
    public void getNextTaskTime__TaskTime10SecondsInTheFuture__ReturnTimeShouldBeArgumentTime() {
        ZonedDateTime taskTime = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(10);
        ZonedDateTime actual = ScheduledTaskChannel.getNextTaskTime(taskTime, 13, ChronoUnit.MINUTES, Watch.create());
        assertThat(actual).isEqualTo(taskTime);
    }


    @Test
    public void getRequest_Interrupt_Exception() throws InterruptedException {
        ScheduledTaskChannel stc = getTestChannel();
        AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        Thread sleepThread = new Thread(() -> {
            try {
                stc.getRequest(new ByteStorage());
            } catch (RuntimeException e) {
                exceptionThrown.set(true);
            }
        });
        sleepThread.start();
        new Thread(() -> sleepThread.interrupt()).start();
        sleepThread.join();
        assertThat(exceptionThrown).isTrue();
    }


    @Test
    public void getRequest_SleepEnds_True() throws InterruptedException {
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TheTaskName", "MyRequest", ChronoUnit.MILLIS, 50, "", ZonedDateTime.now(), Watch.create());
        boolean actual = stc.getRequest(new ByteStorage());
        assertThat(actual).isTrue();
    }


    @Test
    public void getRequest_Shutdown_False() throws InterruptedException {
        ScheduledTaskChannel stc = getTestChannel();
        AtomicBoolean sleepReturn = new AtomicBoolean(true);
        Thread sleepThread = new Thread(() -> sleepReturn.set(stc.getRequest(new ByteStorage())));
        sleepThread.start();
        new Thread(() -> stc.shutdown(sleepThread)).start();
        sleepThread.join();
        assertThat(sleepReturn).isFalse();
    }


    @Test
    public void responseWriteTime_NormalCase_0() {
        long actual = getTestChannel().responseWriteTime();
        assertThat(actual).isZero();
    }


    @Test
    public void getState_NormalCase_CheckThatLabelsAreThere() {
        ScheduledTaskChannel stc = getTestChannel();
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_name")).isEqualTo("TheTaskName");
        assertThat(status.getString("request")).isEqualTo("MyRequest");
        assertThat(status.has("next_task_time")).isTrue();
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
