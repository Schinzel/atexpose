package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.FunnyChars;
import org.json.JSONObject;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MinuteIntervalTaskChannelTest {


    @Test
    public void constructor_IntervalTooLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new MinuteIntervalTaskChannel("theTaskName", "request", 0)
        );
    }


    @Test
    public void constructor_IntervalTooHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new MinuteIntervalTaskChannel("theTaskName", "request", 1441)
        );
    }


    @Test
    public void shutdown_RunningThread_mWasNormalWakeUpTrue() {
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("TheTaskName", "ThisIsTheTask", 1);
        Thread thread = new Thread(() -> stc.getRequest(new ByteStorage()));
        thread.start();
        //Interrupt the waiting task
        stc.shutdown(thread);
        //assert that false is returned as the wake-up was not normal, but a shutdown
        assertThat(stc.getShutdownWasInvoked()).isTrue();
    }


    @Test
    public void getRequest_ShutdownInvokedWhileWaiting_False() throws InterruptedException {
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("TheTaskName", "ThisIsTheTask", 1);
        AtomicBoolean bool = new AtomicBoolean(true);
        Thread thread = new Thread(() -> bool.set(stc.getRequest(new ByteStorage())));
        thread.start();
        new Thread(() -> stc.shutdown(thread)).start();
        thread.join();
        assertThat(bool).isFalse();
    }


    @Test
    public void getRequest_TaskTimeOccurs_True() {
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("The task 1", "ThisIsMyTask", 1);
        //override next-fire-time and set it to be a short time in the future
        long millisToSleep = 10;
        stc.mTimeToFireNext = ZonedDateTime.now(ZoneOffset.UTC).plusNanos(millisToSleep * 1_000_000);
        boolean wasNormalWakeUp = stc.getRequest(new ByteStorage());
        assertThat(wasNormalWakeUp).isTrue();
    }


    @Test
    public void getRequest_TaskTimeOccurs_ByteStorageReturnsSetRequest() {
        String request = FunnyChars.SERBO_CROATION_GAJ.getString();
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("The task 1", request, 1);
        //override next-fire-time and set it to be a short time in the future
        long millisToSleep = 10;
        stc.mTimeToFireNext = ZonedDateTime.now(ZoneOffset.UTC).plusNanos(millisToSleep * 1_000_000);
        ByteStorage byteStorage = new ByteStorage();
        stc.getRequest(byteStorage);
        assertThat(byteStorage.getAsString()).isEqualTo(request);
    }


    @Test
    public void getRequest_IntervalSetTo15Min_TimeToFireNextIs15Min() {
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("The task 1", "TheRequest", 15);
        new Thread(() -> stc.getRequest(new ByteStorage())).start();
        ZonedDateTime fifteenMinFromNow = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(15);
        assertThat(stc.mTimeToFireNext).isBetween(fifteenMinFromNow.minusSeconds(1), fifteenMinFromNow.plusSeconds(1));
    }


    @Test
    public void testSleep() {
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("TheTaskName", "TheRequest", 15);
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
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("TaskName", "TheRequest", 55);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_name")).isEqualTo("TaskName");
        assertThat(status.getString("request")).isEqualTo("TheRequest");
        assertThat(status.has("next_task_time")).isTrue();
    }


    @Test
    public void getState_MinuteIntervalTask_TaskTimeContainsMinuteInterval() {
        MinuteIntervalTaskChannel stc = new MinuteIntervalTaskChannel("TaskName", "TheRequest", 55);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).contains("55");
    }

}