/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.proto.xmlrpc;

import com.mysql.fabric.FabricCommunicationException;
import com.mysql.fabric.proto.xmlrpc.XmlRpcMethodCaller;
import com.mysql.fabric.xmlrpc.Client;
import com.mysql.fabric.xmlrpc.base.Array;
import com.mysql.fabric.xmlrpc.base.Data;
import com.mysql.fabric.xmlrpc.base.Member;
import com.mysql.fabric.xmlrpc.base.MethodCall;
import com.mysql.fabric.xmlrpc.base.MethodResponse;
import com.mysql.fabric.xmlrpc.base.Param;
import com.mysql.fabric.xmlrpc.base.Params;
import com.mysql.fabric.xmlrpc.base.Struct;
import com.mysql.fabric.xmlrpc.base.Value;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InternalXmlRpcMethodCaller
implements XmlRpcMethodCaller {
    private Client xmlRpcClient;

    public InternalXmlRpcMethodCaller(String url) throws FabricCommunicationException {
        try {
            this.xmlRpcClient = new Client((String)url);
            return;
        }
        catch (MalformedURLException ex) {
            throw new FabricCommunicationException((Throwable)ex);
        }
    }

    private Object unwrapValue(Value v) {
        if (v.getType() == 8) {
            return this.methodResponseArrayToList((Array)((Array)v.getValue()));
        }
        if (v.getType() != 7) return v.getValue();
        HashMap<String, Object> s = new HashMap<String, Object>();
        Iterator<Member> i$ = ((Struct)v.getValue()).getMember().iterator();
        while (i$.hasNext()) {
            Member m = i$.next();
            s.put(m.getName(), this.unwrapValue((Value)m.getValue()));
        }
        return s;
    }

    private List<Object> methodResponseArrayToList(Array array) {
        ArrayList<Object> result = new ArrayList<Object>();
        Iterator<Value> i$ = array.getData().getValue().iterator();
        while (i$.hasNext()) {
            Value v = i$.next();
            result.add((Object)this.unwrapValue((Value)v));
        }
        return result;
    }

    @Override
    public void setHeader(String name, String value) {
        this.xmlRpcClient.setHeader((String)name, (String)value);
    }

    @Override
    public void clearHeader(String name) {
        this.xmlRpcClient.clearHeader((String)name);
    }

    public List<Object> call(String methodName, Object[] args) throws FabricCommunicationException {
        MethodCall methodCall = new MethodCall();
        Params p = new Params();
        if (args == null) {
            args = new Object[]{};
        }
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null) {
                throw new NullPointerException((String)"nil args unsupported");
            }
            if (String.class.isAssignableFrom(args[i].getClass())) {
                p.addParam((Param)new Param((Value)new Value((String)((String)args[i]))));
                continue;
            }
            if (Double.class.isAssignableFrom(args[i].getClass())) {
                p.addParam((Param)new Param((Value)new Value((double)((Double)args[i]).doubleValue())));
                continue;
            }
            if (!Integer.class.isAssignableFrom(args[i].getClass())) throw new IllegalArgumentException((String)("Unknown argument type: " + args[i].getClass()));
            p.addParam((Param)new Param((Value)new Value((int)((Integer)args[i]).intValue())));
        }
        methodCall.setMethodName((String)methodName);
        methodCall.setParams((Params)p);
        try {
            MethodResponse resp = this.xmlRpcClient.execute((MethodCall)methodCall);
            return this.methodResponseArrayToList((Array)((Array)resp.getParams().getParam().get((int)0).getValue().getValue()));
        }
        catch (Exception ex) {
            throw new FabricCommunicationException((String)("Error during call to `" + methodName + "' (args=" + Arrays.toString((Object[])args) + ")"), (Throwable)ex);
        }
    }
}

