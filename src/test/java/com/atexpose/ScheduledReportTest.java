package com.atexpose;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * @author Schinzel
 */
public class ScheduledReportTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void test_addScheduledReport_NO_SMTP() {
        String taskName = "theTaskName";
        String request = "ping";
        String timeOfDay = "14:30";
        AtExpose cc = new AtExpose();
        exception.expect(RuntimeException.class);
        exception.expectMessage("You need to set SMTP settings");
        cc.addScheduledReport(taskName, request, timeOfDay, "monkey@example.com", "fromName");
    }


    @Test
    public void test_addScheduledReport() throws JSONException {
        String taskName = "theTaskName";
        String request = "ping";
        String timeOfDay = "14:30";
        AtExpose atExpose = AtExpose.create();
        atExpose.setSMTPServerGmail("u1", "p1");
        atExpose.addScheduledReport(taskName, request, timeOfDay, "monkey@example.com", "fn1");
        JSONObject jo = atExpose.getState().getJson();
        JSONObject joDispatcher = jo.getJSONArray("Dispatchers").getJSONObject(0);
        assertEquals("ScheduledReport_" + taskName, joDispatcher.getString("Name"));
    }


}
