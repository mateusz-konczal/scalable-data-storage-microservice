package pl.edu.utp.thesis.verticle;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import io.vertx.cassandra.CassandraClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.edu.utp.thesis.SharedConstants;
import pl.edu.utp.thesis.model.Device;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CassandraVerticle extends AbstractVerticle implements SharedConstants {

  private Vertx vertx;
  private CassandraClient cassandraClient;
  private long currentTimeMillis;
  private static final Logger LOGGER_CASSANDRA = LogManager.getLogger(CassandraVerticle.class);

  public CassandraVerticle(Vertx vertx, CassandraClient cassandraClient) {
    this.vertx = vertx;
    this.cassandraClient = cassandraClient;
  }

  @Override
  public void start() {
    LOGGER_CASSANDRA.info("Cassandra verticle is up");
    EventBus eventBus = vertx.eventBus();

    eventBus.consumer(EVENT_BUS_ADDRESS, message -> {
      try {
        currentTimeMillis = System.currentTimeMillis();
        Device device = Json.decodeValue(message.body().toString(), Device.class);
        LOGGER_CASSANDRA.info("Save the device with parameters: " + device.toString());
        if (device.getDeviceName() == null) {
          LOGGER_CASSANDRA.warn("No device name defined");
          return;
        }
        if (!(device.getDeviceName().equalsIgnoreCase("utp402ztc")) &&
          !(device.getDeviceName().equalsIgnoreCase("sq2frb"))) {
          LOGGER_CASSANDRA.warn("The " + device.getDeviceName() + " device is not supported");
          return;
        }
        workWithCassandra(device, cassandraClient);
      } catch (Exception e) {
        LOGGER_CASSANDRA.error("Exception while processing: " + message.body().toString() + ": " + e.getMessage());
      }
    }).setMaxBufferedMessages(1);
  }

  @Override
  public void stop() {
    if (cassandraClient != null) {
      cassandraClient.close(result -> {
        if (result.succeeded()) {
          LOGGER_CASSANDRA.info("Cassandra client is now closed");
          cassandraClient = null;
        } else {
          LOGGER_CASSANDRA.error("Close Cassandra client failed: " + result.cause().getMessage());
        }
      });
    }
  }

  private void workWithCassandra(Device device, CassandraClient cassandraClient) {
    String rawQuery = "INSERT INTO %s.%s " +
      "(label, year, month, day, hour, minute, second, millisecond, measured, recorded, asdouble, aslong, asstring) " +
      "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    String processedQuery = String.format(rawQuery, CASSANDRA_KEYSPACE, device.getDeviceName());
    LOGGER_CASSANDRA.info("Processed query: " + processedQuery);

    long recordingTimestamp = System.currentTimeMillis();
    BatchStatement batchStatement = new BatchStatement();

    cassandraClient.prepare(processedQuery, prepared -> {
      if (prepared.succeeded()) {
        LOGGER_CASSANDRA.info("The query has successfully been prepared");
        PreparedStatement preparedStatement = prepared.result();
        for (Device.Value value : device.getValueList()) {
          String label = value.getRegister() == null ? "unknown" : value.getRegister();
          long valueTimestamp = value.getTimestamp() == null ? recordingTimestamp : value.getTimestamp();
          ZonedDateTime utcTimestamp = Instant.ofEpochMilli(valueTimestamp).atZone(ZoneId.of("UTC"));
          Double asDouble = value.getAsDouble();
          Long asLong = value.getAsLong();
          String asString = value.getAsString() == null ? "" : value.getAsString();
          BoundStatement boundStatement = preparedStatement.bind(
            label,
            utcTimestamp.getYear(),
            utcTimestamp.getMonthValue(),
            utcTimestamp.getDayOfMonth(),
            utcTimestamp.getHour(),
            utcTimestamp.getMinute(),
            utcTimestamp.getSecond(),
            utcTimestamp.getNano() / 1000000,
            new Timestamp(valueTimestamp),
            new Timestamp(recordingTimestamp),
            asDouble,
            asLong,
            asString);
          batchStatement.add(boundStatement);
        }
        cassandraClient.execute(batchStatement, executed -> {
          if (executed.succeeded()) {
            LOGGER_CASSANDRA.info("The given batch executed successfully");
            long executionTimeMillis = System.currentTimeMillis() - currentTimeMillis;
            LOGGER_CASSANDRA.info("Working time of Cassandra verticle: " + executionTimeMillis + " ms");
          } else {
            LOGGER_CASSANDRA.error("Unable to execute the batch: " + executed.cause().getMessage());
          }
        });
      } else {
        LOGGER_CASSANDRA.error("Unable to prepare the query: " + prepared.cause().getMessage());
      }
    });
  }
}
