package com.atexpose.api;

import com.atexpose.Expose;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
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
        assertEquals("String exposedMethod(String String)", methodHelp);
        
        String argsMissing = help.help("Chimp", "");
        assertEquals("No matches found for 'Chimp'", argsMissing);
    }

}
