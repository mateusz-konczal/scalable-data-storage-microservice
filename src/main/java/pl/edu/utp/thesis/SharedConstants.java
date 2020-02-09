package pl.edu.utp.thesis;

public interface SharedConstants {
  String CASSANDRA_SERVER_DEFAULT_NAME = "localhost";
  int CASSANDRA_DEFAULT_PORT = 9042;
  String CASSANDRA_KEYSPACE = "datastore";

  int CASSANDRA_VERTICLE_INSTANCES = 4;
  String EVENT_BUS_ADDRESS = "cassandra.save";

  String KAFKA_SERVER_DEFAULT = "localhost:9092";
  String KAFKA_GROUP_ID = "my_group";
  String KAFKA_AUTO_OFFSET_RESET = "latest";
  String KAFKA_AUTO_COMMIT = "true";
  String KAFKA_TOPIC = "datastore";
}
