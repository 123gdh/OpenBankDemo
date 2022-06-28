package com.example.openbank.utils;

import java.util.concurrent.ConcurrentHashMap;

public class CreateOutId {
    private static ConcurrentHashMap<String,Integer> con = new ConcurrentHashMap<>();
    public static String createId(){
        Integer id = con.get("ID");
        if (id == null){
            con.put("ID",0);
            return String.valueOf(System.currentTimeMillis());
        }else {
            int a = id+1;
            con.put("ID",a);
            long l = System.currentTimeMillis();
            return String.valueOf(l+a);
        }
    }
}
