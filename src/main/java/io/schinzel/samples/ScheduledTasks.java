package io.schinzel.samples;

import com.atexpose.AtExpose;

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
                .addMinuteTask("MyTask1", "time", 5)
                .addDailyTask("MyTask2", "echo hi", "20:55", "UTC")
                .addMonthlyTask("MyTask3", "ping", "07:00", 14, "America/New_York")
                .startCLI();
    }
}
