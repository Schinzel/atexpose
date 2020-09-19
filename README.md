# @Expose

The goal of @Expose is to be the least distracting and fastest way to get a web server up and 
running that can serve static file content such as HTML-files and handle proprietary Ajax requests. 

Put differently: It should be the web server that distracts you the least from solving the actual problem you are working on.

[![Build Status](https://travis-ci.org/Schinzel/atexpose.svg?branch=master)](https://travis-ci.org/Schinzel/atexpose)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Schinzel_atexpose&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=Schinzel_atexpose)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Schinzel_atexpose&metric=coverage)](https://sonarcloud.io/dashboard?id=Schinzel_atexpose)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Schinzel_atexpose&metric=security_rating)](https://sonarcloud.io/dashboard?id=Schinzel_atexpose)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Schinzel_atexpose&metric=sqale_index)](https://sonarcloud.io/dashboard?id=Schinzel_atexpose)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Schinzel_atexpose&metric=ncloc)](https://sonarcloud.io/dashboard?id=Schinzel_atexpose)

There are other frameworks and projects that offers similar functionality to
@Expose. Many of them are a lot more well used and tested than @Expose. Notable
examples are GlassFish, Netty and Jetty. If in doubt use these instead. 




##### Getting started
Include the following in your POM:
```xml
<repositories>
    <repository>
        <id>maven-repo.atexpose.com</id>
        <url>http://maven-repo.atexpose.com/release</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.atexpose</groupId>
        <artifactId>atexpose</artifactId>
        <version>0.9.36</version>
    </dependency>
</dependencies>
```

Create a sample class:

```java
public class MyClass {

    @Expose
    public String myMethod(){
        return "My method says hello!";
    }
    
    
    @Expose(
            arguments = {"String"}
    )
    public String setTheThing(String str) {
        return "The thing was set to '" + str + "'.";
    }    
}
```


Start @Expose:
```java
AtExpose.create()
	//Expose static methods in a class
	.expose(new MyClass())
	//Start web server
	.start(WebServerBuilder.create().build());


```

To invoke call the following URL in a browser.

<http://localhost:5555/api/myMethod>


##### Samples
Samples found [here](https://github.com/Schinzel/atexpose/tree/master/src/main/java/io/schinzel/samples)


##### A large degree freedom
* You want ten different web servers, you got it. 
* Extensible. You can build you own custom parsers, wrappers, channels and more.  


##### Battle Tested
@Expose runs well on Heroku and is battle tested on a site that has thousands of visitors per day.


# Overview
@Expose consists of:
1. An API
2. A set of dispatcher

The API is defines the methods that can be invoked. The dispatchers are responsible for receiving incoming requests, processing the requests and sending the response.

The main components of the API are 
1. Methods
2. Arguments
3. Data types


A dispatcher consists of
1. A channel - Receives the incoming messages and sends the response
2. A parser - Parses the incoming message
3. A wrapper - Wraps the response
4. Zero, one or multiple logs

![Object Diagram](https://docs.google.com/drawings/d/e/2PACX-1vRK1kiOCWuwzFy_dq8s3Xk0RWQO85wvDa_aGZxCZg3KVMUhDK1da-ctQknM4HtvVVeLlTWcHwC17X6_/pub?w=960&h=720)




# Channels

### Purpose
The purpose of a channel is to read incoming messages and to write outgoing responses.

### Available Channels
* CommandLineChannel - Reads from and writes to system out. 
* ScriptFileChannel - Input is a file with a set of requests. 
* ScheduledTask - Input is a request that is triggered at an interval. 
* ScheduledReport - Input is a request that is triggered at an interval. Output is an email sent. 
*  WebChannel - Input is a request sent to a port. Output is written to the same socket from where the request originated. 



# Parsers

### Purpose
The purpose of a parser is to parse incoming messages.

#### Available Parsers

###### TextParser
`doSomething 123, "monkey"`

###### UrlParser
`doSomething?arg1=123&arg2=monkey`

###### JsonRpcParser
`{"method": "doSomething", "params": {"arg1": 123, "arg2": monkey}}`


# Wrappers

### Purpose
The purpose of a wrapper is to wrap outgoing responses. 

### Available Wrappers
###### CsvWrapper
A simple return with comma separated values. 

###### WebWrapper
For wrapping responses to send to a browser. 
Sets HTTP response header. 
Handles
1. Static files. Examples: HTML, CSS, image files, JavaScript files.
2. POST and GET requests

Server side includes are supported for text files.
1. File includes. Syntax: `<!--#include file="header.html" -->`
2. Variables include. Syntax: `<!--#echo var="my_var" -->`



# Loggers

A logger has 
1. a type - Event or Error
2. a format - formats the output
3. a writer - writes the formatted output
4. optionally a crypto - it can be desirable to encrypt sensitive log data

The available formats are
1. JSON
2. Single line format - suitable for log entries 
3. Multi line format - easier to read for humans

The available writers are
1. System out - writes to system out
2. Mail - send the entry as an emial


# Generators
### Purpose
The purpose of generators is to generate something from the API. 
For example API documentation or a Java or a JavaScript client.


The IGenerator interface
```
/**
 * @param methods               All the methods in the API
 * @param customDataTypeClasses All added custom data types classes added to the API
 */
void generate(List<MethodObject> methods, List<Class<?>> customDataTypeClasses);
```

To use a generator
`atExpose.generate(IGenerator)`

The project includes a JsClientGenerator which generates a JavaScript client.

# Queues

Queues are a great way to reduce complexity in a system. By using message queues the different parts of the system do not get intertwined. By having a system that is decoupled it will be easier to understand, easier to change and several other benefits. You can easily have some services on AWS, some on Azure and some on your own servers. 

### Queues in @Expose
@Expose supports receiving requests through Amazons SQS - Simple Queue Service. To make life easier there is a stand alone utility class for adding requests to the queue.

A message queue has two main components; a producer and a consumer. A producer puts messages on the queue and a consumer consumes the messages from the queue. For @Expose messages are requests. 

#### Consumer
A consumer is a dispatcher like for example the command line interface which gets its request by reading from an SQS queue.

#### Producer
Requests can be added to a SQS queue in any manner. There does exist two utility classes to facilitate adding messages to the queue:
1. `JsonRpc` formats request in the expected JSON RPC format. 
2. `SqsProducer` adds requests to the SQS queue. 

#### Message format
Messages are sent in [JSON-RPC 2.0](http://www.jsonrpc.org/specification) format
```json
{"method": "doSomething", "params": {"para1": 23, "para2": 42}}
```

### Sample
Prerequisites. An [AWS SQS Fifo queue](http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/FIFO-queues.html). 

##### Step 1 - On system 1 expose a method
```java
public class JobClass {

    @Expose(requiredAccessLevel = 1,
            arguments = {"Int"})
    String doHeavyBackgroundJob(int count) {
        Sandman.snoozeSeconds(1);
        return "Phew, all done with heavy job " + count;
    }
}
```

##### Step 2 - On system 1 set up a consumer
On the system with the exposed method, start a consumer. 
```java
AtExpose.create()
	//Expose sample class
	.expose(new JobClass())
	//Start SQS consumer
	.startDispatcher(getSqsConsumer());

private static IDispatcher getSqsConsumer() {
	return SqsConsumerFactory.builder()
		.awsAccessKey(AWS.ACCESS_KEY)
		.awsSecretKey(AWS.SECRET_KEY)
		.queueUrl(AWS.QUEUE_URL)
		.region(Regions.EU_WEST_1)
		.name("MyFirstSqsConsumer")
		.noOfThreads(2)
		.accessLevel(1)
		.build();
}
```

##### Step 3 - On system 2 add requests to the queue
```java
//Create a SqsProducer that can put messages on an AWS SQS queue.
IQueueProducer sqsProducer = SqsProducer.builder()
	.awsAccessKey(AWS.ACCESS_KEY)
	.awsSecretKey(AWS.SECRET_KEY)
	.region(Regions.EU_WEST_1)
	.queueUrl(AWS.QUEUE_URL)
	.sqsQueueType(SqsQueueType.FIFO)
	.build();

String jsonRpc = JsonRpc.builder()
	.methodName("doHeavyBackgroundJob")
	.argument("Int", String.valueOf(i))
	.build()
	.toString();
sqsProducer.send(jsonRpc);
```




# Time and File format

* All times are in UTC.
* Where there is an option, UTF-8 is used. 



# Versions
## Features added for next release
- New feature: Generators
- JavaScript client generator added. Translates the api to a JavaScript client.
- API requests prefixed with `api` instead of `call`
- Aliases are no longer supported
- Data types `Float` and `JSONObject` are no longer supported
- `IExceptionProperties` is no longer supported
- Argument can now take an optional reg ex. All argument values has to match reg ex else an error is thrown.

## 0.9.38
_2020-01-03_
- Upgraded dependencies 

## 0.9.37
_2019-07-25_
- Added validation required argument count of exposed methods
- Upgraded dependencies 
- Removed unused dependency common-codec
- Bug fix: using unnamed arguments caused error

## 0.9.36
_2017-11-13_
- Exposed method `startTime`. Returns time in UTC and Swedish time. 

## 0.9.35
_2017-11-05_
- WebWrapper. Include files can contain other include files which in turn can contain other include files and so on.

## 0.9.34
_2017-10-31_
- Basic-utils 1.31
- Can set custom 404 page
- ScheduledTask/Report builder: `zoneId` -> `timeZone`
- AtExpose. New method: `start(Iterable<IDispatcher> dispatchers)`
- API. New method for more concise code: `addArgument(Sring name, DataType dataType, String description)`

## 0.9.33
_2017-10-19_
- Added exposed method `startTime` which returns the time the instance was started.

## 0.9.32
_2017-10-16_
- Dispatchers are built using stand alone builders instead of mainly metods or chained builders in main @Expose class. As such several methods has been removed from AtExpose, for example `startCli` and `getWebServerBuilder`. A set of factory classes has been added, for example `CliFactory` and `ScheduledTaskFactory`.

## 0.9.31
_2017-10-04_
- Basic-utils 1.30
- Bug fix: Handles calls from browser sync

## 0.9.30
_2017-09-15_
- Basic-utils 1.29

## 0.9.29
_2017-09-15_
- Basic-utils 1.28
- Logging
    - Bug fix: execution time is now reported correctly
    - Bug fix: `addEventLogger` and `addErrorLogger` previously exchanged `logFormatter` and `logWriter` when exposed. 
    - `logFormatter` and `logWriter` are now derived on their enum names rather than their class names.
    - Default logFormatter is now json
    - Default logWriter is now system_out
    - `LogFormatterFactory` and `LogWriterFactory` methods changed from `getInstance` to `create`

## 0.9.28
_2017-08-08_
- Basic-utils 1.26
- Scheduled Tasks
    - Now supports time zones
    - `addTask` renamed to `addMinuteTask`

## 0.9.27
_2017-07-12_
- Can now attach properties with thrown exceptions.

## 0.9.26
_2017-07-10_
- New basic-utils version 1.24
- Text parser: text qualifier is now single quote instead of double quote
- `AtExpose.expose` added for conciser set ups. 
- AWS Simple Queue Service supported
    - `JsonRpcParser` added - Parses [JSON-RPC 2.0](http://www.jsonrpc.org/specification) messages. 
    - `SqsChannel` added - Reads messages from an SQS queue.
    - Added exposed method `sendToQueue(String queueProducerName, String message)` that sends a message to a queue. 
    - Added `AtExpose.addQueueProducer(String queueProducerName, IQueueProducer queueProducer)` that adds queue producers to a collection.

## 0.9.25
_2017-06-23_
- Removed proprietary JsonOrdered. 
- Better error messages from exposed code including class, method and line number. 
- Logger encryption replaced proprietary with cipher from basic-utils.

## 0.9.24
- Uses the crypto from basic-utils instead

## 0.9.23
- Removed see-alsos annotation

## 0.9.22
- Can now expose package private methods

## 0.9.21
- More detailed error messages. 

## 0.9.20
- Refactoring. 
