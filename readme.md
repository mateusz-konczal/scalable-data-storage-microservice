# Scalable data storage microservice
The microservice mediates between a fast messaging system (Apache Kafka) and a fast NoSQL database (Apache Cassandra) and receives data about a device in JSON format. It is a form of preparation for using the Cassandra database for massive data collection from various sources, e.g. power grid, drones. The main task of the microservice is quick and correct handling of the queue system to relieve the temporary load and then save the information to the database.
- --
## Technologies
* Java 8
* Maven
* Vert.x 3.8.5
* Kafka 2.4.0
* Cassandra 3.11.6
* Log4j 1.2.16

## Usage
```
git clone https://github.com/mateusz-konczal/scalable-data-storage-microservice.git
cd scalable-data-storage-microservice
```
Run the program in your favorite IDE

### Kafka on Windows
* Download the zipped file from [kafka.apache.org/downloads](https://kafka.apache.org/downloads)
* Unzip the file and go to the root directory
* Start the Apache ZooKeeper server with the command:
```
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```
* Start the Apache Kafka server with the command:
```
bin\windows\kafka-server-start.bat config\server.properties
```
* Create a topic and specify the server, replication factor and number of partitions for the topic, e.g.:
```
bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic datastore
```
* You can list available topics in Kafka:
```
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```
* You can also run the message producer:
```
bin\windows\kafka-console-producer.bat --bootstrap-list localhost:9092 --topic datastore
```
### Cassandra on Windows
* Download the zipped file from [cassandra.apache.org/_/download](https://cassandra.apache.org/_/download.html)
* Unzip the file and add the path to the bin directory in the PATH environment variable
* Start the Apache Cassandra server with the command:
```
cassandra
```
* Start the cqlsh client:
```
cqlsh
```
* Create a keyspace and specify data replication, e.g.:
```
CREATE KEYSPACE IF NOT EXISTS datastore WITH REPLIACTION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 } ;
```
* You can see all keyspaces:
```
describe keyspaces;
```
* You can select a keyspace called datastore:
```
use datastore;
```
* You can create tables in the selected keyspace using CQL (Cassandra Query Language)

## Functional scheme
![functional scheme](https://github.com/mateusz-konczal/scalable-data-storage-microservice/blob/main/img/img.png?raw=true)
