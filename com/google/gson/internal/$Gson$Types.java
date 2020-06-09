/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public final class $Gson$Types {
    static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    private $Gson$Types() {
        throw new UnsupportedOperationException();
    }

    public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type ... typeArguments) {
        return new ParameterizedTypeImpl((Type)ownerType, (Type)rawType, (Type[])typeArguments);
    }

    public static GenericArrayType arrayOf(Type componentType) {
        return new GenericArrayTypeImpl((Type)componentType);
    }

    public static WildcardType subtypeOf(Type bound) {
        return new WildcardTypeImpl((Type[])new Type[]{bound}, (Type[])EMPTY_TYPE_ARRAY);
    }

    public static WildcardType supertypeOf(Type bound) {
        return new WildcardTypeImpl((Type[])new Type[]{Object.class}, (Type[])new Type[]{bound});
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Type type2;
            Class c = (Class)type;
            if (c.isArray()) {
                type2 = new GenericArrayTypeImpl((Type)$Gson$Types.canonicalize(c.getComponentType()));
                return (Type)type2;
            }
            type2 = c;
            return (Type)type2;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)type;
            return new ParameterizedTypeImpl((Type)p.getOwnerType(), (Type)p.getRawType(), (Type[])p.getActualTypeArguments());
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType)type;
            return new GenericArrayTypeImpl((Type)g.getGenericComponentType());
        }
        if (!(type instanceof WildcardType)) return type;
        WildcardType w = (WildcardType)type;
        return new WildcardTypeImpl((Type[])w.getUpperBounds(), (Type[])w.getLowerBounds());
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            $Gson$Preconditions.checkArgument((boolean)(rawType instanceof Class));
            return (Class)rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance($Gson$Types.getRawType((Type)componentType), (int)0).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return $Gson$Types.getRawType((Type)((WildcardType)type).getUpperBounds()[0]);
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException((String)("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className));
    }

    static boolean equal(Object a, Object b) {
        if (a == b) return true;
        if (a == null) return false;
        if (!a.equals((Object)b)) return false;
        return true;
    }

    public static boolean equals(Type a, Type b) {
        if (a == b) {
            return true;
        }
        if (a instanceof Class) {
            return a.equals((Object)b);
        }
        if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType pa = (ParameterizedType)a;
            ParameterizedType pb = (ParameterizedType)b;
            if (!$Gson$Types.equal((Object)pa.getOwnerType(), (Object)pb.getOwnerType())) return false;
            if (!pa.getRawType().equals((Object)pb.getRawType())) return false;
            if (!Arrays.equals((Object[])pa.getActualTypeArguments(), (Object[])pb.getActualTypeArguments())) return false;
            return true;
        }
        if (a instanceof GenericArrayType) {
            if (!(b instanceof GenericArrayType)) {
                return false;
            }
            GenericArrayType ga = (GenericArrayType)a;
            GenericArrayType gb = (GenericArrayType)b;
            return $Gson$Types.equals((Type)ga.getGenericComponentType(), (Type)gb.getGenericComponentType());
        }
        if (a instanceof WildcardType) {
            if (!(b instanceof WildcardType)) {
                return false;
            }
            WildcardType wa = (WildcardType)a;
            WildcardType wb = (WildcardType)b;
            if (!Arrays.equals((Object[])wa.getUpperBounds(), (Object[])wb.getUpperBounds())) return false;
            if (!Arrays.equals((Object[])wa.getLowerBounds(), (Object[])wb.getLowerBounds())) return false;
            return true;
        }
        if (!(a instanceof TypeVariable)) return false;
        if (!(b instanceof TypeVariable)) {
            return false;
        }
        TypeVariable va = (TypeVariable)a;
        TypeVariable vb = (TypeVariable)b;
        if (va.getGenericDeclaration() != vb.getGenericDeclaration()) return false;
        if (!va.getName().equals((Object)vb.getName())) return false;
        return true;
    }

    static int hashCodeOrZero(Object o) {
        if (o == null) return 0;
        int n = o.hashCode();
        return n;
    }

    public static String typeToString(Type type) {
        String string;
        if (type instanceof Class) {
            string = ((Class)type).getName();
            return string;
        }
        string = type.toString();
        return string;
    }

    static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
        if (toResolve == rawType) {
            return context;
        }
        if (toResolve.isInterface()) {
            Class<?>[] interfaces = rawType.getInterfaces();
            int length = interfaces.length;
            for (int i = 0; i < length; ++i) {
                if (interfaces[i] == toResolve) {
                    return rawType.getGenericInterfaces()[i];
                }
                if (!toResolve.isAssignableFrom(interfaces[i])) continue;
                return $Gson$Types.getGenericSupertype((Type)rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
            }
        }
        if (rawType.isInterface()) return toResolve;
        while (rawType != Object.class) {
            Class<?> rawSupertype = rawType.getSuperclass();
            if (rawSupertype == toResolve) {
                return rawType.getGenericSuperclass();
            }
            if (toResolve.isAssignableFrom(rawSupertype)) {
                return $Gson$Types.getGenericSupertype((Type)rawType.getGenericSuperclass(), rawSupertype, toResolve);
            }
            rawType = rawSupertype;
        }
        return toResolve;
    }

    static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
        $Gson$Preconditions.checkArgument((boolean)supertype.isAssignableFrom(contextRawType));
        return $Gson$Types.resolve((Type)context, contextRawType, (Type)$Gson$Types.getGenericSupertype((Type)context, contextRawType, supertype));
    }

    public static Type getArrayComponentType(Type array) {
        Class<?> class_;
        if (array instanceof GenericArrayType) {
            class_ = ((GenericArrayType)array).getGenericComponentType();
            return class_;
        }
        class_ = ((Class)array).getComponentType();
        return class_;
    }

    public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
        Type collectionType = $Gson$Types.getSupertype((Type)context, contextRawType, Collection.class);
        if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
        }
        if (!(collectionType instanceof ParameterizedType)) return Object.class;
        return ((ParameterizedType)collectionType).getActualTypeArguments()[0];
    }

    public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
        if (context == Properties.class) {
            return new Type[]{String.class, String.class};
        }
        Type mapType = $Gson$Types.getSupertype((Type)context, contextRawType, Map.class);
        if (!(mapType instanceof ParameterizedType)) return new Type[]{Object.class, Object.class};
        ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
        return mapParameterizedType.getActualTypeArguments();
    }

    public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
        Type original;
        while (toResolve instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable)toResolve;
            toResolve = $Gson$Types.resolveTypeVariable((Type)context, contextRawType, typeVariable);
            if (toResolve != typeVariable) continue;
            return toResolve;
        }
        if (toResolve instanceof Class && ((Class)toResolve).isArray()) {
            Type type;
            Type newComponentType;
            original = (Class)toResolve;
            Class<?> componentType = ((Class)original).getComponentType();
            if (componentType == (newComponentType = $Gson$Types.resolve((Type)context, contextRawType, componentType))) {
                type = original;
                return type;
            }
            type = $Gson$Types.arrayOf((Type)newComponentType);
            return type;
        }
        if (toResolve instanceof GenericArrayType) {
            Type newComponentType;
            Type type;
            original = (GenericArrayType)toResolve;
            Type componentType = original.getGenericComponentType();
            if (componentType == (newComponentType = $Gson$Types.resolve((Type)context, contextRawType, (Type)componentType))) {
                type = original;
                return type;
            }
            type = $Gson$Types.arrayOf((Type)newComponentType);
            return type;
        }
        if (toResolve instanceof ParameterizedType) {
            Type type;
            original = (ParameterizedType)toResolve;
            Type ownerType = original.getOwnerType();
            Type newOwnerType = $Gson$Types.resolve((Type)context, contextRawType, (Type)ownerType);
            boolean changed = newOwnerType != ownerType;
            Type[] args = original.getActualTypeArguments();
            int length = args.length;
            for (int t = 0; t < length; ++t) {
                Type resolvedTypeArgument = $Gson$Types.resolve((Type)context, contextRawType, (Type)args[t]);
                if (resolvedTypeArgument == args[t]) continue;
                if (!changed) {
                    args = (Type[])args.clone();
                    changed = true;
                }
                args[t] = resolvedTypeArgument;
            }
            if (changed) {
                type = $Gson$Types.newParameterizedTypeWithOwner((Type)newOwnerType, (Type)original.getRawType(), (Type[])args);
                return type;
            }
            type = original;
            return type;
        }
        if (!(toResolve instanceof WildcardType)) return toResolve;
        original = (WildcardType)toResolve;
        Type[] originalLowerBound = original.getLowerBounds();
        Type[] originalUpperBound = original.getUpperBounds();
        if (originalLowerBound.length == 1) {
            Type lowerBound = $Gson$Types.resolve((Type)context, contextRawType, (Type)originalLowerBound[0]);
            if (lowerBound == originalLowerBound[0]) return original;
            return $Gson$Types.supertypeOf((Type)lowerBound);
        }
        if (originalUpperBound.length != 1) return original;
        Type upperBound = $Gson$Types.resolve((Type)context, contextRawType, (Type)originalUpperBound[0]);
        if (upperBound == originalUpperBound[0]) return original;
        return $Gson$Types.subtypeOf((Type)upperBound);
    }

    static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
        Class<?> declaredByRaw = $Gson$Types.declaringClassOf(unknown);
        if (declaredByRaw == null) {
            return unknown;
        }
        Type declaredBy = $Gson$Types.getGenericSupertype((Type)context, contextRawType, declaredByRaw);
        if (!(declaredBy instanceof ParameterizedType)) return unknown;
        int index = $Gson$Types.indexOf((Object[])declaredByRaw.getTypeParameters(), unknown);
        return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
    }

    private static int indexOf(Object[] array, Object toFind) {
        int i = 0;
        while (i < array.length) {
            if (toFind.equals((Object)array[i])) {
                return i;
            }
            ++i;
        }
        throw new NoSuchElementException();
    }

    private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
        ? genericDeclaration = typeVariable.getGenericDeclaration();
        if (!(genericDeclaration instanceof Class)) return null;
        Class class_ = (Class)genericDeclaration;
        return class_;
    }

    static void checkNotPrimitive(Type type) {
        $Gson$Preconditions.checkArgument((boolean)(!(type instanceof Class) || !((Class)type).isPrimitive()));
    }
}

