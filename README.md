#Overview
This project creates a microservice that generates flash card hash ids and is intended as a simple demonstration of combining
 several technologies together:

* Docker
* Spring MVC
* Spring Boot
* Cucumber
* Groovy
* Spock
* Ansible

#Prerequisites

* [JDK 8](http://www.oracle.com/technetwork/java/index.html) installed and working
* Building under [Ubuntu Linux](http://www.ubuntu.com/) is supported and recommended 

#Building
Type `./gradlew` to build and assemble the service.

#Installation
TODO

#Tips and Tricks

##Verifying The Setup
TODO

##Running Acceptance Test From IDEA
Running the acceptance tests from IDEA is a two step process.  First, you need to launch the container so that
Cucumber has something to poke at.  The simplest way to do this is to go into the `build/docker` directory and run
`docker-compose up`.  That will create and launch the container.  Once the container is up, you can launch the
the acceptance tests.  **IMPORTANT:** the container must be launched from a normal shell.  The IDEA container
cannot run Docker (Docker inside Docker is too meta for me) and won't work.

##Operations Endpoints
The services supports a variety of endpoints useful to an Operations engineer.

* /operations - Provides a hypermedia-based “discovery page” for the other endpoints.
* /operations/autoconfig - Displays an auto-configuration report showing all auto-configuration candidates and the reason why they ‘were’ or ‘were not’ applied.
* /operations/beans - Displays a complete list of all the Spring beans in your application.
* /operations/configprops - Displays a collated list of all @ConfigurationProperties.
* /operations/dump - Performs a thread dump.
* /operations/env - Exposes properties from Spring’s ConfigurableEnvironment.
* /operations/flyway - Shows any Flyway database migrations that have been applied.
* /operations/health - Shows application health information.
* /operations/info - Displays arbitrary application info.
* /operations/liquibase - Shows any Liquibase database migrations that have been applied.
* /operations/logfile - Returns the contents of the logfile (if logging.file or logging.path properties have been set).
* /operations/metrics - Shows ‘metrics’ information for the current application.
* /operations/mappings - Displays a collated list of all @RequestMapping paths.
* /operations/shutdown - Allows the application to be gracefully shutdown (not enabled by default).
* /operations/trace - Displays trace information (by default the last few HTTP requests).

##REST API Documentation
You can find the current API documentation at `/docs/index.hml`.

#Troubleshooting

TODO

#License and Credits
This project is licensed under the [Apache License Version 2.0, January 2004](http://www.apache.org/licenses/).

#List of Changes

