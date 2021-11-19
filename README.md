# Service DSL

[![CircleCI](https://circleci.com/gh/intuit/xtext-service-dsl/tree/master.svg?style=svg)](https://app.circleci.com/pipelines/github/intuit/xtext-service-dsl)

[Builds](https://app.circleci.com/pipelines/github/intuit/xtext-service-dsl)

## Overview

The project provides an xtext-based language and runtime engine for defining and executing HTTP service stub(s).  This can be used independently or imported in other Xtext DSLs projects to add capability to make a service call. This project comes with a runtime engine based on Spring Web Client. It should be easy to come up with a runtime engine using any other java based http clients.

Example:

```
Service SomeRestService as getSomeData method GET  {
  Url -> @Config("endpoint")
  Path -> ${"/v1/consents"}
  Timeout -> ${@Config("timeout")}
  @Header accept -> ${"*/*"}
}
```

It is up to the importing language whether to execute the service stub explicitly or implicitly.  

For now, only REST Services will be supported, but we want to support GraphQL and gRPC in the future.

## Pre-requisites

1. Java 8
2. Maven 3

## Making changes to Runtime

* Clone this project.
* Run `maven clean install`
* Import the runtime module `web-client-service-evaluator`. This is standard java maven project which can be imported in your favorite java editor.
  
  
## Making changes to the language

#### -- Installing the Software 

*	Download and install [Eclipse](https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2020-06/R/eclipse-jee-2020-06-R-macosx-cocoa-x86_64.dmg). You might run into a JVM error while installing. Please edit the info.plist file with your java path. [StackOverFlow Reference](https://stackoverflow.com/questions/52652846/cant-install-eclipse-failed-to-create-the-java-virtual-machine-on-mac)

*	Install Xtext SDK in Eclipse:
	*	In Eclipse, click Help --> Install New Software
	*	In Available Software, click Add to add a repository with [this location](http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/)   
	*	Select the repository just added in _Work with_ drop-down
	*	Select and install Xtext Complete SDK (2.25.0)  
   	
*	Restart Eclipse.    
   
#### -- Setting up the Project 

*	Clone this project
*  	Import the project in Eclipse, click File --> Import Project --> Maven --> Existing Maven Project
*	Under `com.intuit.dsl.service`, right click on [GenerateGraphQL.mwe2] --> Run As --> MWE2 Workflow
* 	Edit the [Grammar](https://github.com/intuit/xtext-service-dsl/blob/master/com.intuit.dsl.service/com.intuit.dsl.service/src/com/intuit/dsl/Service.xtext) and perform Step 3 again.

#### -- Manually building the Eclipse plugin.

*	Ensure you've setup your project following the instructions above
*	Create a new "Run Configuration" as an "Eclipse Application" and name it appropriately
*	When you run this new configuration, a new Eclipse Window will open, in which you can create service files and edit them.


## Contributing

see [Contributing](./CONTRIBUTING.md)

## License

see [LICENSE](./LICENSE)
