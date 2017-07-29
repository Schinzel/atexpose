package com.atexpose.dispatcher.channels.tasks;

import java.time.*;

public class REMOVE_ME {
    public static void main(String[] args) {
        String timeOfDay = "11:30";
        LocalTime.parse(timeOfDay).atDate(LocalDate.now(ZoneOffset.UTC));
        LocalTime.parse(timeOfDay).atDate(LocalDate.now(ZoneOffset.UTC)).atZone(ZoneId.of("UTC"));
        ZonedDateTime.now(ZoneId.of("UTC")).minusSeconds(1);
        LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).toLocalDate();


    }
}
