# @Expose

The goal of @Expose is that it should be the fastest way to get a web server up and running that can 
serve static file content such as HTML-files and handle proprietary Ajax requests. 

Put differently: It should be the web server that distracts you the least from solving the actual problem you are working on.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/20d9426304f246c18f22402af9cb22bb)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Schinzel/atexpose&amp;utm_campaign=Badge_Grade)

[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/20d9426304f246c18f22402af9cb22bb)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Schinzel/atexpose&amp;utm_campaign=Badge_Coverage)

[![Build Status](https://travis-ci.org/Schinzel/atexpose.svg?branch=master)](https://travis-ci.org/Schinzel/atexpose)

There are other frameworks and projects that offers similar functionality to
@Expose. Many of them are a lot more well used and tested than @Expose. Notable
examples are GlassFish and Jetty. If in doubt use these instead. 


##### Easy to get started
Clear and well documented samples included.

##### Intuitive to use
```
AtExpose.create().getWebServerBuilder()
        .port(8080)
        .webServerDir("mydir")
        .startWebServer();

Add a task that runs once per day:
addDailyTask MyTaskName, 'downloadSomething', 11:40
```
##### A large degree freedom
You want ten different web servers, you got it. You can build you own custom parsers, wrappers and channels. 


##### Battle Tested
@Expose runs well on Heroku and is battle tested on a site that has thousands of visitors per day.




<a href="https://sites.google.com/schinzel.io/atexpose" target="_blank">Go here for full documentation</a>

# Change Log
## Features added for next release
Nothing yet

## 0.9.26
_2017-07-10_
- New basic-util version 1.24
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
- Logger encryption replaced proprietary with cipher from basic utils.

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
