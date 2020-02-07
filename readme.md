Implementation of a scalable data storage microservice

The aim of my thesis is the practical implementation of scalable and 
efficient data storage microservice. The microservice mediates between 
the fast messaging system (Apache Kafka) and the fast NoSQL database 
(Apache Cassandra) and converts the JSON data exchange format. It is a 
form of preparation for using the Cassandra database for massive data 
collection from various sources, e.g. power grid, drones. The main 
task of the microservice is quick and correct handling of the queue 
system to relieve the temporary load and then save the information to 
the database.