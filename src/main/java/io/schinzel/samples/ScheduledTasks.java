package io.schinzel.samples;

import com.atexpose.AtExpose;

public class ScheduledTasks {
    public static void main(String[] args) {
        AtExpose.create()
                .addTask("MyTask1", "time", 5)
                .addDailyTask("MyTask2", "echo hi", "20:55", "UTC")
                .addMonthlyTask("MyTask3", "ping", "07:00", 14, "America/New_York")
                .startCLI();

    }
}
