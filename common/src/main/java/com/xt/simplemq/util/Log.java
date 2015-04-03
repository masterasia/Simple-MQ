package com.xt.simplemq.util;

import com.xt.simplemq.constant.FileConstant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class Log implements FileConstant{

    private static short _level = 0xf;

    private static String _name = "noname";

    private static String _path = "";

    private static File file;

    private static FileWriter fileWriter;

    private static File errFile;

    private static FileWriter errFileWriter;

    public static void init(String name, String path, short level){
        _level = level;
        _name = name;

        if (path.endsWith(File.separator))
            _path = path;
        else
            _path = path + File.separator;

        File filePath = new File(_path);

        if (!filePath.exists() && !filePath.isDirectory())
            filePath.mkdirs();

        file = new File(_path + _name + LOG_PATH_END);

        errFile = new File(_path + _name + ERR_LOG_PATH_END);

        try {
            fileWriter = new FileWriter(file, true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            errFileWriter = new FileWriter(errFile, true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(){
        try {
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            errFileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized static void log(String msg, short level){
        if ((_level & level) == 0)
            return ;
    }

}
