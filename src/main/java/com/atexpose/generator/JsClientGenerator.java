package com.atexpose.generator;

import com.atexpose.api.MethodObject;
import io.schinzel.basicutils.file.FileWriter;

import java.util.List;

public class JsClientGenerator implements IGenerator {
    private final String mFilePath;

    public JsClientGenerator(String filePath) {
        mFilePath = filePath;
    }


    @Override
    public void generate(List<MethodObject> methods) {
        StringBuilder sb = new StringBuilder();
        for (MethodObject method : methods) {
            if (method.getAccessLevelRequiredToUseThisMethod() == 1) {
                String methodName = method.getMethod().getName();
                String str = "\tasync " + methodName + "(){\n"
                        + "\t\tawait new ServerCallerInt()\n"
                        + "\t\t\t.setPath('/api/"+methodName+"')\n"
                        + "\t\t\t.callWithPromise();\n"
                        + "\t}\n\n";
                sb.append(str);
            }
        }

        String fileContent = HEADER + sb.toString() + FOOTER;
        FileWriter.writer()
                .fileName(mFilePath)
                .content(fileContent)
                .write();

    }


    private final static String HEADER = "export class ServerCaller {\n"
            + "";
    private final static String FOOTER = "" +
            "}\n" +
            "\n" +
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
            "\n" +
            "    /**\n" +
            "     * Add an argument to send to server.\n" +
            "     * @param name of the argument (will be key in JSON arguments)\n" +
            "     * @param value of the argument for the name key (in JSON)\n" +
            "     * @return {ServerCaller} This for chaining\n" +
            "     */\n" +
            "    addArg(name, value) {\n" +
            "        this._requestArguments[name] = value;\n" +
            "        return this;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Set data other than request arguments\n" +
            "     * @param data\n" +
            "     * @return {ServerCaller} This for chaining\n" +
            "     */\n" +
            "    setRequestData(data) {\n" +
            "        this._requestArguments = data;\n" +
            "        return this;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Set callback function for successful request\n" +
            "     * Example: setSuccessCallback(function(response) { })\n" +
            "     * @param callback\n" +
            "     * @return {ServerCaller} This for chaining\n" +
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
            "     * @return {ServerCaller} This for chaining\n" +
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
            "        console.log(`requesting API url '${requestPathWithHost}'`);\n" +
            "        $.ajax({\n" +
            "            type: \"GET\",\n" +
            "            url: requestPathWithHost,\n" +
            "            data: this._requestArguments,\n" +
            "            cache: true,\n" +
            "            timeout: REQUEST_TIMEOUT,\n" +
            "            contentType: this._contentType,\n" +
            "            processData: this._processData\n" +
            "        })\n" +
            "            .done((response) => {\n" +
            "                console.log(response);\n" +
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
            "}";


}
