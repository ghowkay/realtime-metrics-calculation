package co.numina.traveler.streams.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Movement {


    private int sensorId;

    private String travelerId;

    private String travelerType;

    private List<String> position;

    private String timestamp;


    @JsonProperty("sensor_id")
    public int getSensorId() {
		return sensorId;
	}

    public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

    @JsonProperty("traveler_id")
    public String getTravelerId() {
		return travelerId;
	}

    public void setTravelerId(String travelerId) {
		this.travelerId = travelerId;
	}

    @JsonProperty("traveler_type")
    public String getTravelerType() {
		return travelerType;
	}

    public void setTravelerType(String travelerType) {
		this.travelerType = travelerType;
	}

    public List<String> getPosition() {
        return position;
    }

    public void setPosition(List<String> position) {
        this.position = position;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    

   
    

  
}
