/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.reflect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeVisitor;
import com.google.common.reflect.Types;
import java.lang.reflect.Array;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

final class Types {
    private static final Function<Type, String> TYPE_NAME = new Function<Type, String>(){

        public String apply(Type from) {
            return JavaVersion.CURRENT.typeName((Type)from);
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on((String)", ").useForNull((String)"null");

    static Type newArrayType(Type componentType) {
        if (!(componentType instanceof WildcardType)) return JavaVersion.CURRENT.newArrayType((Type)componentType);
        WildcardType wildcard = (WildcardType)componentType;
        Type[] lowerBounds = wildcard.getLowerBounds();
        Preconditions.checkArgument((boolean)(lowerBounds.length <= 1), (Object)"Wildcard cannot have more than one lower bounds.");
        if (lowerBounds.length == 1) {
            return Types.supertypeOf((Type)Types.newArrayType((Type)lowerBounds[0]));
        }
        Type[] upperBounds = wildcard.getUpperBounds();
        Preconditions.checkArgument((boolean)(upperBounds.length == 1), (Object)"Wildcard should have only one upper bound.");
        return Types.subtypeOf((Type)Types.newArrayType((Type)upperBounds[0]));
    }

    static ParameterizedType newParameterizedTypeWithOwner(@Nullable Type ownerType, Class<?> rawType, Type ... arguments) {
        if (ownerType == null) {
            return Types.newParameterizedType(rawType, (Type[])arguments);
        }
        Preconditions.checkNotNull(arguments);
        Preconditions.checkArgument((boolean)(rawType.getEnclosingClass() != null), (String)"Owner type for unenclosed %s", rawType);
        return new ParameterizedTypeImpl((Type)ownerType, rawType, (Type[])arguments);
    }

    static ParameterizedType newParameterizedType(Class<?> rawType, Type ... arguments) {
        return new ParameterizedTypeImpl(ClassOwnership.JVM_BEHAVIOR.getOwnerType(rawType), rawType, (Type[])arguments);
    }

    static <D extends GenericDeclaration> TypeVariable<D> newArtificialTypeVariable(D declaration, String name, Type ... bounds) {
        Type[] arrtype;
        if (bounds.length == 0) {
            Type[] arrtype2 = new Type[1];
            arrtype = arrtype2;
            arrtype2[0] = Object.class;
            return Types.newTypeVariableImpl(declaration, (String)name, (Type[])arrtype);
        }
        arrtype = bounds;
        return Types.newTypeVariableImpl(declaration, (String)name, (Type[])arrtype);
    }

    @VisibleForTesting
    static WildcardType subtypeOf(Type upperBound) {
        return new WildcardTypeImpl((Type[])new Type[0], (Type[])new Type[]{upperBound});
    }

    @VisibleForTesting
    static WildcardType supertypeOf(Type lowerBound) {
        return new WildcardTypeImpl((Type[])new Type[]{lowerBound}, (Type[])new Type[]{Object.class});
    }

    static String toString(Type type) {
        String string;
        if (type instanceof Class) {
            string = ((Class)type).getName();
            return string;
        }
        string = type.toString();
        return string;
    }

    @Nullable
    static Type getComponentType(Type type) {
        Preconditions.checkNotNull(type);
        AtomicReference<V> result = new AtomicReference<V>();
        new TypeVisitor(result){
            final /* synthetic */ AtomicReference val$result;
            {
                this.val$result = atomicReference;
            }

            void visitTypeVariable(TypeVariable<?> t) {
                this.val$result.set(Types.access$100((Type[])t.getBounds()));
            }

            void visitWildcardType(WildcardType t) {
                this.val$result.set(Types.access$100((Type[])t.getUpperBounds()));
            }

            void visitGenericArrayType(java.lang.reflect.GenericArrayType t) {
                this.val$result.set(t.getGenericComponentType());
            }

            void visitClass(Class<?> t) {
                this.val$result.set(t.getComponentType());
            }
        }.visit((Type[])new Type[]{type});
        return (Type)result.get();
    }

    @Nullable
    private static Type subtypeOfComponentType(Type[] bounds) {
        Type[] arr$ = bounds;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type bound = arr$[i$];
            Type componentType = Types.getComponentType((Type)bound);
            if (componentType != null) {
                if (!(componentType instanceof Class)) return Types.subtypeOf((Type)componentType);
                Class componentClass = (Class)componentType;
                if (!componentClass.isPrimitive()) return Types.subtypeOf((Type)componentType);
                return componentClass;
            }
            ++i$;
        }
        return null;
    }

    private static <D extends GenericDeclaration> TypeVariable<D> newTypeVariableImpl(D genericDeclaration, String name, Type[] bounds) {
        TypeVariableImpl<D> typeVariableImpl = new TypeVariableImpl<D>(genericDeclaration, (String)name, (Type[])bounds);
        return Reflection.newProxy(TypeVariable.class, (InvocationHandler)new TypeVariableInvocationHandler(typeVariableImpl));
    }

    private static Type[] toArray(Collection<Type> types) {
        return types.toArray(new Type[types.size()]);
    }

    private static Iterable<Type> filterUpperBounds(Iterable<Type> bounds) {
        return Iterables.filter(bounds, Predicates.not(Predicates.equalTo(Object.class)));
    }

    private static void disallowPrimitiveType(Type[] types, String usedAs) {
        Type[] arr$ = types;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type type = arr$[i$];
            if (type instanceof Class) {
                Class cls = (Class)type;
                Preconditions.checkArgument((boolean)(!cls.isPrimitive()), (String)"Primitive type '%s' used as %s", (Object)cls, (Object)usedAs);
            }
            ++i$;
        }
    }

    static Class<?> getArrayClass(Class<?> componentType) {
        return Array.newInstance(componentType, (int)0).getClass();
    }

    private Types() {
    }

    static /* synthetic */ Type access$100(Type[] x0) {
        return Types.subtypeOfComponentType((Type[])x0);
    }

    static /* synthetic */ void access$200(Type[] x0, String x1) {
        Types.disallowPrimitiveType((Type[])x0, (String)x1);
    }

    static /* synthetic */ Type[] access$300(Collection x0) {
        return Types.toArray((Collection<Type>)x0);
    }

    static /* synthetic */ Function access$400() {
        return TYPE_NAME;
    }

    static /* synthetic */ Joiner access$500() {
        return COMMA_JOINER;
    }

    static /* synthetic */ Iterable access$700(Iterable x0) {
        return Types.filterUpperBounds((Iterable<Type>)x0);
    }
}

