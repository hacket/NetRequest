package com.example.netrequest.model;

public class ArrayData {

    public String subIcon;
    public String name;
    public String subType;
    public String type;
    public String isForceAdd;
    public String key;
    public String size;
    public String position;

    @Override
    public String toString() {
        return "Data{" +
                "subIcon='" + subIcon + '\'' +
                ", name='" + name + '\'' +
                ", subType='" + subType + '\'' +
                ", type='" + type + '\'' +
                ", isForceAdd='" + isForceAdd + '\'' +
                ", key='" + key + '\'' +
                ", size='" + size + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
