package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher_factories.ScheduledTaskFactory;

/**
 * <p>
 * The purpose of this sample is to show how to set up scheduled tasks
 * </p>
 * <p>
 * Run below sample and type 'status' in command line interface. This displays the different
 * tasks currently running
 * </p>
 */

public class Sample6_ScheduledTasks {
    public static void main(String[] args) {
        AtExpose.create()
                .start(getMinuteTask())
                .start(getDailyTask())
                .start(getHourlyTask())
                .start(getMonthlyTask())
                .startCLI();
    }


    private static IDispatcher getMinuteTask() {
        return ScheduledTaskFactory.minuteTaskBuilder()
                .taskName("MyTask1")
                .request("time")
                .minutes(5)
                .build();
    }


    private static IDispatcher getHourlyTask() {
        return ScheduledTaskFactory.hourlyTaskBuilder()
                .taskName("MyTask2")
                .request("time")
                .minuteOfTheHour(10)
                .timeZone("UTC")
                .build();
    }


    private static IDispatcher getDailyTask() {
        return ScheduledTaskFactory.dailyTaskBuilder()
                .taskName("MyTask3")
                .request("echo hi")
                .timeOfDay("20:55")
                .timeZone("UTC")
                .build();
    }


    private static IDispatcher getMonthlyTask() {
        return ScheduledTaskFactory.monthlyTaskBuilder()
                .taskName("MyTask4")
                .request("ping")
                .timeOfDay("07:00")
                .dayOfMonth(14)
                .timeZone("America/New_York")
                .build();
    }
}