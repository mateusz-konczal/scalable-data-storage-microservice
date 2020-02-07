package pl.edu.utp.thesis.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.edu.utp.thesis.SharedConstants;
import pl.edu.utp.thesis.model.Device;

import java.util.Properties;

public class KafkaVerticle extends AbstractVerticle implements SharedConstants {

  private Vertx vertx;
  private KafkaConsumer<String, String> kafkaConsumer = null;
  private static final Logger LOGGER_KAFKA = LogManager.getLogger(KafkaVerticle.class);

  public KafkaVerticle(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void start() {
    LOGGER_KAFKA.info("Kafka verticle is up");
    EventBus eventBus = vertx.eventBus();

    Properties config = new Properties();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_DEFAULT);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_GROUP_ID);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KAFKA_AUTO_OFFSET_RESET);
    config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, KAFKA_AUTO_COMMIT);
    kafkaConsumer = KafkaConsumer.create(vertx, config);

    kafkaConsumer.handler(record -> {
      LOGGER_KAFKA.info("Processing: key = " + record.key() + ", value = " + record.value() +
        ", partition = " + record.partition() + ", offset = " + record.offset());
      try {
        Device device = Json.decodeValue(record.value(), Device.class);
        sendForSaving(eventBus, device);
      } catch (DecodeException e) {
        LOGGER_KAFKA.error("Decode failed: " + e.getMessage());
      }
    });

    kafkaConsumer.subscribe(KAFKA_TOPIC, result -> {
      if (result.succeeded()) {
        LOGGER_KAFKA.info("Kafka consumer subscribed");
      } else {
        LOGGER_KAFKA.error("Could not subscribe: " + result.cause().getMessage());
      }
    });

    kafkaConsumer.exceptionHandler(exception -> LOGGER_KAFKA.error("Error: " + exception.getMessage()));
  }

  @Override
  public void stop() {
    if (kafkaConsumer != null) {
      kafkaConsumer.close(result -> {
        if (result.succeeded()) {
          LOGGER_KAFKA.info("Kafka consumer is now closed");
          kafkaConsumer = null;
        } else {
          LOGGER_KAFKA.error("Close Kafka consumer failed: " + result.cause().getMessage());
        }
      });
    }
  }

  private void sendForSaving(EventBus eventBus, Device device) {
    if (device == null) {
      LOGGER_KAFKA.warn("No device defined");
      return;
    }
    LOGGER_KAFKA.info("Device processed: device = " + device.getDeviceName() +
      ", list of values = " + device.getValueList());
    try {
      eventBus.send(EVENT_BUS_ADDRESS, Json.encode(device));
    } catch (EncodeException e) {
      LOGGER_KAFKA.error("Encode failed: " + e.getMessage());
    }
  }
}
