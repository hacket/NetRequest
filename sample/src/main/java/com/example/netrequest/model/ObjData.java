package com.example.netrequest.model;

import java.util.List;

public class ObjData {

    public String summary;
    public String image;
    public String link;
    public String language;
    public String transcoded;
    public String listBigImage;
    public String title;
    public String feed;
    public String id;
    public String homeImage;
    public String updated;
    public boolean editMode;
    public int voteUp;
    public int sort;
    public int clickNum;
    public int startTime;
    public int endTime;

    public List<String> images;

    @Override
    public String toString() {
        return "News{" +
                "summary='" + summary + '\'' +
                ", image='" + image + '\'' +
                ", link='" + link + '\'' +
                ", language='" + language + '\'' +
                ", transcoded='" + transcoded + '\'' +
                ", listBigImage='" + listBigImage + '\'' +
                ", title='" + title + '\'' +
                ", feed='" + feed + '\'' +
                ", id='" + id + '\'' +
                ", homeImage='" + homeImage + '\'' +
                ", updated='" + updated + '\'' +
                ", editMode=" + editMode +
                ", voteUp=" + voteUp +
                ", sort=" + sort +
                ", clickNum=" + clickNum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", images=" + images +
                '}';
    }
}
