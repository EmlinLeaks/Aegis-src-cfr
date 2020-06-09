/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Array;
import com.mysql.fabric.xmlrpc.base.Data;
import com.mysql.fabric.xmlrpc.base.Fault;
import com.mysql.fabric.xmlrpc.base.Member;
import com.mysql.fabric.xmlrpc.base.MethodResponse;
import com.mysql.fabric.xmlrpc.base.Param;
import com.mysql.fabric.xmlrpc.base.Params;
import com.mysql.fabric.xmlrpc.base.Struct;
import com.mysql.fabric.xmlrpc.base.Value;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ResponseParser
extends DefaultHandler {
    private MethodResponse resp = null;
    Stack<Object> elNames = new Stack<E>();
    Stack<Object> objects = new Stack<E>();

    public MethodResponse getMethodResponse() {
        return this.resp;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String thisElement = qName;
        if (thisElement == null) return;
        this.elNames.push((Object)thisElement);
        if (thisElement.equals((Object)"methodResponse")) {
            this.objects.push((Object)new MethodResponse());
            return;
        }
        if (thisElement.equals((Object)"params")) {
            this.objects.push((Object)new Params());
            return;
        }
        if (thisElement.equals((Object)"param")) {
            this.objects.push((Object)new Param());
            return;
        }
        if (thisElement.equals((Object)"value")) {
            this.objects.push((Object)new Value());
            return;
        }
        if (thisElement.equals((Object)"array")) {
            this.objects.push((Object)new Array());
            return;
        }
        if (thisElement.equals((Object)"data")) {
            this.objects.push((Object)new Data());
            return;
        }
        if (thisElement.equals((Object)"struct")) {
            this.objects.push((Object)new Struct());
            return;
        }
        if (thisElement.equals((Object)"member")) {
            this.objects.push((Object)new Member());
            return;
        }
        if (!thisElement.equals((Object)"fault")) return;
        this.objects.push((Object)new Fault());
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String thisElement = (String)this.elNames.pop();
        if (thisElement == null) return;
        if (thisElement.equals((Object)"methodResponse")) {
            this.resp = (MethodResponse)this.objects.pop();
            return;
        }
        if (thisElement.equals((Object)"params")) {
            Params pms = (Params)this.objects.pop();
            MethodResponse parent = (MethodResponse)this.objects.peek();
            parent.setParams((Params)pms);
            return;
        }
        if (thisElement.equals((Object)"param")) {
            Param p = (Param)this.objects.pop();
            Params parent = (Params)this.objects.peek();
            parent.addParam((Param)p);
            return;
        }
        if (thisElement.equals((Object)"value")) {
            Value v = (Value)this.objects.pop();
            Object parent = this.objects.peek();
            if (parent instanceof Data) {
                ((Data)parent).addValue((Value)v);
                return;
            }
            if (parent instanceof Param) {
                ((Param)parent).setValue((Value)v);
                return;
            }
            if (parent instanceof Member) {
                ((Member)parent).setValue((Value)v);
                return;
            }
            if (!(parent instanceof Fault)) return;
            ((Fault)parent).setValue((Value)v);
            return;
        }
        if (thisElement.equals((Object)"array")) {
            Array a = (Array)this.objects.pop();
            Value parent = (Value)this.objects.peek();
            parent.setArray((Array)a);
            return;
        }
        if (thisElement.equals((Object)"data")) {
            Data d = (Data)this.objects.pop();
            Array parent = (Array)this.objects.peek();
            parent.setData((Data)d);
            return;
        }
        if (thisElement.equals((Object)"struct")) {
            Struct s = (Struct)this.objects.pop();
            Value parent = (Value)this.objects.peek();
            parent.setStruct((Struct)s);
            return;
        }
        if (thisElement.equals((Object)"member")) {
            Member m = (Member)this.objects.pop();
            Struct parent = (Struct)this.objects.peek();
            parent.addMember((Member)m);
            return;
        }
        if (!thisElement.equals((Object)"fault")) return;
        Fault f = (Fault)this.objects.pop();
        MethodResponse parent = (MethodResponse)this.objects.peek();
        parent.setFault((Fault)f);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            String thisElement = (String)this.elNames.peek();
            if (thisElement == null) return;
            if (thisElement.equals((Object)"name")) {
                ((Member)this.objects.peek()).setName((String)new String((char[])ch, (int)start, (int)length));
                return;
            }
            if (thisElement.equals((Object)"value")) {
                ((Value)this.objects.peek()).appendString((String)new String((char[])ch, (int)start, (int)length));
                return;
            }
            if (thisElement.equals((Object)"i4") || thisElement.equals((Object)"int")) {
                ((Value)this.objects.peek()).setInt((String)new String((char[])ch, (int)start, (int)length));
                return;
            }
            if (thisElement.equals((Object)"boolean")) {
                ((Value)this.objects.peek()).setBoolean((String)new String((char[])ch, (int)start, (int)length));
                return;
            }
            if (thisElement.equals((Object)"string")) {
                ((Value)this.objects.peek()).appendString((String)new String((char[])ch, (int)start, (int)length));
                return;
            }
            if (thisElement.equals((Object)"double")) {
                ((Value)this.objects.peek()).setDouble((String)new String((char[])ch, (int)start, (int)length));
                return;
            }
            if (thisElement.equals((Object)"dateTime.iso8601")) {
                ((Value)this.objects.peek()).setDateTime((String)new String((char[])ch, (int)start, (int)length));
                return;
            }
            if (!thisElement.equals((Object)"base64")) return;
            ((Value)this.objects.peek()).setBase64((byte[])new String((char[])ch, (int)start, (int)length).getBytes());
            return;
        }
        catch (Exception e) {
            throw new SAXParseException((String)e.getMessage(), null, (Exception)e);
        }
    }
}

