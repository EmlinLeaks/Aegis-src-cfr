/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.TypeParameterMatcher;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public abstract class TypeParameterMatcher {
    private static final TypeParameterMatcher NOOP = new TypeParameterMatcher(){

        public boolean match(Object msg) {
            return true;
        }
    };

    public static TypeParameterMatcher get(Class<?> parameterType) {
        Map<Class<?>, TypeParameterMatcher> getCache = InternalThreadLocalMap.get().typeParameterMatcherGetCache();
        TypeParameterMatcher matcher = getCache.get(parameterType);
        if (matcher != null) return matcher;
        matcher = parameterType == Object.class ? NOOP : new ReflectiveMatcher(parameterType);
        getCache.put(parameterType, (TypeParameterMatcher)matcher);
        return matcher;
    }

    public static TypeParameterMatcher find(Object object, Class<?> parametrizedSuperclass, String typeParamName) {
        Class<?> thisClass;
        TypeParameterMatcher matcher;
        Map<Class<?>, Map<String, TypeParameterMatcher>> findCache = InternalThreadLocalMap.get().typeParameterMatcherFindCache();
        Map<String, TypeParameterMatcher> map = findCache.get(thisClass = object.getClass());
        if (map == null) {
            map = new HashMap<String, TypeParameterMatcher>();
            findCache.put(thisClass, map);
        }
        if ((matcher = map.get((Object)typeParamName)) != null) return matcher;
        matcher = TypeParameterMatcher.get(TypeParameterMatcher.find0((Object)object, parametrizedSuperclass, (String)typeParamName));
        map.put((String)typeParamName, (TypeParameterMatcher)matcher);
        return matcher;
    }

    private static Class<?> find0(Object object, Class<?> parametrizedSuperclass, String typeParamName) {
        Class<?> thisClass;
        Class<?> currentClass = thisClass = object.getClass();
        do {
            int typeParamIndex;
            TypeVariable<Class<?>>[] typeParams;
            if (currentClass.getSuperclass() == parametrizedSuperclass) {
                typeParamIndex = -1;
                typeParams = currentClass.getSuperclass().getTypeParameters();
            } else {
                if ((currentClass = currentClass.getSuperclass()) != null) continue;
                return TypeParameterMatcher.fail(thisClass, (String)typeParamName);
            }
            for (int i = 0; i < typeParams.length; ++i) {
                if (!typeParamName.equals((Object)typeParams[i].getName())) continue;
                typeParamIndex = i;
                break;
            }
            if (typeParamIndex < 0) {
                throw new IllegalStateException((String)("unknown type parameter '" + typeParamName + "': " + parametrizedSuperclass));
            }
            Type genericSuperType = currentClass.getGenericSuperclass();
            if (!(genericSuperType instanceof ParameterizedType)) {
                return Object.class;
            }
            Type[] actualTypeParams = ((ParameterizedType)genericSuperType).getActualTypeArguments();
            Type actualTypeParam = actualTypeParams[typeParamIndex];
            if (actualTypeParam instanceof ParameterizedType) {
                actualTypeParam = ((ParameterizedType)actualTypeParam).getRawType();
            }
            if (actualTypeParam instanceof Class) {
                return (Class)actualTypeParam;
            }
            if (actualTypeParam instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType)actualTypeParam).getGenericComponentType();
                if (componentType instanceof ParameterizedType) {
                    componentType = ((ParameterizedType)componentType).getRawType();
                }
                if (componentType instanceof Class) {
                    return Array.newInstance((Class)componentType, (int)0).getClass();
                }
            }
            if (!(actualTypeParam instanceof TypeVariable)) return TypeParameterMatcher.fail(thisClass, (String)typeParamName);
            TypeVariable v = (TypeVariable)actualTypeParam;
            currentClass = thisClass;
            if (!(v.getGenericDeclaration() instanceof Class)) {
                return Object.class;
            }
            parametrizedSuperclass = (Class)v.getGenericDeclaration();
            typeParamName = v.getName();
            if (!parametrizedSuperclass.isAssignableFrom(thisClass)) return Object.class;
        } while (true);
    }

    private static Class<?> fail(Class<?> type, String typeParamName) {
        throw new IllegalStateException((String)("cannot determine the type of the type parameter '" + typeParamName + "': " + type));
    }

    public abstract boolean match(Object var1);

    TypeParameterMatcher() {
    }
}

