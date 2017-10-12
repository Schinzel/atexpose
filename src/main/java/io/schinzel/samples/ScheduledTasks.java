package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcherfactories.DispatcherFactory;
import com.atexpose.dispatcherfactories.TaskFactory;

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
                .startDispatcher(getMinuteTask())
                .startDispatcher(getDailyTask())
                .startDispatcher(getMonthlyTask())
                .startDispatcher(DispatcherFactory.cliBuilder().build());
    }


    private static Dispatcher getMinuteTask() {
        return TaskFactory.minuteTaskBuilder()
                .taskName("MyTask1")
                .request("time")
                .minutes(5)
                .build();
    }


    private static Dispatcher getDailyTask() {
        return TaskFactory.dailyTaskBuilder()
                .taskName("MyTask2")
                .request("echo hi")
                .timeOfDay("20:55")
                .zoneId("UTC")
                .build();
    }


    private static Dispatcher getMonthlyTask() {
        return TaskFactory.monthlyTaskBuilder()
                .taskName("MyTask3")
                .request("ping")
                .timeOfDay("07:00")
                .dayOfMonth(14)
                .zoneId("America/New_York\"")
                .build();

    }

}
