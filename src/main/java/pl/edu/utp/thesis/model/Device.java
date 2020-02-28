package pl.edu.utp.thesis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
  "device",
  "value"
})

public class Device {
  @JsonProperty("device")
  private String deviceName = null;

  @JsonProperty("value")
  private List<Value> valueList = new ArrayList<>();

  @JsonProperty("device")
  public String getDeviceName() {
    return deviceName;
  }

  @JsonProperty("device")
  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  @JsonProperty("value")
  public List<Value> getValueList() {
    return valueList;
  }

  @JsonProperty("value")
  public void setValueList(List<Value> valueList) {
    this.valueList = valueList;
  }

  @Override
  public String toString() {
    return "Device{" +
      "deviceName='" + deviceName + '\'' +
      ", valueList=" + valueList +
      '}';
  }


  public static class Value {
    @JsonProperty("register")
    private String register = null;

    @JsonProperty("timestamp")
    private Long timestamp = null;

    @JsonProperty("asDouble")
    private Double asDouble = null;

    @JsonProperty("asLong")
    private Long asLong = null;

    @JsonProperty("asString")
    private String asString = null;

    @JsonProperty("register")
    public String getRegister() {
      return register;
    }

    @JsonProperty("register")
    public void setRegister(String register) {
      this.register = register;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
      return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
    }

    @JsonProperty("asDouble")
    public Double getAsDouble() {
      return asDouble;
    }

    @JsonProperty("asDouble")
    public void setAsDouble(Double asDouble) {
      this.asDouble = asDouble;
    }

    @JsonProperty("asLong")
    public Long getAsLong() {
      return asLong;
    }

    @JsonProperty("asLong")
    public void setAsLong(Long asLong) {
      this.asLong = asLong;
    }

    @JsonProperty("asString")
    public String getAsString() {
      return asString;
    }

    @JsonProperty("asString")
    public void setAsString(String asString) {
      this.asString = asString;
    }

    @Override
    public String toString() {
      return "Value{" +
        "register='" + register + '\'' +
        ", timestamp=" + timestamp +
        ", asDouble=" + asDouble +
        ", asLong=" + asLong +
        ", asString='" + asString + '\'' +
        '}';
    }
  }
}
