package io.momonet.portcheckerv2.models;

public class Destination {

    private String host;

    private Integer port;

    private String description;

    private boolean status;

    public Destination(String host, Integer port, String description) {
        this.host = host;
        this.port = port;
        this.description = description;
        this.status = false;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
