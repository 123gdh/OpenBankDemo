package com.example.openbank.enums;

public enum Msg {
    SUCCESS("处理成功"),
    SUCC("SUCCESS"),
    ERROR("系统异常");

    String ms;

    Msg(String ms) {
        this.ms = ms;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "ms='" + ms + '\'' +
                '}';
    }

    public String getms(){
        return ms;
    }
}
