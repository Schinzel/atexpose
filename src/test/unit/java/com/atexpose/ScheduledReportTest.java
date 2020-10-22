package com.atexpose;

import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcherfactories.ScheduledReportFactory;
import com.atexpose.util.mail.MockMailSender;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Schinzel
 */
public class ScheduledReportTest {

    @Test
    public void test_addScheduledReport() throws JSONException {
        IDispatcher scheduledReport = ScheduledReportFactory.builder()
                .taskName("theTaskName")
                .request("ping")
                .timeOfDay("14:30")
                .timeZone("UTC")
                .emailSender(new MockMailSender())
                .fromName("fromName")
                .build();
        AtExpose atExpose = AtExpose.create()
                .start(scheduledReport);
        JSONObject jo = atExpose.getState().getJson();
        JSONObject joDispatcher = jo.getJSONArray("Dispatchers").getJSONObject(0);
        assertEquals("ScheduledReport_theTaskName", joDispatcher.getString("Name"));
    }


}
