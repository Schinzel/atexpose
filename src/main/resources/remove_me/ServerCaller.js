export class ServerCaller {
    /**
     * No description available
     */
    async concat(String = '', Int = '0'){
        return await new ServerCallerInt()
            .setPath('/api/concat')
            .addArg('String', String)
            .addArg('Int', Int)
            .callWithPromise();
    }

    /**
     * Returns the argument string. Util method for testing.
     */
    async echo(String = ''){
        return await new ServerCallerInt()
            .setPath('/api/echo')
            .addArg('String', String)
            .callWithPromise();
    }

    /**
     * Simply returns the string "pong". Util method for testing.
     */
    async ping(){
        return await new ServerCallerInt()
            .setPath('/api/ping')
            .callWithPromise();
    }

    /**
     * Returns the time when the server was started
     */
    async startTime(){
        return await new ServerCallerInt()
            .setPath('/api/startTime')
            .callWithPromise();
    }

    /**
     * No description available
     */
    async test_it(){
        return await new ServerCallerInt()
            .setPath('/api/test_it')
            .callWithPromise();
    }

    /**
     * No description available
     */
    async test_it_2(test_var = ''){
        return await new ServerCallerInt()
            .setPath('/api/test_it_2')
            .addArg('test_var', test_var)
            .callWithPromise();
    }

    /**
     * Returns the current server time in UTC.
     */
    async time(){
        return await new ServerCallerInt()
            .setPath('/api/time')
            .callWithPromise();
    }

}


const REQUEST_TIMEOUT = 60000;

class ServerCallerInt {
    constructor() {
        /**
         * arguments to send to server will be stored here
         * @protected
         */
        this._requestArguments = {};
        // function to execute on successful ajax request
        this._successCallback = () => {
        };
        /**
         * @protected
         * @type {string}
         */
        this._contentType = 'application/x-www-form-urlencoded; charset=UTF-8';
        /**
         * @protected
         * @type {boolean}
         */
        this._processData = true;
        // function to execute on failed ajax request
        this._failCallback = (jqXHR) => {
            // Use response text sent from server. If no text sent from server, then set generic error text
            let responseText = jqXHR.responseText ?
                jqXHR.responseText
                : "No error text from server";
            throw(`Error calling server ${responseText}`);
        };
    }

    /**
     * @param path {String} Path excluding host. For example: /api/v1/dev/db/clear
     */
    setPath(path){
        this._requestPath = path;
        return this;
    }


    /**
     * Add an argument to send to server.
     * @param name of the argument (will be key in JSON arguments)
     * @param value of the argument for the name key (in JSON)
     * @return {ServerCaller} This for chaining
     */
    addArg(name, value) {
        this._requestArguments[name] = value;
        return this;
    }

    /**
     * Set data other than request arguments
     * @param data
     * @return {ServerCaller} This for chaining
     */
    setRequestData(data) {
        this._requestArguments = data;
        return this;
    }

    /**
     * Set callback function for successful request
     * Example: setSuccessCallback(function(response) { })
     * @param callback
     * @return {ServerCaller} This for chaining
     */
    setSuccessCallback(callback) {
        if (typeof callback !== "function") {
            throw "Argument must be a function";
        }
        this._successCallback = callback;
        return this;
    }

    /**
     * Set callback function for failed request
     * Example: setFailCallback(function(status, response) { })
     * @param callback
     * @return {ServerCaller} This for chaining
     */
    setFailCallback(callback) {
        if (typeof callback !== "function") {
            throw "Argument must be a function";
        }
        this._failCallback = callback;
        return this;
    }

    /**
     * Execute the ajax call
     */
    call() {
        let requestPathWithHost = getAjaxUrl(this._requestPath);
        console.log(`requesting API url '${requestPathWithHost}'`);
        $.ajax({
            type: "GET",
            url: requestPathWithHost,
            data: this._requestArguments,
            cache: true,
            timeout: REQUEST_TIMEOUT,
            contentType: this._contentType,
            processData: this._processData
        })
            .done((response) => {
                let responseAsJson = toJSON(response);
                this._successCallback(responseAsJson);
            })
            .fail((jqXHR) => {
                handleFail(jqXHR.responseText, jqXHR.status);
                this._failCallback(jqXHR);
            });
    }

    /**
     * Executes the ajax call returning a Promise
     * @returns {Promise} The promise for the result of the call
     */
    callWithPromise() {
        return new Promise((resolve, reject) => {
            this
                .setSuccessCallback(resolve)
                .setFailCallback(reject)
                .call();
        });
    }
}


/**
 * Get complete api path with host
 * @param path
 * @return {string}
 */
function getAjaxUrl(path) {
    return "//" + document.location.host + path;
}


/**
 * Handles failure in call to server
 * @param responseText The failure response from the server
 * @param statusCode
 */
function handleFail(responseText, statusCode) {
    const httpStatusUnauthorized = 401;
    console.error(`statusCode: '${statusCode}' responseText: '${responseText}'`);
    // Check if the status is unauthorized
    if (statusCode === httpStatusUnauthorized) {
        /** @type {{error: String, redirectPage: String}} */
        let responseTextAsJson = toJSON(responseText);
        // Send user to redirectPage
        window.location.href = responseTextAsJson.redirectPage;
    }
}


/**
 * Try parsing a string to JSON
 * @param str argument to try to parse
 * @return {*} JSON if successful otherwise the argument string
 */
function toJSON(str) {
    // noinspection UnusedCatchParameterJS
    try {
        return JSON.parse(str);
    } catch (e) {
        return str;
    }
}