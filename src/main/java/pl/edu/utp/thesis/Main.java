package pl.edu.utp.thesis;

import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.edu.utp.thesis.verticle.CassandraVerticle;
import pl.edu.utp.thesis.verticle.KafkaVerticle;

public class Main implements SharedConstants {

  private static final Logger LOGGER = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    Main main = new Main();
    main.init();
  }

  private void init() {
    LOGGER.info("Main thread started");
    Vertx vertx = Vertx.vertx();

    CassandraClientOptions cassandraClientOptions = new CassandraClientOptions()
      .addContactPoint(CASSANDRA_SERVER_DEFAULT_NAME)
      .setPort(CASSANDRA_DEFAULT_PORT)
      .setKeyspace(CASSANDRA_KEYSPACE);
    CassandraClient cassandraClient = CassandraClient.createShared(vertx, cassandraClientOptions);
    LOGGER.info("Cassandra client created");

    KafkaVerticle kafkaVerticle = new KafkaVerticle(vertx);
    vertx.deployVerticle(kafkaVerticle);

    DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(CASSANDRA_VERTICLE_INSTANCES);
    vertx.deployVerticle(() -> new CassandraVerticle(vertx, cassandraClient), deploymentOptions);

    LOGGER.info("Main thread completed");
  }
}
