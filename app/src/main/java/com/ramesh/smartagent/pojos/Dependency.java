package com.ramesh.smartagent.pojos;

public    class Dependency {
    private String id;
    private String name;
    private String type;
    private double sizeInBytes;
    private String cdn_path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSizeInBytes(double sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public void setCdn_path(String cdn_path) {
        this.cdn_path = cdn_path;
    }

    public double getSizeInBytes() {
        return sizeInBytes;
    }

    public String getCdn_path() {
        return cdn_path;
    }
}