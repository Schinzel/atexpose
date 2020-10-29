package com.atexpose.generator;

import com.atexpose.api.Argument;
import com.atexpose.api.MethodObject;
import com.atexpose.api.datatypes.*;
import com.google.common.collect.Streams;
import io.schinzel.basicutils.file.FileWriter;
import io.schinzel.jstranspiler.JsTranspiler;
import io.schinzel.jstranspiler.transpiler.IToJavaScript;
import io.schinzel.jstranspiler.transpiler.KotlinClass;
import io.schinzel.jstranspiler.transpiler.KotlinEnum;

import java.util.List;
import java.util.stream.Collectors;

public class JsClientGenerator implements IGenerator {
    private final String mFilePath;

    public JsClientGenerator(String filePath) {
        mFilePath = filePath;
    }


    @Override
    public void generate(List<MethodObject> serverSideMethods, List<Class<?>> customDataTypeClasses) {
        StringBuilder jsMethods = new StringBuilder();
        for (MethodObject serverSideMethod : serverSideMethods) {
            if (serverSideMethod.getAccessLevelRequiredToUseThisMethod() == 1) {

                String jsMethodName = serverSideMethod.getMethod().getName();
                List<Argument> argumentList = serverSideMethod
                        .getMethodArguments()
                        .getArguments();

                @SuppressWarnings("UnstableApiUsage")
                String jsArguments = Streams.mapWithIndex(
                        argumentList.stream(),
                        (argument, index) -> {
                            boolean isOptionalArgument = index >= serverSideMethod.getNoOfRequiredArguments();
                            return getClientArgument(argument, isOptionalArgument);
                        })
                        .collect(Collectors.joining(", "));

                String jsDocArguments = argumentList
                        .stream()
                        .map(n -> "     * @param {" + getJsDataTypeName(n.getDataType()) + "} " + n.getKey() + " - " + n.getDescription() + "\n")
                        .collect(Collectors.joining());

                String setServerCallerArguments = argumentList
                        .stream()
                        .map(n -> "            .addArg('" + n.getKey() + "', " + n.getKey() + ")\n")
                        .collect(Collectors.joining());

                final AbstractDataType returnDataType = serverSideMethod.getReturnDataType();


                String jsMethod = ""
                        + "    /**\n"
                        + "     * " + serverSideMethod.getDescription() + "\n"
                        + jsDocArguments
                        + "     * @returns {Promise<" + getJsDataTypeName(returnDataType) + ">}\n"
                        + "     */\n"
                        + "    async " + jsMethodName + "(" + jsArguments + "){\n"
                        + "        let response = await new ServerCallerInt()\n"
                        + "            .setPath('/api/" + jsMethodName + "')\n"
                        + setServerCallerArguments
                        + "            .callWithPromise();\n"
                        + getJsReturnStatement(returnDataType)
                        + "    }\n\n";

                jsMethods.append(jsMethod);
            }
        }


        String jsCustomClasses = customDataTypeClasses.stream()
                .map(n -> {
                    IToJavaScript clazz = n.isEnum()
                            ? new KotlinEnum(n)
                            : new KotlinClass(n);
                    return clazz.toJavaScript();
                })
                .collect(Collectors.joining());

        String jsServerCaller = "" +
                START_SERVER_CALLER_CLASS +
                jsMethods.toString() +
                END_SERVER_CALLER_CLASS;

        String fileContent = "" +
                HEADER +
                DATA_OBJECT_CLASS +
                jsCustomClasses +
                jsServerCaller +
                SERVER_CALLER_INTERNAL_CLASS;

        FileWriter.writer()
                .fileName(mFilePath)
                .content(fileContent)
                .write();

    }


    /**
     * @param returnDataType
     * @return Example "response" or "MyEnum[response]" or "new MyClass(response)"
     */
    private static String getJsReturnStatement(AbstractDataType returnDataType) {
        if (isStandardDataType(returnDataType))
            return "        return response;\n";
        // If the return is an enum
        if (isEnum(returnDataType))
            return "        return " + returnDataType.getKey() + "[response];\n";
        // if the return is void
        if (returnDataType instanceof VoidDT)
            return "";
        // else, i.e. a return that is one of the data types registered with @expose
        return "        return new " + returnDataType.getKey() + "(response);\n";
    }


    /**
     * @param argument           The argument to convert to a JS argument
     * @param isOptionalArgument True if the argument is optional, else false
     * @return Examples: "my_arg" or "my_arg = 0" or "my_arg = 'default arg'"
     */
    private static String getClientArgument(Argument argument, boolean isOptionalArgument) {
        String returnValue = argument.getKey();
        if (isOptionalArgument) {
            boolean isNumeric = argument.getDataType().getKey().equalsIgnoreCase("Int");
            boolean defaultValIsNull = argument.getDefaultValue() == null;
            boolean isEnum = (argument.getDataType() instanceof ClassDT
                    && ((ClassDT<?>) argument.getDataType()).getClazz().isEnum());
            String valueAssignment;
            if (isNumeric || defaultValIsNull) {
                valueAssignment = argument.getDefaultValueAsString();
            } else if (isEnum) {
                valueAssignment = "'" + argument.getDefaultValueAsString() + "'";
            } else {
                valueAssignment = "'" + argument.getDefaultValueAsString() + "'";
            }
            returnValue += " = " + valueAssignment;
        }
        return returnValue;
    }


    private static boolean isEnum(AbstractDataType dataType) {
        return (dataType instanceof ClassDT) && ((ClassDT) dataType).getClazz().isEnum();
    }

    private static boolean isStandardDataType(AbstractDataType dataType) {
        return dataType instanceof BooleanDT
                || dataType instanceof IntDT
                || dataType instanceof StringDT;
    }

    private static String getJsDataTypeName(AbstractDataType dataType) {
        if (dataType instanceof AlphNumStringDT) {
            return "string";
        } else if (dataType instanceof BooleanDT) {
            return "boolean";
        } else if (dataType instanceof IntDT) {
            return "number";
        } else if (dataType instanceof StringDT) {
            return "string";
        } else {
            return dataType.getKey();
        }
    }

    private static final String HEADER = "" +
            "/**\n" +
            " * The purpose of this class is to send requests to the server. \n" +
            " * This class has been automatically generated with one method per method in the API. \n" +
            " */\n";


    private static final String DATA_OBJECT_CLASS = JsTranspiler.Companion.getDataObjectClass();


    private static final String START_SERVER_CALLER_CLASS = "" +
            "// noinspection JSUnusedGlobalSymbols,UnnecessaryLocalVariableJS\n" +
            "export class ServerCaller {\n" + "";


    private static final String END_SERVER_CALLER_CLASS = "" +
            "}\n" +
            "\n";


    private static final String SERVER_CALLER_INTERNAL_CLASS = "" +
            "\n" +
            "const REQUEST_TIMEOUT = 60000;\n" +
            "\n" +
            "class ServerCallerInt {\n" +
            "    constructor() {\n" +
            "        /**\n" +
            "         * arguments to send to server will be stored here\n" +
            "         * @protected\n" +
            "         */\n" +
            "        this._requestArguments = {};\n" +
            "        // function to execute on successful ajax request\n" +
            "        this._successCallback = () => {\n" +
            "        };\n" +
            "        /**\n" +
            "         * @protected\n" +
            "         * @type {string}\n" +
            "         */\n" +
            "        this._contentType = 'application/x-www-form-urlencoded; charset=UTF-8';\n" +
            "        /**\n" +
            "         * @protected\n" +
            "         * @type {boolean}\n" +
            "         */\n" +
            "        this._processData = true;\n" +
            "        // function to execute on failed ajax request\n" +
            "        this._failCallback = (jqXHR) => {\n" +
            "            // Use response text sent from server. If no text sent from server, then set generic error text\n" +
            "            let responseText = jqXHR.responseText ?\n" +
            "                jqXHR.responseText\n" +
            "                : \"No error text from server\";\n" +
            "            throw(`Error calling server ${responseText}`);\n" +
            "        };\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @param path {String} Path excluding host. For example: /api/v1/dev/db/clear\n" +
            "     */\n" +
            "    setPath(path){\n" +
            "        this._requestPath = path;\n" +
            "        return this;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Add an argument to send to server.\n" +
            "     * @param name of the argument (will be key in JSON arguments)\n" +
            "     * @param value of the argument for the name key (in JSON)\n" +
            "     * @return {ServerCallerInt} This for chaining\n" +
            "     */\n" +
            "    addArg(name, value) {\n" +
            "        if (value instanceof DataObject) {\n" +
            "            value = JSON.stringify(value);\n" +
            "        } else if (typeof value === 'object'){\n" +
            "            value = '\"' + value.name + '\"';\n" +
            "        }\n" +
            "        this._requestArguments[name] = value;\n" +
            "        return this;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Set callback function for successful request\n" +
            "     * Example: setSuccessCallback(function(response) { })\n" +
            "     * @param callback\n" +
            "     * @return {ServerCallerInt} This for chaining\n" +
            "     */\n" +
            "    setSuccessCallback(callback) {\n" +
            "        if (typeof callback !== \"function\") {\n" +
            "            throw \"Argument must be a function\";\n" +
            "        }\n" +
            "        this._successCallback = callback;\n" +
            "        return this;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Set callback function for failed request\n" +
            "     * Example: setFailCallback(function(status, response) { })\n" +
            "     * @param callback\n" +
            "     * @return {ServerCallerInt} This for chaining\n" +
            "     */\n" +
            "    setFailCallback(callback) {\n" +
            "        if (typeof callback !== \"function\") {\n" +
            "            throw \"Argument must be a function\";\n" +
            "        }\n" +
            "        this._failCallback = callback;\n" +
            "        return this;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Execute the ajax call\n" +
            "     */\n" +
            "    call() {\n" +
            "        let requestPathWithHost = getAjaxUrl(this._requestPath);\n" +
            "        console.log(`requesting API url '${this._requestPath}'`);\n" +
            "        $.ajax({\n" +
            "            type: \"POST\",\n" +
            "            url: requestPathWithHost,\n" +
            "            data: this._requestArguments,\n" +
            "            cache: true,\n" +
            "            timeout: REQUEST_TIMEOUT,\n" +
            "            contentType: this._contentType,\n" +
            "            processData: this._processData\n" +
            "        })\n" +
            "            .done((response) => {\n" +
            "                let responseAsJson = toJSON(response);\n" +
            "                this._successCallback(responseAsJson);\n" +
            "            })\n" +
            "            .fail((jqXHR) => {\n" +
            "                handleFail(jqXHR.responseText, jqXHR.status);\n" +
            "                this._failCallback(jqXHR);\n" +
            "            });\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Executes the ajax call returning a Promise\n" +
            "     * @returns {Promise} The promise for the result of the call\n" +
            "     */\n" +
            "    callWithPromise() {\n" +
            "        return new Promise((resolve, reject) => {\n" +
            "            this\n" +
            "                .setSuccessCallback(resolve)\n" +
            "                .setFailCallback(reject)\n" +
            "                .call();\n" +
            "        });\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "\n" +
            "/**\n" +
            " * Get complete api path with host\n" +
            " * @param path\n" +
            " * @return {string}\n" +
            " */\n" +
            "function getAjaxUrl(path) {\n" +
            "    return \"//\" + document.location.host + path;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "/**\n" +
            " * Handles failure in call to server\n" +
            " * @param responseText The failure response from the server\n" +
            " * @param statusCode\n" +
            " */\n" +
            "function handleFail(responseText, statusCode) {\n" +
            "    const httpStatusUnauthorized = 401;\n" +
            "    console.error(`statusCode: '${statusCode}' responseText: '${responseText}'`);\n" +
            "    // Check if the status is unauthorized\n" +
            "    if (statusCode === httpStatusUnauthorized) {\n" +
            "        /** @type {{error: String, redirectPage: String}} */\n" +
            "        let responseTextAsJson = toJSON(responseText);\n" +
            "        // Send user to redirectPage\n" +
            "        window.location.href = responseTextAsJson.redirectPage;\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "\n" +
            "/**\n" +
            " * Try parsing a string to JSON\n" +
            " * @param str argument to try to parse\n" +
            " * @return {*} JSON if successful otherwise the argument string\n" +
            " */\n" +
            "function toJSON(str) {\n" +
            "    // noinspection UnusedCatchParameterJS\n" +
            "    try {\n" +
            "        return JSON.parse(str);\n" +
            "    } catch (e) {\n" +
            "        return str;\n" +
            "    }\n" +
            "}\n\n";


}
