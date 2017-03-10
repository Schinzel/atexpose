package com.atexpose.api;

import com.atexpose.Expose;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.state.State;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-03-09.
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
@AllArgsConstructor
@Accessors(prefix = "m")
class Help {
    private final API mAPI;


    @Expose(
            arguments = {"SearchString", "Options"},
            requiredAccessLevel = 3,
            description = {"Returns help on the argument method.", "Wildcards \"*\" can be used."},
            labels = {"API"}
    )
    public String help(String searchString, String options) {
        //If not argument was passed
        if (Checker.isEmpty(searchString)) {
            //Set the argument to be help, which will return info on the command "help"
            searchString = "help";
        }
        //Get the methods that match the argument search string, sorted
        Stream<MethodObject> methodStream = mAPI.getMethods()
                .getUsingWildCards(searchString)
                .stream()
                .sorted();
        Stream<String> returnStream
                //If the verbose help return is requests
                = (options.contains("v"))
                //Get the state and then string from methods
                ? methodStream.map(MethodObject::getState).map(State::getString)
                //Get the concise syntax from the methods
                : methodStream.map(MethodObject::getSyntax);
        //Get the stream as a string
        String returnString = returnStream.collect(Collectors.joining("\n"));
        //If the return string is empty, i.e. there were no results.
        if (returnString.isEmpty()) {
            return "No matches found for '" + searchString + "'";
        }
        return returnString;
    }


}
