package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class UpdatePointDeviceLocationBean {
private String user_id;
private String devname;
private String devmac;
private String  center_latitude;
private String center_longitude;
private String edge_latitude;
private String edge_longitude;

    public UpdatePointDeviceLocationBean(String user_id, String devname, String devmac, String center_latitude, String center_longitude, String edge_latitude, String edge_longitude, String circle_radius) {
        this.user_id = user_id;
        this.devname = devname;
        this.devmac = devmac;
        this.center_latitude = center_latitude;
        this.center_longitude = center_longitude;
        this.edge_latitude = edge_latitude;
        this.edge_longitude = edge_longitude;
        this.circle_radius = circle_radius;
    }

    private String circle_radius;
}
