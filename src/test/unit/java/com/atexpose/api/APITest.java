package com.atexpose.api;

import com.atexpose.Expose;
import com.atexpose.api.datatypes.DataTypeEnum;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


/**
 * @author Jorgen Andersson <jorgen@kollektiva.se>
 */
public class APITest {

    @SuppressWarnings("unused")
    public static class ExposedClass {
        @Expose(
                arguments = {"String"},
                labels = {"TestLabel"},
                requiredArgumentCount = 1
        )
        public static String exposedMethod(String input) {
            return input;
        }
    }


    @Test
    public void testApiHelp() {
        API api = new API()
                .addLabel("TestLabel", "This label is for test")
                .expose(ExposedClass.class);
        Help help = new Help(api);
        String methodHelp = help.help("exposedMethod", "");
        assertEquals("string exposedMethod(string String)", methodHelp);
        String argsMissing = help.help("Chimp", "");
        assertEquals("No matches found for 'Chimp'", argsMissing);
    }


    @Test
    public void addArgument_ArgumentAdded_ExistsArgumentWithSetDescription() {
        API api = new API().addArgument("MyName", DataTypeEnum.STRING, "My description");
        Argument argument = api.getArguments()
                .get("MyName");
        assertThat(argument.getDescription()).isEqualTo("My description");
    }

}
