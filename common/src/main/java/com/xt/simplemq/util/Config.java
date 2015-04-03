package com.xt.simplemq.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class Config {

    private static String _path = "conf" + File.separator;

    private static Map<String, Map<String, String>> store = new HashMap<String, Map<String, String>>();

    /**
     * 设置配置文件的路径
     * <p/>
     * 可以是绝对路径，也可以是相对路径
     *
     * @param path
     */
    public static void setConfPath(String path) {
        if (path.endsWith(File.separator)) {
            _path = path;
        } else {
            _path = path + File.separator;
        }
    }

    /**
     * 获取配置数据
     *
     * @param file 存放的文件，省略了“.conf”部分，支持里面存在路径
     * @param k    配置文件内的key值
     * @return
     * @throws IOException 文件不存在，或者无法读取等，会抛出异常
     */
    public static String getConf(String file, String k) {
        Map<String, String> value;
        value = getValuesOfFile(file);

        if (value == null) {
            return null;
        }

        return value.get(k);
    }

    private static Map<String, String> getValuesOfFile(String file) {
        Map<String, String> values = store.get(file);
        if (values == null) {
            parseFile(file);
            values = store.get(file);
        }

        return values;
    }

    private static void parseFile(String file) {
        File f = new File(_path + file + ".conf");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(f));
            Map<String, String> file_params = new HashMap<String, String>();

            String line;
            while ((line = reader.readLine()) != null) {
                parseFileLine(file_params, line);
            }
            reader.close();

            if (!file_params.isEmpty()) {
                store.put(file, file_params);
            }
        }
        catch (IOException e) {
            //如果发现文件不存在，直接输出异常信息
            e.printStackTrace();
        }
    }

    private static void parseFileLine(Map<String, String> file_params, String line) {
        line = line.trim();
        //过滤掉空行
        if (line.isEmpty()) {
            return;
        }
        //过滤掉注释
        if (line.startsWith(";") || line.startsWith("#") || line.startsWith("[")) {
            return;
        }
        String[] kv = line.split("=", 2);
        file_params.put(kv[0].trim(), kv[1].trim());
    }
}
