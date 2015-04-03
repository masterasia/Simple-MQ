package com.xt.simplemq.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class MessageContextUnpacker {
    private int i = 0;

    private int l = 0;

    private String raw;

    private char   c;

    public MessageContext unpack(String raw) throws UtilException {
        this.raw = raw;
        i = 0;
        l = raw.length();
        return unpackValue();
    }

    private MessageContext unpackValue() throws UtilException {
        nextChar();
        switch (c) {
        case '"':   //String
            return unpackString();
        case '[':   //Array
            return unpackArray();
        case '{':   //Map
            return unpackMap();
        case 'n':   //null
        case 'f':   //false
        case 't':   //true
            return unpackVar();
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '-':
            return unpackNumber();
        default:
            return null;
        }
    }

    private MessageContext unpackMap() throws UtilException {
        ++i;    //skip {

        HashMap<String, MessageContext> m = new HashMap<String, MessageContext>();
        while (true) {
            nextChar();
            if (c == '}') {
                ++i;
                break;
            } else if (c == ',') {
                ++i;    //skip ,
            } else {
                MessageContext k = unpackString();
                ++i;    //skip :
                MessageContext v = unpackValue();
                m.put((String) k.getMessageContext(), v);
            }
        }
        return new MessageContext(m);
    }

    private MessageContext unpackArray() throws UtilException {
        ++i;    //skip [

        ArrayList<MessageContext> l = new ArrayList<MessageContext>();
        while (true) {
            nextChar();
            if (c == ']') {
                ++i;
                break;
            } else {
                if (',' == c) {
                    ++i;
                }
                l.add(unpackValue());
            }
        }
        return new MessageContext(l);
    }

    private MessageContext unpackVar() throws UtilException {
        nextChar();
        if (c == 'n') {
            i += 4;
            return new MessageContext();
        } else if (c == 'f') {
            i += 5;
            return new MessageContext(false);
        } else if (c == 't') {
            i += 4;
            return new MessageContext(true);
        } else {
            throw new UtilException("Unrecongize char at index:" + i);
        }
    }

    private MessageContext unpackNumber() throws UtilException {
        StringBuffer sb = new StringBuffer();
        boolean isDot = false;
        while (i < l) {
            nextChar();
            if (c == '-' || (c >= '0' && c <= '9')) {
                sb.append(c);
            } else if (c == '.' && isDot == false) {
                isDot = true;
                sb.append(c);
            } else {
                break;
            }
            ++i;
        }
        MessageContext v;
        if (isDot) {
            v = new MessageContext(Double.parseDouble(sb.toString()));
        } else {
            v = new MessageContext(Long.parseLong(sb.toString()));
        }

        return v;
    }

    private MessageContext unpackString() throws UtilException {
        ++i;
        StringBuffer sb = new StringBuffer();
        while (i < l) {
            nextChar();
            if (c == '\\') {
                ++i;    //有一个u过滤掉
                nextChar();
                if (c == 'u') {   //unicode
                    int ivalue = 0;
                    ++i;
                    for (int j = 0; j < 4; j++) {
                        nextChar();
                        if (c >= '0' && c <= '9') {
                            ivalue = (ivalue << 4) + (c - '0');
                        } else if (c >= 'a' && c <= 'f') {
                            ivalue = (ivalue << 4) + 10 + (c - 'a');
                        } else if (c >= 'A' && c <= 'F') {
                            ivalue = (ivalue << 4) + 10 + (c - 'A');
                        } else {
                            throw new UtilException("Unrecongize char at index:" + i);
                        }
                        ++i;
                    }
                    sb.append((char) ivalue);
                } else {  //escape
                    switch (c) {
                    case 't':
                        sb.append('\t');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '"':
                        sb.append('\"');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    default:
                        throw new UtilException("Unrecongize char at index:" + i);
                    }
                    ++i;
                }
            } else if ('"' == c) { //结束条件
                ++i;
                break;
            } else {
                sb.append(c);
                ++i;
            }
        }
        return new MessageContext(sb.toString());
    }

    private void nextChar() throws UtilException {
        if (i >= l) {
            throw new UtilException("Unexpect end");
        }
        c = raw.charAt(i);
    }
}
