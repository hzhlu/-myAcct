package com.macrolab.myAcct.common;

import com.macrolab.myAcct.service.DBService;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用的环境
 */
public class AppContext {
    public static DBService dbService;
    public static Map<String, Object> context = new HashMap<>();

}
