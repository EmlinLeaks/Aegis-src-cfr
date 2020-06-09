/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.reflect;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class TypeToken<T> {
    final Class<? super T> rawType;
    final Type type;
    final int hashCode;

    protected TypeToken() {
        this.type = TypeToken.getSuperclassTypeParameter(this.getClass());
        this.rawType = $Gson$Types.getRawType((Type)this.type);
        this.hashCode = this.type.hashCode();
    }

    TypeToken(Type type) {
        this.type = $Gson$Types.canonicalize((Type)$Gson$Preconditions.checkNotNull(type));
        this.rawType = $Gson$Types.getRawType((Type)this.type);
        this.hashCode = this.type.hashCode();
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException((String)"Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType)superclass;
        return $Gson$Types.canonicalize((Type)parameterized.getActualTypeArguments()[0]);
    }

    public final Class<? super T> getRawType() {
        return this.rawType;
    }

    public final Type getType() {
        return this.type;
    }

    @Deprecated
    public boolean isAssignableFrom(Class<?> cls) {
        return this.isAssignableFrom(cls);
    }

    @Deprecated
    public boolean isAssignableFrom(Type from) {
        if (from == null) {
            return false;
        }
        if (this.type.equals((Object)from)) {
            return true;
        }
        if (this.type instanceof Class) {
            return this.rawType.isAssignableFrom($Gson$Types.getRawType((Type)from));
        }
        if (this.type instanceof ParameterizedType) {
            return TypeToken.isAssignableFrom((Type)from, (ParameterizedType)((ParameterizedType)this.type), new HashMap<String, Type>());
        }
        if (!(this.type instanceof GenericArrayType)) {
            throw TypeToken.buildUnexpectedTypeError((Type)this.type, Class.class, ParameterizedType.class, GenericArrayType.class);
        }
        if (!this.rawType.isAssignableFrom($Gson$Types.getRawType((Type)from))) return false;
        if (!TypeToken.isAssignableFrom((Type)from, (GenericArrayType)((GenericArrayType)this.type))) return false;
        return true;
    }

    @Deprecated
    public boolean isAssignableFrom(TypeToken<?> token) {
        return this.isAssignableFrom((Type)token.getType());
    }

    private static boolean isAssignableFrom(Type from, GenericArrayType to) {
        Type toGenericComponentType = to.getGenericComponentType();
        if (!(toGenericComponentType instanceof ParameterizedType)) return true;
        Type t = from;
        if (from instanceof GenericArrayType) {
            t = ((GenericArrayType)from).getGenericComponentType();
            return TypeToken.isAssignableFrom((Type)t, (ParameterizedType)((ParameterizedType)toGenericComponentType), new HashMap<String, Type>());
        }
        if (!(from instanceof Class)) return TypeToken.isAssignableFrom((Type)t, (ParameterizedType)((ParameterizedType)toGenericComponentType), new HashMap<String, Type>());
        Class<?> classType = (Class<?>)from;
        do {
            if (!classType.isArray()) {
                t = classType;
                return TypeToken.isAssignableFrom((Type)t, (ParameterizedType)((ParameterizedType)toGenericComponentType), new HashMap<String, Type>());
            }
            classType = classType.getComponentType();
        } while (true);
    }

    private static boolean isAssignableFrom(Type from, ParameterizedType to, Map<String, Type> typeVarMap) {
        Type[] tArgs;
        int i;
        if (from == null) {
            return false;
        }
        if (to.equals((Object)from)) {
            return true;
        }
        Class<?> clazz = $Gson$Types.getRawType((Type)from);
        ParameterizedType ptype = null;
        if (from instanceof ParameterizedType) {
            ptype = (ParameterizedType)from;
        }
        if (ptype != null) {
            tArgs = ptype.getActualTypeArguments();
            TypeVariable<Class<?>>[] tParams = clazz.getTypeParameters();
            for (i = 0; i < tArgs.length; ++i) {
                Type arg = tArgs[i];
                TypeVariable<Class<?>> var = tParams[i];
                while (arg instanceof TypeVariable) {
                    TypeVariable v = (TypeVariable)arg;
                    arg = typeVarMap.get((Object)v.getName());
                }
                typeVarMap.put((String)var.getName(), (Type)arg);
            }
            if (TypeToken.typeEquals((ParameterizedType)ptype, (ParameterizedType)to, typeVarMap)) {
                return true;
            }
        }
        tArgs = clazz.getGenericInterfaces();
        int tParams = tArgs.length;
        i = 0;
        do {
            if (i >= tParams) {
                Type sType = clazz.getGenericSuperclass();
                return TypeToken.isAssignableFrom((Type)sType, (ParameterizedType)to, new HashMap<String, Type>(typeVarMap));
            }
            Type itype = tArgs[i];
            if (TypeToken.isAssignableFrom((Type)itype, (ParameterizedType)to, new HashMap<String, Type>(typeVarMap))) {
                return true;
            }
            ++i;
        } while (true);
    }

    private static boolean typeEquals(ParameterizedType from, ParameterizedType to, Map<String, Type> typeVarMap) {
        if (!from.getRawType().equals((Object)to.getRawType())) return false;
        Type[] fromArgs = from.getActualTypeArguments();
        Type[] toArgs = to.getActualTypeArguments();
        int i = 0;
        while (i < fromArgs.length) {
            if (!TypeToken.matches((Type)fromArgs[i], (Type)toArgs[i], typeVarMap)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private static AssertionError buildUnexpectedTypeError(Type token, Class<?> ... expected) {
        StringBuilder exceptionMessage = new StringBuilder((String)"Unexpected type. Expected one of: ");
        Class<?>[] arrclass = expected;
        int n = arrclass.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                exceptionMessage.append((String)"but got: ").append((String)token.getClass().getName()).append((String)", for type token: ").append((String)token.toString()).append((char)'.');
                return new AssertionError((Object)exceptionMessage.toString());
            }
            Class<?> clazz = arrclass[n2];
            exceptionMessage.append((String)clazz.getName()).append((String)", ");
            ++n2;
        } while (true);
    }

    private static boolean matches(Type from, Type to, Map<String, Type> typeMap) {
        if (to.equals((Object)from)) return true;
        if (!(from instanceof TypeVariable)) return false;
        if (!to.equals((Object)typeMap.get((Object)((TypeVariable)from).getName()))) return false;
        return true;
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public final boolean equals(Object o) {
        if (!(o instanceof TypeToken)) return false;
        if (!$Gson$Types.equals((Type)this.type, (Type)((TypeToken)o).type)) return false;
        return true;
    }

    public final String toString() {
        return $Gson$Types.typeToString((Type)this.type);
    }

    public static TypeToken<?> get(Type type) {
        return new TypeToken<T>((Type)type);
    }

    public static <T> TypeToken<T> get(Class<T> type) {
        return new TypeToken<T>(type);
    }

    public static TypeToken<?> getParameterized(Type rawType, Type ... typeArguments) {
        return new TypeToken<T>((Type)$Gson$Types.newParameterizedTypeWithOwner(null, (Type)rawType, (Type[])typeArguments));
    }

    public static TypeToken<?> getArray(Type componentType) {
        return new TypeToken<T>((Type)$Gson$Types.arrayOf((Type)componentType));
    }
}

