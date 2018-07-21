## Project :  Transaction stats streaming as a service

Code provide streaming of real time stats of transactions as a service that runs in a docker container.

## Project Brief

This service have two end points, first to get the transaction data into the system
and second endpoint provide the way to get the stats(sum,total,min,max,avg) of transaction inserted in last 60 seconds.

 
## Technology stack

1. Sping Boot Framework for writing Rest API
2. Spring Tool Suite for code development
3. Maven as a dependency management system
4. Docker for containring the solution


### Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment notes on how to deploy the project in your system

### Prerequisites

Code can be started with docker as well in normal way provide you have jdk installed in your system.
If want to run the code in docker,Docker machine should be up and running. Docker compose should be installed


### Deployment

1. In Conatiner

* git clone https://Manishmbm2010@bitbucket.org/Manishmbm2010/home_banking_service_django_poc.git
* cd home_banking_service_django_poc/
* sudo docker run -it --rm -v "$PWD":/usr/src/app/ --volume "$HOME"/.m2:/root/.m2 -w /usr/src/app/ maven:3-jdk-8-alpine mvn clean install
* sudo docker-compose up --build

2. Without Conatiner

* git clone https://Manishmbm2010@bitbucket.org/Manishmbm2010/home_banking_service_django_poc.git
* cd home_banking_service_django_poc/
* mvn clean install
* java -jar target/

### Testing with bulk Requests

If you want to test the code and post some transaction data to service you can do it in an autaomated faishon by following the below instructions.

sh automatedCurl.sh 5 10

Just call automated curl with two arguments.

First argument take the number of request you want to make.
Second argument introduce delay(in seconds) between every request intiated.

### Rest end points


* http://localhost:8080/transactions			        (method=POST)
* {"amount": 12.3,"timestamp": 1578192204000}
* http://localhost:8080/transactions				(method=GET)
* http://localhost:8080/statistics				(method=PUT) 


### Solution architecture

In order to make the services time and space complexity near to o(1) and to provide the stats streaming faciliate
a internal dameon kind of process scheduled for evey second that calcuate the stats and update the final stats, that is being consumned by the stat service in O(1) time. at the same time all the transaction whose timestamp is older than 60 seconds will be deleted to maintain the space complexity of the service near to O(1).

 

##Author

* **Manish Jain**


