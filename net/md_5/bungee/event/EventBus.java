/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventHandlerMethod;

public class EventBus {
    private final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = new HashMap<Class<?>, Map<Byte, Map<Object, Method[]>>>();
    private final Map<Class<?>, EventHandlerMethod[]> byEventBaked = new ConcurrentHashMap<Class<?>, EventHandlerMethod[]>();
    private final Lock lock = new ReentrantLock();
    private final Logger logger;

    public EventBus() {
        this(null);
    }

    public EventBus(Logger logger) {
        this.logger = logger == null ? Logger.getLogger((String)"global") : logger;
    }

    public void post(Object event) {
        EventHandlerMethod[] handlers = this.byEventBaked.get(event.getClass());
        if (handlers == null) return;
        EventHandlerMethod[] arreventHandlerMethod = handlers;
        int n = arreventHandlerMethod.length;
        int n2 = 0;
        while (n2 < n) {
            EventHandlerMethod method = arreventHandlerMethod[n2];
            try {
                method.invoke((Object)event);
            }
            catch (IllegalAccessException ex) {
                throw new Error((String)("Method became inaccessible: " + event), (Throwable)ex);
            }
            catch (IllegalArgumentException ex) {
                throw new Error((String)("Method rejected target/argument: " + event), (Throwable)ex);
            }
            catch (InvocationTargetException ex) {
                this.logger.log((Level)Level.WARNING, (String)MessageFormat.format((String)"Error dispatching event {0} to listener {1}", (Object[])new Object[]{event, method.getListener()}), (Throwable)ex.getCause());
            }
            ++n2;
        }
    }

    private Map<Class<?>, Map<Byte, Set<Method>>> findHandlers(Object listener) {
        HashMap<Class<?>, Map<Byte, Set<Method>>> handler = new HashMap<Class<?>, Map<Byte, Set<Method>>>();
        Method[] arrmethod = listener.getClass().getDeclaredMethods();
        int n = arrmethod.length;
        int n2 = 0;
        while (n2 < n) {
            Method m = arrmethod[n2];
            EventHandler annotation = m.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    this.logger.log((Level)Level.INFO, (String)"Method {0} in class {1} annotated with {2} does not have single argument", (Object[])new Object[]{m, listener.getClass(), annotation});
                } else {
                    HashSet<Method> priority;
                    HashMap<Byte, HashSet<Method>> prioritiesMap = (HashMap<Byte, HashSet<Method>>)handler.get(params[0]);
                    if (prioritiesMap == null) {
                        prioritiesMap = new HashMap<Byte, HashSet<Method>>();
                        handler.put(params[0], prioritiesMap);
                    }
                    if ((priority = (HashSet<Method>)prioritiesMap.get((Object)Byte.valueOf((byte)annotation.priority()))) == null) {
                        priority = new HashSet<Method>();
                        prioritiesMap.put(Byte.valueOf((byte)annotation.priority()), priority);
                    }
                    priority.add(m);
                }
            }
            ++n2;
        }
        return handler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void register(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = this.findHandlers((Object)listener);
        this.lock.lock();
        try {
            Iterator<Map.Entry<Class<?>, Map<Byte, Set<Method>>>> iterator = handler.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Class<?>, Map<Byte, Set<Method>>> e = iterator.next();
                Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.get(e.getKey());
                if (prioritiesMap == null) {
                    prioritiesMap = new HashMap<Byte, Map<Object, Method[]>>();
                    this.byListenerAndPriority.put(e.getKey(), prioritiesMap);
                }
                for (Map.Entry<Byte, Set<Method>> entry : e.getValue().entrySet()) {
                    Map<Object, Method[]> currentPriorityMap = prioritiesMap.get((Object)entry.getKey());
                    if (currentPriorityMap == null) {
                        currentPriorityMap = new HashMap<Object, Method[]>();
                        prioritiesMap.put((Byte)entry.getKey(), currentPriorityMap);
                    }
                    Method[] baked = new Method[entry.getValue().size()];
                    currentPriorityMap.put((Object)listener, (Method[])entry.getValue().toArray(baked));
                }
                this.bakeHandlers(e.getKey());
            }
            return;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unregister(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = this.findHandlers((Object)listener);
        this.lock.lock();
        try {
            Iterator<Map.Entry<Class<?>, Map<Byte, Set<Method>>>> iterator = handler.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Class<?>, Map<Byte, Set<Method>>> e = iterator.next();
                Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.get(e.getKey());
                if (prioritiesMap != null) {
                    for (Byte priority : e.getValue().keySet()) {
                        Map<Object, Method[]> currentPriority = prioritiesMap.get((Object)priority);
                        if (currentPriority == null) continue;
                        currentPriority.remove((Object)listener);
                        if (!currentPriority.isEmpty()) continue;
                        prioritiesMap.remove((Object)priority);
                    }
                    if (prioritiesMap.isEmpty()) {
                        this.byListenerAndPriority.remove(e.getKey());
                    }
                }
                this.bakeHandlers(e.getKey());
            }
            return;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void bakeHandlers(Class<?> eventClass) {
        byte by;
        Map<Byte, Map<Object, Method[]>> handlersByPriority = this.byListenerAndPriority.get(eventClass);
        if (handlersByPriority == null) {
            this.byEventBaked.remove(eventClass);
            return;
        }
        ArrayList<EventHandlerMethod> handlersList = new ArrayList<EventHandlerMethod>((int)(handlersByPriority.size() * 2));
        byte value = -128;
        do {
            Map<Object, Method[]> handlersByListener;
            if ((handlersByListener = handlersByPriority.get((Object)Byte.valueOf((byte)value))) != null) {
                for (Map.Entry<Object, Method[]> listenerHandlers : handlersByListener.entrySet()) {
                    for (Method method : listenerHandlers.getValue()) {
                        EventHandlerMethod ehm = new EventHandlerMethod((Object)listenerHandlers.getKey(), (Method)method);
                        handlersList.add(ehm);
                    }
                }
            }
            by = value;
            value = (byte)(value + 1);
        } while (by < 127);
        this.byEventBaked.put(eventClass, (EventHandlerMethod[])handlersList.toArray(new EventHandlerMethod[handlersList.size()]));
    }
}

