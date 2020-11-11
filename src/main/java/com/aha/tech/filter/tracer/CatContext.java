//package com.aha.tech.filter.cat;
//
//import com.dianping.cat.Cat;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author: luweihong
// * @Date: 2019/11/19
// */
//public class CatContext implements Cat.Context {
//
//    private Map<String, String> properties = new HashMap<>();
//
//    @Override
//    public void addProperty(String key, String value) {
//        properties.put(key, value);
//    }
//
//    @Override
//    public String getProperty(String key) {
//        return properties.get(key);
//    }
//
//    @Override
//    public String toString() {
//        return "CatContext{"
//                + "properties=" + properties + '}';
//    }
//}