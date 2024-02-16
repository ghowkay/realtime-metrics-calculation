package co.numina.traveler.streams.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class CountMetric {


    private int sensorId;

    private String travelerType;


    private String path;

    private Long timestamp;

    private Long count;

    public int getSensorId() {
		return sensorId;
	}

    public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}


    public String getTravelerType() {
		return travelerType;
	}

    public void setTravelerType(String travelerType) {
		this.travelerType = travelerType;
	}


  
    

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    

   
    

  
}
