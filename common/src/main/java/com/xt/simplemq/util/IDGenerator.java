package com.xt.simplemq.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class IDGenerator {
    private String filename;

    private long   curMqID;

    public IDGenerator(String filename) {
        this.filename = filename;
        initWithFile();
    }

    public synchronized long nextID() {
        return ++curMqID;
    }

    private void initWithFile() {
        File f = new File(filename);
        if (f.exists() == false || f.length() == 0) {
            curMqID = -1l;
            return;
        }
        FileInputStream fin = null;
        DataInputStream din = null;
        try {
            fin = new FileInputStream(f);
            din = new DataInputStream(fin);
            curMqID = din.readLong();
        }
        catch (IOException e) {
            Log.fatal("Bad mqid file");
            e.printStackTrace();
        }
        finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            }
            catch (IOException ignore) {
            }
            try {
                if (din != null) {
                    din.close();
                }
            }
            catch (IOException ignore) {
            }
        }
    }
}
