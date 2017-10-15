package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.tasks.ScheduledReportChannel;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.mail.MockMailSender;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ScheduledReportFactoryTest extends ScheduledReportFactory {


    @Test
    public void getChannel_DefaultSetUp_ScheduledReportChannel() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        assertThat(dispatcher.getChannel())
                .isInstanceOf(ScheduledReportChannel.class);
    }


    @Test
    public void getParser_DefaultSetUp_TextParser() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        assertThat(dispatcher.getParser())
                .isInstanceOf(TextParser.class);
    }


    @Test
    public void getWrapper_DefaultSetUp_CsvWrapper() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        assertThat(dispatcher.getWrapper())
                .isInstanceOf(CsvWrapper.class);
    }


    @Test
    public void accessLevel_DefaultSetUp_3() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        assertThat(dispatcher.getAccessLevel())
                .isEqualTo(3);
    }


    @Test
    public void getKey_DefaultSetUp_ScheduledReport_MyTaskName() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        assertThat(dispatcher.getKey())
                .isEqualTo("ScheduledReport_MyTaskName");
    }


    @Test
    public void threadCount_DefaultSetUp_1() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        assertThat(dispatcher.getThreadNumber())
                .isEqualTo(1);
    }


    @Test
    public void isSynchronized_DefaultSetUp_False() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        assertThat(dispatcher.isSynchronized())
                .isEqualTo(false);
    }


    @Test
    public void getLogger_DefaultSetUp_EventLogger() {
        Dispatcher dispatcher = (Dispatcher) ScheduledReportFactory.builder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:50")
                .zoneId("UTC")
                .recipient("recip@example.com")
                .fromName("Me")
                .emailSender(new MockMailSender())
                .build();
        Logger logger = dispatcher.getLoggers().get(0);
        assertThat(logger.getLoggerType())
                .isInstanceOf(LoggerType.EVENT.getClass());
    }

}