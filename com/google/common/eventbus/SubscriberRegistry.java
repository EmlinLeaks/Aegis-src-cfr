/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.Weak
 */
package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.Subscriber;
import com.google.common.eventbus.SubscriberRegistry;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.j2objc.annotations.Weak;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

final class SubscriberRegistry {
    private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers = Maps.newConcurrentMap();
    @Weak
    private final EventBus bus;
    private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, ImmutableList<Method>>(){

        public ImmutableList<Method> load(Class<?> concreteClass) throws java.lang.Exception {
            return SubscriberRegistry.access$000(concreteClass);
        }
    });
    private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, ImmutableSet<Class<?>>>(){

        public ImmutableSet<Class<?>> load(Class<?> concreteClass) {
            return ImmutableSet.copyOf(TypeToken.of(concreteClass).getTypes().rawTypes());
        }
    });

    SubscriberRegistry(EventBus bus) {
        this.bus = Preconditions.checkNotNull(bus);
    }

    void register(Object listener) {
        Multimap<Class<?>, Subscriber> listenerMethods = this.findAllSubscribers((Object)listener);
        Iterator<Map.Entry<Class<?>, Collection<Subscriber>>> i$ = listenerMethods.asMap().entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<Class<?>, Collection<Subscriber>> entry = i$.next();
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> eventMethodsInListener = entry.getValue();
            CopyOnWriteArraySet<Subscriber> eventSubscribers = (CopyOnWriteArraySet<Subscriber>)this.subscribers.get(eventType);
            if (eventSubscribers == null) {
                CopyOnWriteArraySet<E> newSet = new CopyOnWriteArraySet<E>();
                eventSubscribers = MoreObjects.firstNonNull(this.subscribers.putIfAbsent(eventType, newSet), newSet);
            }
            eventSubscribers.addAll(eventMethodsInListener);
        }
    }

    void unregister(Object listener) {
        Collection<Subscriber> listenerMethodsForType;
        CopyOnWriteArraySet currentSubscribers;
        Multimap<Class<?>, Subscriber> listenerMethods = this.findAllSubscribers((Object)listener);
        Iterator<Map.Entry<Class<?>, Collection<Subscriber>>> i$ = listenerMethods.asMap().entrySet().iterator();
        do {
            if (!i$.hasNext()) return;
            Map.Entry<Class<?>, Collection<Subscriber>> entry = i$.next();
            Class<?> eventType = entry.getKey();
            listenerMethodsForType = entry.getValue();
            currentSubscribers = (CopyOnWriteArraySet)this.subscribers.get(eventType);
            if (currentSubscribers == null) throw new IllegalArgumentException((String)("missing event subscriber for an annotated method. Is " + listener + " registered?"));
        } while (currentSubscribers.removeAll(listenerMethodsForType));
        throw new IllegalArgumentException((String)("missing event subscriber for an annotated method. Is " + listener + " registered?"));
    }

    @VisibleForTesting
    Set<Subscriber> getSubscribersForTesting(Class<?> eventType) {
        return (Set)MoreObjects.firstNonNull(this.subscribers.get(eventType), ImmutableSet.<E>of());
    }

    Iterator<Subscriber> getSubscribers(Object event) {
        ImmutableSet<Class<?>> eventTypes = SubscriberRegistry.flattenHierarchy(event.getClass());
        ArrayList<Iterator<E>> subscriberIterators = Lists.newArrayListWithCapacity((int)eventTypes.size());
        Iterator i$ = eventTypes.iterator();
        while (i$.hasNext()) {
            Class eventType = (Class)i$.next();
            CopyOnWriteArraySet eventSubscribers = (CopyOnWriteArraySet)this.subscribers.get((Object)eventType);
            if (eventSubscribers == null) continue;
            subscriberIterators.add(eventSubscribers.iterator());
        }
        return Iterators.concat(subscriberIterators.iterator());
    }

    private Multimap<Class<?>, Subscriber> findAllSubscribers(Object listener) {
        HashMultimap<Class<?>, Subscriber> methodsInListener = HashMultimap.create();
        Class<?> clazz = listener.getClass();
        Iterator i$ = SubscriberRegistry.getAnnotatedMethods(clazz).iterator();
        while (i$.hasNext()) {
            Method method = (Method)i$.next();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> eventType = parameterTypes[0];
            methodsInListener.put(eventType, (Subscriber)Subscriber.create((EventBus)this.bus, (Object)listener, (Method)method));
        }
        return methodsInListener;
    }

    private static ImmutableList<Method> getAnnotatedMethods(Class<?> clazz) {
        return subscriberMethodsCache.getUnchecked(clazz);
    }

    private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
        Set<Class<T>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
        HashMap<MethodIdentifier, Method> identifiers = Maps.newHashMap();
        Iterator<Class<T>> i$ = supertypes.iterator();
        block0 : while (i$.hasNext()) {
            Class<T> supertype = i$.next();
            Method[] arr$ = supertype.getDeclaredMethods();
            int len$ = arr$.length;
            int i$2 = 0;
            do {
                if (i$2 >= len$) continue block0;
                Method method = arr$[i$2];
                if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Preconditions.checkArgument((boolean)(parameterTypes.length == 1), (String)"Method %s has @Subscribe annotation but has %s parameters.Subscriber methods must have exactly 1 parameter.", (Object)method, (int)parameterTypes.length);
                    MethodIdentifier ident = new MethodIdentifier((Method)method);
                    if (!identifiers.containsKey((Object)ident)) {
                        identifiers.put(ident, method);
                    }
                }
                ++i$2;
            } while (true);
            break;
        }
        return ImmutableList.copyOf(identifiers.values());
    }

    @VisibleForTesting
    static ImmutableSet<Class<?>> flattenHierarchy(Class<?> concreteClass) {
        try {
            return flattenHierarchyCache.getUnchecked(concreteClass);
        }
        catch (UncheckedExecutionException e) {
            throw Throwables.propagate((Throwable)e.getCause());
        }
    }

    static /* synthetic */ ImmutableList access$000(Class x0) {
        return SubscriberRegistry.getAnnotatedMethodsNotCached(x0);
    }
}

