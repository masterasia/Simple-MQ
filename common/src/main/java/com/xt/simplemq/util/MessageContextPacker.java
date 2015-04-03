package com.xt.simplemq.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class MessageContextPacker {
    public String pack(MessageContext v) {
        StringBuffer bf = packBuffer(v);
        return bf.toString();
    }

    @SuppressWarnings("unchecked")
    public StringBuffer packBuffer(MessageContext v) {
        Object o = v.getMessageContext();
        if (null == o) {
            return packBuffer();
        }

        if (o instanceof Integer) {
            return packBuffer((Integer) o);
        } else if (o instanceof Short) {
            return packBuffer((Short) o);
        } else if (o instanceof Boolean) {
            return packBuffer((Boolean) o);
        } else if (o instanceof Long) {
            return packBuffer((Long) o);
        } else if (o instanceof Float) {
            return packBuffer((Float) o);
        } else if (o instanceof Double) {
            return packBuffer((Double) o);
        } else if (o instanceof String) {
            return packBuffer((String) o);
        } else if (o instanceof List<?>) {
            return packBuffer((List<MessageContext>) o);
        } else if (o instanceof Map<?, ?>) {
            return packBuffer((Map<String, MessageContext>) o);
        } else {
            return null;
        }
    }

    private StringBuffer packBuffer(Map<String, MessageContext> v) {
        StringBuffer sb = new StringBuffer();

        sb.append("{");
        boolean is = false;
        for (String k : v.keySet()) {
            if (is) {
                sb.append(",");
            }
            is = true;
            MessageContext sv = v.get(k);
            sb.append(packBuffer(k));
            sb.append(":");
            sb.append(packBuffer(sv));
        }
        sb.append("}");
        return sb;
    }

    private StringBuffer packBuffer(List<MessageContext> v) {
        StringBuffer sb = new StringBuffer();

        sb.append('[');
        Iterator<MessageContext> itr = v.iterator();
        boolean is = false;
        while (itr.hasNext()) {
            if (is) {
                sb.append(',');
            }
            is = true;
            MessageContext nv = itr.next();
            sb.append(packBuffer(nv));
        }
        sb.append(']');
        return sb;
    }

    private StringBuffer packBuffer(String v) {
        StringBuffer sb = new StringBuffer();
        sb.append('"');
        sb.append(UnicodePacker.toUnicode(v));
        sb.append('"');
        return sb;
    }

    private StringBuffer packBuffer(Float v) {
        return new StringBuffer(v.toString());
    }

    private StringBuffer packBuffer(Double v) {
        return new StringBuffer(v.toString());
    }

    private StringBuffer packBuffer(Boolean v) {
        return new StringBuffer(v.toString());
    }

    private StringBuffer packBuffer(Long v) {
        return new StringBuffer(v.toString());
    }

    private StringBuffer packBuffer() {
        return new StringBuffer("null");
    }

    private StringBuffer packBuffer(Integer v) {
        return new StringBuffer(v.toString());
    }

    private StringBuffer packBuffer(Short v) {
        return new StringBuffer(v.toString());
    }
}
