package io.momonet.portcheckerv2.models;

public class Destination {

    private String host;

    private Integer port;

    private String description;

    private Status status;

    public Destination(String host, Integer port, String description) {
        this.host = host;
        this.port = port;
        this.description = description;
        this.status = Status.UNKNOWN;
    }

    public Destination() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
