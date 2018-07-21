## Project :  Transaction stats streaming as a service

Code provide streaming of real time stats of transactions as a service and provide the instructions to deploy the code in docker container.

## Project Brief

This service has two end points, first one to get the transaction data into the system and second endpoint provide the way to get the stats(sum,total,min,max,avg) of transactions whose time stamp is not older than 60 seconds.
In order to acheive the time and space complexity almost constant for /statistic end point , a daemon kind of scheduled process has been setup.
This process execute with 1 millisecond of interval of its last iteration , that make sure you get all updated stats without getting into the calculation that makes the O(1) time complexity for /statistic end point.
Scheduled process takes care of deletion of transaction which are older than 60 seconds ,hence maintaing the space complexity to the number of transaction intiated in last 60 seconds.
API are thread safe and allow concurrent updates on the list that makes the solution more robbust.

 
## Technology stack

1. Sping Boot Framework for writing Rest API
2. Spring Tool Suite for code development
3. Maven as a dependency management system
4. Docker for containring the solution


### Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment notes on how to deploy the project in your system

### Prerequisites

Code can be started with docker as well in normal way provide you have jdk & maven installed in your system.
If want to run the code in docker,Docker machine should be up and running. Docker compose should be installed


### Deployment

Test cases will take at least a minute and few seconds because in one of the test case delay of 61 seconds has been introduced.

1. In Conatiner

* git clone https://Manishmbm2010@bitbucket.org/Manishmbm2010/transaction_statistic_streaming-_service.git
* cd transaction_statistic_streaming-_service/
* sudo docker run -it --rm -v "$PWD":/usr/src/app/ --volume "$HOME"/.m2:/root/.m2 -w /usr/src/app/ maven:3-jdk-8-alpine mvn clean install
* sudo docker-compose up --build

2. Without Conatiner

* git clone https://Manishmbm2010@bitbucket.org/Manishmbm2010/transaction_statistic_streaming-_service.git
* cd transaction_statistic_streaming-_service/
* mvn clean install
* java -jar target/statistics-0.0.1.jar

### Testing with bulk Requests

If you want to test the code and post some transaction data to service you can do it in an autaomated faishon by following the below instructions.

cd transaction_statistic_streaming-_service/
./automatedCurl.sh 5000 0

Just call automated curl with two arguments.

First argument take the number of request you would like to make.
Second argument introduce delay(in seconds) between every request.

### Rest end points


* http://localhost:8080/transactions			        (method=POST)  body : {"amount": 12.3,"timestamp": 1578192204000}
* http://localhost:8080/statistics				(method=PUT) 


##Author

* **Manish Jain**


