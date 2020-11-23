package io.schinzel.samples;


import com.atexpose.AtExpose;
import com.atexpose.api.Argument;
import com.atexpose.api.data_types.ClassDT;
import com.atexpose.api.data_types.DataTypeEnum;
import io.schinzel.samples.sample10.Person;
import io.schinzel.samples.sample10.Position;
import io.schinzel.samples.sample10.SampleAPI;

/**
 * The purpose of this sample is to show how to use custom classes and enums with @expose.
 * <p>
 * Load page: http://127.0.0.1:5555/
 */
public class Sample10 {

    public static void main(String[] args) {
        AtExpose.create()
                .addDataType(Person.class)
                .addDataType(Position.class)
                .addArgument(Argument.builder()
                        .name("manager")
                        .dataType(new ClassDT<>(Person.class))
                        .description("A manager")
                        .build())
                .addArgument(Argument.builder()
                        .name("position")
                        .dataType(new ClassDT<>(Position.class))
                        .defaultValue('"' + Position.FIRST.name() + '"')
                        .description("The position")
                        .build())
                .addArgument(Argument.builder()
                        .name("sample_arg")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .allowedCharsRegEx("[a-z0-9_@.-]*")
                        .defaultValue("default-value")
                        .description("Description of the sample argument")
                        .build())
                .expose(SampleAPI.class)
                .generateJavaScriptClient("src/main/resources/web/sample10/ServerCaller.js")
                .startWebServer("web/sample10");
        System.out.println("System started!");
    }
}
