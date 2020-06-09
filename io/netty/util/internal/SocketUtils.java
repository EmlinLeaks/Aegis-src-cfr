/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.SuppressJava6Requirement;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

public final class SocketUtils {
    private SocketUtils() {
    }

    public static void connect(Socket socket, SocketAddress remoteAddress, int timeout) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>((Socket)socket, (SocketAddress)remoteAddress, (int)timeout){
                final /* synthetic */ Socket val$socket;
                final /* synthetic */ SocketAddress val$remoteAddress;
                final /* synthetic */ int val$timeout;
                {
                    this.val$socket = socket;
                    this.val$remoteAddress = socketAddress;
                    this.val$timeout = n;
                }

                public Void run() throws IOException {
                    this.val$socket.connect((SocketAddress)this.val$remoteAddress, (int)this.val$timeout);
                    return null;
                }
            });
            return;
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }

    public static void bind(Socket socket, SocketAddress bindpoint) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>((Socket)socket, (SocketAddress)bindpoint){
                final /* synthetic */ Socket val$socket;
                final /* synthetic */ SocketAddress val$bindpoint;
                {
                    this.val$socket = socket;
                    this.val$bindpoint = socketAddress;
                }

                public Void run() throws IOException {
                    this.val$socket.bind((SocketAddress)this.val$bindpoint);
                    return null;
                }
            });
            return;
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }

    public static boolean connect(SocketChannel socketChannel, SocketAddress remoteAddress) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>((SocketChannel)socketChannel, (SocketAddress)remoteAddress){
                final /* synthetic */ SocketChannel val$socketChannel;
                final /* synthetic */ SocketAddress val$remoteAddress;
                {
                    this.val$socketChannel = socketChannel;
                    this.val$remoteAddress = socketAddress;
                }

                public Boolean run() throws IOException {
                    return Boolean.valueOf((boolean)this.val$socketChannel.connect((SocketAddress)this.val$remoteAddress));
                }
            }).booleanValue();
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    public static void bind(SocketChannel socketChannel, SocketAddress address) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>((SocketChannel)socketChannel, (SocketAddress)address){
                final /* synthetic */ SocketChannel val$socketChannel;
                final /* synthetic */ SocketAddress val$address;
                {
                    this.val$socketChannel = socketChannel;
                    this.val$address = socketAddress;
                }

                public Void run() throws IOException {
                    this.val$socketChannel.bind((SocketAddress)this.val$address);
                    return null;
                }
            });
            return;
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }

    public static SocketChannel accept(ServerSocketChannel serverSocketChannel) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>((ServerSocketChannel)serverSocketChannel){
                final /* synthetic */ ServerSocketChannel val$serverSocketChannel;
                {
                    this.val$serverSocketChannel = serverSocketChannel;
                }

                public SocketChannel run() throws IOException {
                    return this.val$serverSocketChannel.accept();
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    public static void bind(DatagramChannel networkChannel, SocketAddress address) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>((DatagramChannel)networkChannel, (SocketAddress)address){
                final /* synthetic */ DatagramChannel val$networkChannel;
                final /* synthetic */ SocketAddress val$address;
                {
                    this.val$networkChannel = datagramChannel;
                    this.val$address = socketAddress;
                }

                public Void run() throws IOException {
                    this.val$networkChannel.bind((SocketAddress)this.val$address);
                    return null;
                }
            });
            return;
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }

    public static SocketAddress localSocketAddress(ServerSocket socket) {
        return AccessController.doPrivileged(new PrivilegedAction<SocketAddress>((ServerSocket)socket){
            final /* synthetic */ ServerSocket val$socket;
            {
                this.val$socket = serverSocket;
            }

            public SocketAddress run() {
                return this.val$socket.getLocalSocketAddress();
            }
        });
    }

    public static InetAddress addressByName(String hostname) throws UnknownHostException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>((String)hostname){
                final /* synthetic */ String val$hostname;
                {
                    this.val$hostname = string;
                }

                public InetAddress run() throws UnknownHostException {
                    return InetAddress.getByName((String)this.val$hostname);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (UnknownHostException)e.getCause();
        }
    }

    public static InetAddress[] allAddressesByName(String hostname) throws UnknownHostException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress[]>((String)hostname){
                final /* synthetic */ String val$hostname;
                {
                    this.val$hostname = string;
                }

                public InetAddress[] run() throws UnknownHostException {
                    return InetAddress.getAllByName((String)this.val$hostname);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (UnknownHostException)e.getCause();
        }
    }

    public static InetSocketAddress socketAddress(String hostname, int port) {
        return AccessController.doPrivileged(new PrivilegedAction<InetSocketAddress>((String)hostname, (int)port){
            final /* synthetic */ String val$hostname;
            final /* synthetic */ int val$port;
            {
                this.val$hostname = string;
                this.val$port = n;
            }

            public InetSocketAddress run() {
                return new InetSocketAddress((String)this.val$hostname, (int)this.val$port);
            }
        });
    }

    public static Enumeration<InetAddress> addressesFromNetworkInterface(NetworkInterface intf) {
        return AccessController.doPrivileged(new PrivilegedAction<Enumeration<InetAddress>>((NetworkInterface)intf){
            final /* synthetic */ NetworkInterface val$intf;
            {
                this.val$intf = networkInterface;
            }

            public Enumeration<InetAddress> run() {
                return this.val$intf.getInetAddresses();
            }
        });
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    public static InetAddress loopbackAddress() {
        return AccessController.doPrivileged(new PrivilegedAction<InetAddress>(){

            public InetAddress run() {
                if (io.netty.util.internal.PlatformDependent.javaVersion() >= 7) {
                    return InetAddress.getLoopbackAddress();
                }
                try {
                    return InetAddress.getByName(null);
                }
                catch (UnknownHostException e) {
                    throw new java.lang.IllegalStateException((Throwable)e);
                }
            }
        });
    }

    public static byte[] hardwareAddressFromNetworkInterface(NetworkInterface intf) throws SocketException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<byte[]>((NetworkInterface)intf){
                final /* synthetic */ NetworkInterface val$intf;
                {
                    this.val$intf = networkInterface;
                }

                public byte[] run() throws SocketException {
                    return this.val$intf.getHardwareAddress();
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (SocketException)e.getCause();
        }
    }
}

