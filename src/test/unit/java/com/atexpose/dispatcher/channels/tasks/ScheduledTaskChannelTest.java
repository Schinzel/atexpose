package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.FunnyChars;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;
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


    /**
     * Util method
     *
     * @return A scheduled task channel
     */
    static ScheduledTaskChannel getTestChannel() {
        return new ScheduledTaskChannel("TheTaskName", "MyRequest", ChronoUnit.MINUTES, 15, "", ZonedDateTime.now(), Watch.create());

    }


    @Test
    public void constructor_SetCustomWatch_CustomWatch() {
        Instant myInstant = Instant.now();
        ScheduledTaskChannel dailyTaskChannel = new ScheduledTaskChannel("TheTaskName",
                "MyRequest", ChronoUnit.MINUTES, 15, "",
                ZonedDateTime.now(), Watch.create().setInstant(myInstant));
        assertThat(dailyTaskChannel.mWatch.getInstant()).isEqualTo(myInstant);
    }


    @Test
    public void constructor_DefaultWatch_WatchReturnsNow() {
        Instant start = Instant.now();
        ScheduledTaskChannel dailyTaskChannel = getTestChannel();
        assertThat(dailyTaskChannel.mWatch.getInstant()).isBetween(start, Instant.now());
    }


    @Test
    public void getClone_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                getTestChannel().getClone());
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
    public void getRequest_TaskTimeOccurs_ByteStorageReturnsSetRequest() {
        String request = FunnyChars.SERBO_CROATION_GAJ.getString();
        ScheduledTaskChannel stc = new ScheduledTaskChannel("TheTaskName", request, ChronoUnit.MILLIS, 10, "", ZonedDateTime.now(), Watch.create());
        //override next-fire-time and set it to be a short time in the future
        ByteStorage byteStorage = new ByteStorage();
        stc.getRequest(byteStorage);
        assertThat(byteStorage.getAsString()).isEqualTo(request);
    }


    @Test
    public void getState_NormalCase_CheckThatLabelsAreThere() {
        ScheduledTaskChannel stc = getTestChannel();
        JSONObject status = stc.getState().getJson();
    }


    @Test
    public void getTaskRequest_SetInConstructorMyRequest_MyRequest() {
        String actualRequest = getTestChannel().getTaskRequest();
        assertThat(actualRequest).isEqualTo("MyRequest");
    }


    @Test
    public void requestReadTime_NormalCase_0() {
        long actualReadTime = getTestChannel().requestReadTime();
        assertThat(actualReadTime).isEqualTo(0);
    }


    @Test
    public void responseWriteTime_NormalCase_0() {
        long actual = getTestChannel().responseWriteTime();
        assertThat(actual).isZero();
    }


    @Test
    public void senderInfo_NormalCase_TaskName() {
        String actualSenderInfo = getTestChannel().senderInfo();
        assertThat(actualSenderInfo).isEqualTo("ScheduledTask: TheTaskName");
    }


    @Test
    public void shutdown_RunningThread_mWasNormalWakeUpTrue() {
        ScheduledTaskChannelMinute stc = new ScheduledTaskChannelMinute("TheTaskName", "ThisIsTheTask", 1);
        Thread thread = new Thread(() -> stc.getRequest(new ByteStorage()));
        thread.start();
        //Interrupt the waiting task
        stc.shutdown(thread);
        //assert that false is returned as the wake-up was not normal, but a shutdown
        assertThat(stc.getShutdownWasInvoked()).isTrue();
    }


    @Test
    public void writeResponse_DoesNothingButDoesNotThrowException() {
        getTestChannel().writeResponse(null);
    }


}
