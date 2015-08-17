package com.xt.simplemq.util;

import com.xt.simplemq.constant.FileConstant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class Log implements FileConstant {

    private static short _level = 0xf;

    final public static short LEVEL_FATAL = 0x10;

    final public static short LEVEL_WARN = 0x08;

    final public static short LEVEL_NOTICE = 0x04;

    final public static short LEVEL_DEBUG = 0x02;

    final public static short LEVEL_CONSOLE = 0x01;

    final private static Map<Short, String> LEVEL_STRING = new HashMap<Short, String>() {
        private static final long serialVersionUID = 1L;

        {
            put(LEVEL_FATAL, "fatal");
            put(LEVEL_WARN, "warning");
            put(LEVEL_NOTICE, "notice");
            put(LEVEL_DEBUG, "debug");
        }
    };

    private static String _name = "unknown";

    private static String _path = "";

    private static File file;

    private static FileWriter fileWriter;

    private static File errFile;

    private static FileWriter errFileWriter;

    private static FileWriter finalWriter;

    public static void init(String name, String path, short level) {
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

    public static void debug(String msg) {
        log(msg, LEVEL_DEBUG, null);
    }

    public static void debug(String msg, Map<String, String> argv) {
        log(msg, LEVEL_DEBUG, argv);
    }

    public static void notice(String msg) {
        log(msg, LEVEL_NOTICE, null);
    }

    public static void notice(String msg, Map<String, String> argv) {
        log(msg, LEVEL_NOTICE, argv);
    }

    public static void warning(String msg) {
        log(msg, LEVEL_WARN, null);
    }

    public static void warning(String msg, Map<String, String> argv) {
        log(msg, LEVEL_WARN, argv);
    }

    public static void warning(Exception e) {
        log(e.toString(), LEVEL_WARN, null);
    }

    public static void fatal(String msg) {
        log(msg, LEVEL_FATAL, null);
    }

    public static void fatal(String msg, Map<String, String> argv) {
        log(msg, LEVEL_FATAL, argv);
    }

    public static void fatal(Exception e) {
        log(e.toString(), LEVEL_FATAL, null);
    }

    public static void close() {
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

    private synchronized static void log(String msg, short level, Map<String, String> argv) {
        if ((_level & level) == 0)
            return;

        checkLogFileStatus();
        try {
            finalWriter = level >= LEVEL_WARN ? errFileWriter : fileWriter;
            StringBuffer argvStr = new StringBuffer();
            if (null != argv && !argv.isEmpty()) {
                for (String k : argv.keySet()) {
                    argvStr.append(String.format(" [%s:%s]", k, argv.get(k)));
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String logStr = String
                    .format("[%s] [%s] [%s] [thread:%s]%s [%s]\n", formatter.format(new Date()), getLevelString(level),
                            _name, Thread.currentThread().getName(), argvStr.toString(), msg);
            finalWriter.write(logStr);
            finalWriter.flush();
            if ((_level & LEVEL_CONSOLE) == LEVEL_CONSOLE) {
                System.out.print(logStr);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkLogFileStatus() {
        if (errFile.exists() == false) {
            try {
                errFile.createNewFile();
            }
            catch (IOException ignore) {
            }
            try {
                errFileWriter.close();
            }
            catch (IOException ignore) {
            }
            try {
                errFileWriter = new FileWriter(errFile);
            }
            catch (IOException ignore) {
            }
        }
        if (file.exists() == false) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
            }
            try {
                fileWriter.close();
            }
            catch (IOException ignore) {
            }
            try {
                fileWriter = new FileWriter(file);
            }
            catch (IOException e) {
            }
        }
    }

    private static String getLevelString(short level) {
        return LEVEL_STRING.get(level);
    }

}
