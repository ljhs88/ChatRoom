package com.example.chatroom;
import org.litepal.crud.LitePalSupport;

public class Message extends LitePalSupport {//定义信息的实体类

    public static final int TYPE_RECEIVED = 0;//发送还是接收
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;
    private String ip;
    private String time;

    public Message() {};

    public Message(String content, int type, String ip, String time) {
        this.content = content;
        this.type = type;
        this.ip = ip;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
