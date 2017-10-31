package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcherfactories.CliFactory;
import com.atexpose.dispatcherfactories.ScheduledTaskFactory;

/**
 * In this sample scheduled tasks are set up.
 * <p>
 * Things to try:
 * - Run below sample and type 'status' in command line interface.
 * - In cli type 'addMinuteTask anyname, time, 1' to type a task
 * - In cli type 'removeTask MyTask3' to remove a task
 * - In cli type 'help *task*' to see all methods that deals with taks.
 */

public class ScheduledTasks {
    public static void main(String[] args) {
        AtExpose.create()
                .start(getMinuteTask())
                .start(getDailyTask())
                .start(getMonthlyTask())
                .start(CliFactory.create());
    }


    private static IDispatcher getMinuteTask() {
        return ScheduledTaskFactory.minuteTaskBuilder()
                .taskName("MyTask1")
                .request("time")
                .minutes(5)
                .build();
    }


    private static IDispatcher getDailyTask() {
        return ScheduledTaskFactory.dailyTaskBuilder()
                .taskName("MyTask2")
                .request("echo hi")
                .timeOfDay("20:55")
                .timeZone("UTC")
                .build();
    }


    private static IDispatcher getMonthlyTask() {
        return ScheduledTaskFactory.monthlyTaskBuilder()
                .taskName("MyTask3")
                .request("ping")
                .timeOfDay("07:00")
                .dayOfMonth(14)
                .timeZone("America/New_York\"")
                .build();

    }

}
