package io.schinzel.samples;


import com.atexpose.AtExpose;
import com.atexpose.api.Argument;
import com.atexpose.api.data_types.ClassDT;
import com.atexpose.api.data_types.DataTypeEnum;
import io.schinzel.samples.sample10.Person;
import io.schinzel.samples.sample10.Position;
import io.schinzel.samples.sample10.SampleAPI;

/**
 * The purpose of this sample is to show how to use custom arguments with custom classes and
 * enums in @expose.
 * <p>
 * Load page: http://127.0.0.1:5555/
 */
public class Sample10 {

    public static void main(String[] args) {
        AtExpose.create()
                // Add new data types
                .addDataType(Person.class)
                .addDataType(Position.class)
                // Create a an argument "manager" that is of data type "Person"
                .addArgument(Argument.builder()
                        .name("manager")
                        .dataType(new ClassDT<>(Person.class))
                        .description("A manager")
                        .build())
                // Create a an argument "position" that is of data type "Position"
                .addArgument(Argument.builder()
                        .name("position")
                        .dataType(new ClassDT<>(Position.class))
                        .defaultValue('"' + Position.FIRST.name() + '"')
                        .description("The position")
                        .build())
                // Create an argument "sample_arg" that is of data type "String"
                // with a reg ex that defines which chars are allowed.
                .addArgument(Argument.builder()
                        .name("sample_arg")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .allowedCharsRegEx("[a-z0-9_@.-]*")
                        .defaultValue("default-value")
                        .description("Description of the sample argument")
                        .build())
                // Expose an API
                .expose(SampleAPI.class)
                // Create a client for calling the server. Person and position will be translated
                // to JavaScript and will be available as class and object
                .generateJavaScriptClient("src/main/resources/web/sample10/ServerCaller.js")
                // Start a web server
                .startWebServer("web/sample10");
        System.out.println("System started!");
    }
}
