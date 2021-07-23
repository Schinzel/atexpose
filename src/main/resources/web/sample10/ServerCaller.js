/**
 * The purpose of this class is to send requests to the server. 
 * This class has been automatically generated with one method per method in the API. 
 */
 // noinspection JSUnusedLocalSymbols
/**
 * This class holds methods common to all transpiled classes.
 */
class DataObject {
    // noinspection JSUnusedGlobalSymbols
    /**
     * return {object} This instance as a json object
     */
    asJsonObject() {
        return JSON.parse(JSON.stringify(this));
    }

    // noinspection JSUnusedGlobalSymbols
    /**
     * return {string} This instance as a json string
     */
    asJsonString() {
        return JSON.stringify(this);
    }

    // noinspection JSUnusedGlobalSymbols
    /**
     * return {object} A clone of this object
     */
    clone() {
        return new this.constructor(this.asJsonObject());
    }
}

export class Person extends DataObject {
    constructor(json) {
        super();
        if (json) {
            /**
             * @private
             */
            this.name = json.name;
            /**
             * @private
             */
            this.age = parseInt(json.age);
            /**
             * @private
             */
            this.timeStamp = new Date(json.timeStamp);
            /**
             * @private
             */
            this.ids = json.ids;
        }
    }

    // noinspection JSUnusedGlobalSymbols
    /**
     * @return {string} 
     */
    getName() {
        return this.name;
    }
    
    // noinspection JSUnusedGlobalSymbols
    /**
     * @return {number} 
     */
    getAge() {
        return this.age;
    }
    
    // noinspection JSUnusedGlobalSymbols
    /**
     * @return {Date} 
     */
    getTimeStamp() {
        return this.timeStamp;
    }
    
    // noinspection JSUnusedGlobalSymbols
    /**
     * @return {string[]} A copy of the array held
     */
    getIds() {
        return this.ids.slice();
    }
    
    // noinspection JSUnusedGlobalSymbols
    /**
     * 
     * @param {number} age
     * @return {Person}
     */
    setAge(age) {
        this.age = age;
        return this;
    }
    
    // noinspection JSUnusedGlobalSymbols
    /**
     * 
     * @param {Date} timeStamp
     * @return {Person}
     */
    setTimeStamp(timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }
    
    // noinspection JSUnusedGlobalSymbols
    /**
     * Argument array is copied and set
     * @param {string[]} ids
     * @return {Person}
     */
    setIds(ids) {
        this.ids = ids.slice();
        return this;
    }
    

}

/**
 * @typedef {{name: string}} Position
 */
export const Position = Object.freeze({
    FIRST: {name: 'FIRST'},
    SECOND: {name: 'SECOND'}
});

// noinspection JSUnusedGlobalSymbols,UnnecessaryLocalVariableJS
export class ServerCaller {
    /**
     * Returns the argument string. Util method for testing.
     * @param {string} String - A string
     * @returns {Promise<string>}
     */
    async echo(String){
        let response = await new ServerCallerInt()
            .setPath('/api/echo')
            .addArg('String', String)
            .callWithPromise();
        return response;
    }

    /**
     * No description available
     * @returns {Promise<Person>}
     */
    async getManager(){
        let response = await new ServerCallerInt()
            .setPath('/api/getManager')
            .callWithPromise();
        return new Person(response);
    }

    /**
     * No description available
     * @returns {Promise<Position>}
     */
    async getPosition(){
        let response = await new ServerCallerInt()
            .setPath('/api/getPosition')
            .callWithPromise();
        return Position[response];
    }

    /**
     * Simply returns the string "pong". Util method for testing.
     * @returns {Promise<string>}
     */
    async ping(){
        let response = await new ServerCallerInt()
            .setPath('/api/ping')
            .callWithPromise();
        return response;
    }

    /**
     * No description available
     * @param {Person} manager - A manager
     * @returns {Promise<string>}
     */
    async setManager(manager){
        let response = await new ServerCallerInt()
            .setPath('/api/setManager')
            .addArg('manager', manager)
            .callWithPromise();
        return response;
    }

    /**
     * No description available
     * @param {Position} position - The position
     * @returns {Promise<string>}
     */
    async setPosition(position){
        let response = await new ServerCallerInt()
            .setPath('/api/setPosition')
            .addArg('position', position)
            .callWithPromise();
        return response;
    }

    /**
     * Returns the time when the server was started
     * @returns {Promise<string>}
     */
    async startTime(){
        let response = await new ServerCallerInt()
            .setPath('/api/startTime')
            .callWithPromise();
        return response;
    }

    /**
     * Returns the current server time in UTC.
     * @returns {Promise<string>}
     */
    async time(){
        let response = await new ServerCallerInt()
            .setPath('/api/time')
            .callWithPromise();
        return response;
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
     * @return {ServerCallerInt} This for chaining
     */
    addArg(name, value) {
        if (value instanceof DataObject) {
            value = JSON.stringify(value);
        } else if (typeof value === 'object'){
            value = '"' + value.name + '"';
        }
        this._requestArguments[name] = value;
        return this;
    }

    /**
     * Set callback function for successful request
     * Example: setSuccessCallback(function(response) { })
     * @param callback
     * @return {ServerCallerInt} This for chaining
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
     * @return {ServerCallerInt} This for chaining
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
        console.log(`requesting API url '${this._requestPath}'`);
        $.ajax({
            type: "POST",
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

