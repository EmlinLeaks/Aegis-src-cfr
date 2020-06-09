/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeCapture;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeResolver;
import com.google.common.reflect.TypeToken;
import com.google.common.reflect.TypeVisitor;
import com.google.common.reflect.Types;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
public abstract class TypeToken<T>
extends TypeCapture<T>
implements Serializable {
    private final Type runtimeType;
    private transient TypeResolver typeResolver;

    protected TypeToken() {
        this.runtimeType = this.capture();
        Preconditions.checkState((boolean)(!(this.runtimeType instanceof TypeVariable)), (String)"Cannot construct a TypeToken for a type variable.\nYou probably meant to call new TypeToken<%s>(getClass()) that can resolve the type variable for you.\nIf you do need to create a TypeToken of a type variable, please use TypeToken.of() instead.", (Object)this.runtimeType);
    }

    protected TypeToken(Class<?> declaringClass) {
        Type captured = super.capture();
        if (captured instanceof Class) {
            this.runtimeType = captured;
            return;
        }
        this.runtimeType = TypeToken.of(declaringClass).resolveType((Type)captured).runtimeType;
    }

    private TypeToken(Type type) {
        this.runtimeType = Preconditions.checkNotNull(type);
    }

    public static <T> TypeToken<T> of(Class<T> type) {
        return new SimpleTypeToken<T>(type);
    }

    public static TypeToken<?> of(Type type) {
        return new SimpleTypeToken<T>((Type)type);
    }

    public final Class<? super T> getRawType() {
        Class rawType = (Class)this.getRawTypes().iterator().next();
        return rawType;
    }

    public final Type getType() {
        return this.runtimeType;
    }

    public final <X> TypeToken<T> where(TypeParameter<X> typeParam, TypeToken<X> typeArg) {
        TypeResolver resolver = new TypeResolver().where(ImmutableMap.of(new TypeResolver.TypeVariableKey(typeParam.typeVariable), typeArg.runtimeType));
        return new SimpleTypeToken<T>((Type)resolver.resolveType((Type)this.runtimeType));
    }

    public final <X> TypeToken<T> where(TypeParameter<X> typeParam, Class<X> typeArg) {
        return this.where(typeParam, TypeToken.of(typeArg));
    }

    public final TypeToken<?> resolveType(Type type) {
        Preconditions.checkNotNull(type);
        TypeResolver resolver = this.typeResolver;
        if (resolver != null) return TypeToken.of((Type)resolver.resolveType((Type)type));
        resolver = this.typeResolver = TypeResolver.accordingTo((Type)this.runtimeType);
        return TypeToken.of((Type)resolver.resolveType((Type)type));
    }

    private Type[] resolveInPlace(Type[] types) {
        int i = 0;
        while (i < types.length) {
            types[i] = this.resolveType((Type)types[i]).getType();
            ++i;
        }
        return types;
    }

    private TypeToken<?> resolveSupertype(Type type) {
        TypeToken<?> supertype = this.resolveType((Type)type);
        supertype.typeResolver = this.typeResolver;
        return supertype;
    }

    @Nullable
    final TypeToken<? super T> getGenericSuperclass() {
        if (this.runtimeType instanceof TypeVariable) {
            return this.boundAsSuperclass((Type)((TypeVariable)this.runtimeType).getBounds()[0]);
        }
        if (this.runtimeType instanceof WildcardType) {
            return this.boundAsSuperclass((Type)((WildcardType)this.runtimeType).getUpperBounds()[0]);
        }
        Type superclass = this.getRawType().getGenericSuperclass();
        if (superclass != null) return this.resolveSupertype((Type)superclass);
        return null;
    }

    @Nullable
    private TypeToken<? super T> boundAsSuperclass(Type bound) {
        TypeToken<?> token = TypeToken.of((Type)bound);
        if (!token.getRawType().isInterface()) return token;
        return null;
    }

    final ImmutableList<TypeToken<? super T>> getGenericInterfaces() {
        if (this.runtimeType instanceof TypeVariable) {
            return this.boundsAsInterfaces((Type[])((TypeVariable)this.runtimeType).getBounds());
        }
        if (this.runtimeType instanceof WildcardType) {
            return this.boundsAsInterfaces((Type[])((WildcardType)this.runtimeType).getUpperBounds());
        }
        ImmutableList.Builder<E> builder = ImmutableList.builder();
        Type[] arr$ = this.getRawType().getGenericInterfaces();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type interfaceType = arr$[i$];
            TypeToken<?> resolvedInterface = this.resolveSupertype((Type)interfaceType);
            builder.add(resolvedInterface);
            ++i$;
        }
        return builder.build();
    }

    private ImmutableList<TypeToken<? super T>> boundsAsInterfaces(Type[] bounds) {
        ImmutableList.Builder<E> builder = ImmutableList.builder();
        Type[] arr$ = bounds;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type bound = arr$[i$];
            TypeToken<?> boundType = TypeToken.of((Type)bound);
            if (boundType.getRawType().isInterface()) {
                builder.add(boundType);
            }
            ++i$;
        }
        return builder.build();
    }

    public final TypeToken<T> getTypes() {
        return new TypeSet((TypeToken)this);
    }

    public final TypeToken<? super T> getSupertype(Class<? super T> superclass) {
        Preconditions.checkArgument((boolean)this.someRawTypeIsSubclassOf(superclass), (String)"%s is not a super class of %s", superclass, (Object)this);
        if (this.runtimeType instanceof TypeVariable) {
            return this.getSupertypeFromUpperBounds(superclass, (Type[])((TypeVariable)this.runtimeType).getBounds());
        }
        if (this.runtimeType instanceof WildcardType) {
            return this.getSupertypeFromUpperBounds(superclass, (Type[])((WildcardType)this.runtimeType).getUpperBounds());
        }
        if (!superclass.isArray()) return this.resolveSupertype((Type)TypeToken.toGenericType(superclass).runtimeType);
        return this.getArraySupertype(superclass);
    }

    public final TypeToken<? extends T> getSubtype(Class<?> subclass) {
        Preconditions.checkArgument((boolean)(!(this.runtimeType instanceof TypeVariable)), (String)"Cannot get subtype of type variable <%s>", (Object)this);
        if (this.runtimeType instanceof WildcardType) {
            return this.getSubtypeFromLowerBounds(subclass, (Type[])((WildcardType)this.runtimeType).getLowerBounds());
        }
        if (this.isArray()) {
            return this.getArraySubtype(subclass);
        }
        Preconditions.checkArgument((boolean)this.getRawType().isAssignableFrom(subclass), (String)"%s isn't a subclass of %s", subclass, (Object)this);
        Type resolvedTypeArgs = this.resolveTypeArgsForSubclass(subclass);
        return TypeToken.of((Type)resolvedTypeArgs);
    }

    public final boolean isSupertypeOf(TypeToken<?> type) {
        return type.isSubtypeOf((Type)this.getType());
    }

    public final boolean isSupertypeOf(Type type) {
        return TypeToken.of((Type)type).isSubtypeOf((Type)this.getType());
    }

    public final boolean isSubtypeOf(TypeToken<?> type) {
        return this.isSubtypeOf((Type)type.getType());
    }

    public final boolean isSubtypeOf(Type supertype) {
        Preconditions.checkNotNull(supertype);
        if (supertype instanceof WildcardType) {
            return TypeToken.any((Type[])((WildcardType)supertype).getLowerBounds()).isSupertypeOf((Type)this.runtimeType);
        }
        if (this.runtimeType instanceof WildcardType) {
            return TypeToken.any((Type[])((WildcardType)this.runtimeType).getUpperBounds()).isSubtypeOf((Type)supertype);
        }
        if (this.runtimeType instanceof TypeVariable) {
            if (this.runtimeType.equals((Object)supertype)) return true;
            if (TypeToken.any((Type[])((TypeVariable)this.runtimeType).getBounds()).isSubtypeOf((Type)supertype)) return true;
            return false;
        }
        if (this.runtimeType instanceof GenericArrayType) {
            return TypeToken.super.isSupertypeOfArray((GenericArrayType)((GenericArrayType)this.runtimeType));
        }
        if (supertype instanceof Class) {
            return this.someRawTypeIsSubclassOf((Class)supertype);
        }
        if (supertype instanceof ParameterizedType) {
            return this.isSubtypeOfParameterizedType((ParameterizedType)((ParameterizedType)supertype));
        }
        if (!(supertype instanceof GenericArrayType)) return false;
        return this.isSubtypeOfArrayType((GenericArrayType)((GenericArrayType)supertype));
    }

    public final boolean isArray() {
        if (this.getComponentType() == null) return false;
        return true;
    }

    public final boolean isPrimitive() {
        if (!(this.runtimeType instanceof Class)) return false;
        if (!((Class)this.runtimeType).isPrimitive()) return false;
        return true;
    }

    public final TypeToken<T> wrap() {
        if (!this.isPrimitive()) return this;
        Class type = (Class)this.runtimeType;
        return TypeToken.of(Primitives.wrap(type));
    }

    private boolean isWrapper() {
        return Primitives.allWrapperTypes().contains((Object)this.runtimeType);
    }

    public final TypeToken<T> unwrap() {
        if (!this.isWrapper()) return this;
        Class type = (Class)this.runtimeType;
        return TypeToken.of(Primitives.unwrap(type));
    }

    @Nullable
    public final TypeToken<?> getComponentType() {
        Type componentType = Types.getComponentType((Type)this.runtimeType);
        if (componentType != null) return TypeToken.of((Type)componentType);
        return null;
    }

    public final Invokable<T, Object> method(Method method) {
        Preconditions.checkArgument((boolean)this.someRawTypeIsSubclassOf(method.getDeclaringClass()), (String)"%s not declared by %s", (Object)method, (Object)this);
        return new Invokable.MethodInvokable<T>((TypeToken)this, (Method)method){
            final /* synthetic */ TypeToken this$0;
            {
                this.this$0 = typeToken;
                super((Method)x0);
            }

            Type getGenericReturnType() {
                return this.this$0.resolveType((Type)super.getGenericReturnType()).getType();
            }

            Type[] getGenericParameterTypes() {
                return TypeToken.access$000((TypeToken)this.this$0, (Type[])super.getGenericParameterTypes());
            }

            Type[] getGenericExceptionTypes() {
                return TypeToken.access$000((TypeToken)this.this$0, (Type[])super.getGenericExceptionTypes());
            }

            public TypeToken<T> getOwnerType() {
                return this.this$0;
            }

            public String toString() {
                return this.getOwnerType() + "." + super.toString();
            }
        };
    }

    public final Invokable<T, T> constructor(Constructor<?> constructor) {
        Preconditions.checkArgument((boolean)(constructor.getDeclaringClass() == this.getRawType()), (String)"%s not declared by %s", constructor, this.getRawType());
        return new Invokable.ConstructorInvokable<T>((TypeToken)this, constructor){
            final /* synthetic */ TypeToken this$0;
            {
                this.this$0 = typeToken;
                super(x0);
            }

            Type getGenericReturnType() {
                return this.this$0.resolveType((Type)super.getGenericReturnType()).getType();
            }

            Type[] getGenericParameterTypes() {
                return TypeToken.access$000((TypeToken)this.this$0, (Type[])super.getGenericParameterTypes());
            }

            Type[] getGenericExceptionTypes() {
                return TypeToken.access$000((TypeToken)this.this$0, (Type[])super.getGenericExceptionTypes());
            }

            public TypeToken<T> getOwnerType() {
                return this.this$0;
            }

            public String toString() {
                return this.getOwnerType() + "(" + com.google.common.base.Joiner.on((String)", ").join((Object[])this.getGenericParameterTypes()) + ")";
            }
        };
    }

    public boolean equals(@Nullable Object o) {
        if (!(o instanceof TypeToken)) return false;
        TypeToken that = (TypeToken)o;
        return this.runtimeType.equals((Object)that.runtimeType);
    }

    public int hashCode() {
        return this.runtimeType.hashCode();
    }

    public String toString() {
        return Types.toString((Type)this.runtimeType);
    }

    protected Object writeReplace() {
        return TypeToken.of((Type)new TypeResolver().resolveType((Type)this.runtimeType));
    }

    @CanIgnoreReturnValue
    final TypeToken<T> rejectTypeVariables() {
        new TypeVisitor((TypeToken)this){
            final /* synthetic */ TypeToken this$0;
            {
                this.this$0 = typeToken;
            }

            void visitTypeVariable(TypeVariable<?> type) {
                throw new IllegalArgumentException((String)(TypeToken.access$400((TypeToken)this.this$0) + "contains a type variable and is not safe for the operation"));
            }

            void visitWildcardType(WildcardType type) {
                this.visit((Type[])type.getLowerBounds());
                this.visit((Type[])type.getUpperBounds());
            }

            void visitParameterizedType(ParameterizedType type) {
                this.visit((Type[])type.getActualTypeArguments());
                this.visit((Type[])new Type[]{type.getOwnerType()});
            }

            void visitGenericArrayType(GenericArrayType type) {
                this.visit((Type[])new Type[]{type.getGenericComponentType()});
            }
        }.visit((Type[])new Type[]{this.runtimeType});
        return this;
    }

    private boolean someRawTypeIsSubclassOf(Class<?> superclass) {
        Class rawType;
        Iterator i$ = this.getRawTypes().iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!superclass.isAssignableFrom(rawType = (Class)i$.next()));
        return true;
    }

    private boolean isSubtypeOfParameterizedType(ParameterizedType supertype) {
        Class<?> matchedClass = TypeToken.of((Type)supertype).getRawType();
        if (!this.someRawTypeIsSubclassOf(matchedClass)) {
            return false;
        }
        TypeVariable<Class<?>>[] typeParams = matchedClass.getTypeParameters();
        Type[] toTypeArgs = supertype.getActualTypeArguments();
        for (int i = 0; i < typeParams.length; ++i) {
            if (TypeToken.super.is((Type)toTypeArgs[i])) continue;
            return false;
        }
        if (Modifier.isStatic((int)((Class)supertype.getRawType()).getModifiers())) return true;
        if (supertype.getOwnerType() == null) return true;
        if (this.isOwnedBySubtypeOf((Type)supertype.getOwnerType())) return true;
        return false;
    }

    private boolean isSubtypeOfArrayType(GenericArrayType supertype) {
        if (this.runtimeType instanceof Class) {
            Class fromClass = (Class)this.runtimeType;
            if (fromClass.isArray()) return TypeToken.of(fromClass.getComponentType()).isSubtypeOf((Type)supertype.getGenericComponentType());
            return false;
        }
        if (!(this.runtimeType instanceof GenericArrayType)) return false;
        GenericArrayType fromArrayType = (GenericArrayType)this.runtimeType;
        return TypeToken.of((Type)fromArrayType.getGenericComponentType()).isSubtypeOf((Type)supertype.getGenericComponentType());
    }

    private boolean isSupertypeOfArray(GenericArrayType subtype) {
        if (this.runtimeType instanceof Class) {
            Class thisClass = (Class)this.runtimeType;
            if (thisClass.isArray()) return TypeToken.of((Type)subtype.getGenericComponentType()).isSubtypeOf(thisClass.getComponentType());
            return thisClass.isAssignableFrom(Object[].class);
        }
        if (!(this.runtimeType instanceof GenericArrayType)) return false;
        return TypeToken.of((Type)subtype.getGenericComponentType()).isSubtypeOf((Type)((GenericArrayType)this.runtimeType).getGenericComponentType());
    }

    private boolean is(Type formalType) {
        if (this.runtimeType.equals((Object)formalType)) {
            return true;
        }
        if (!(formalType instanceof WildcardType)) return false;
        if (!TypeToken.every((Type[])((WildcardType)formalType).getUpperBounds()).isSupertypeOf((Type)this.runtimeType)) return false;
        if (!TypeToken.every((Type[])((WildcardType)formalType).getLowerBounds()).isSubtypeOf((Type)this.runtimeType)) return false;
        return true;
    }

    private static Bounds every(Type[] bounds) {
        return new Bounds((Type[])bounds, (boolean)false);
    }

    private static Bounds any(Type[] bounds) {
        return new Bounds((Type[])bounds, (boolean)true);
    }

    private ImmutableSet<Class<? super T>> getRawTypes() {
        ImmutableSet.Builder<E> builder = ImmutableSet.builder();
        new TypeVisitor((TypeToken)this, builder){
            final /* synthetic */ ImmutableSet.Builder val$builder;
            final /* synthetic */ TypeToken this$0;
            {
                this.this$0 = typeToken;
                this.val$builder = builder;
            }

            void visitTypeVariable(TypeVariable<?> t) {
                this.visit((Type[])t.getBounds());
            }

            void visitWildcardType(WildcardType t) {
                this.visit((Type[])t.getUpperBounds());
            }

            void visitParameterizedType(ParameterizedType t) {
                this.val$builder.add((Object)((Class)t.getRawType()));
            }

            void visitClass(Class<?> t) {
                this.val$builder.add(t);
            }

            void visitGenericArrayType(GenericArrayType t) {
                this.val$builder.add(Types.getArrayClass(TypeToken.of((Type)t.getGenericComponentType()).getRawType()));
            }
        }.visit((Type[])new Type[]{this.runtimeType});
        return builder.build();
    }

    private boolean isOwnedBySubtypeOf(Type supertype) {
        TypeToken type;
        Type ownerType;
        Iterator<E> i$ = this.getTypes().iterator();
        do {
            if (!i$.hasNext()) return false;
        } while ((ownerType = (type = (TypeToken)i$.next()).getOwnerTypeIfPresent()) == null || !TypeToken.of((Type)ownerType).isSubtypeOf((Type)supertype));
        return true;
    }

    @Nullable
    private Type getOwnerTypeIfPresent() {
        if (this.runtimeType instanceof ParameterizedType) {
            return ((ParameterizedType)this.runtimeType).getOwnerType();
        }
        if (!(this.runtimeType instanceof Class)) return null;
        return ((Class)this.runtimeType).getEnclosingClass();
    }

    @VisibleForTesting
    static <T> TypeToken<? extends T> toGenericType(Class<T> cls) {
        if (cls.isArray()) {
            Type arrayOfGenericType = Types.newArrayType((Type)TypeToken.toGenericType(cls.getComponentType()).runtimeType);
            return TypeToken.of((Type)arrayOfGenericType);
        }
        Type[] typeParams = cls.getTypeParameters();
        Type ownerType = cls.isMemberClass() && !Modifier.isStatic((int)cls.getModifiers()) ? TypeToken.toGenericType(cls.getEnclosingClass()).runtimeType : null;
        if (typeParams.length > 0) return TypeToken.of((Type)Types.newParameterizedTypeWithOwner((Type)ownerType, cls, (Type[])typeParams));
        if (ownerType == null) return TypeToken.of(cls);
        if (ownerType == cls.getEnclosingClass()) return TypeToken.of(cls);
        return TypeToken.of((Type)Types.newParameterizedTypeWithOwner((Type)ownerType, cls, (Type[])typeParams));
    }

    private TypeToken<? super T> getSupertypeFromUpperBounds(Class<? super T> supertype, Type[] upperBounds) {
        Type[] arr$ = upperBounds;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type upperBound = arr$[i$];
            TypeToken<?> bound = TypeToken.of((Type)upperBound);
            if (bound.isSubtypeOf(supertype)) {
                return bound.getSupertype(supertype);
            }
            ++i$;
        }
        throw new IllegalArgumentException((String)(supertype + " isn't a super type of " + this));
    }

    private TypeToken<? extends T> getSubtypeFromLowerBounds(Class<?> subclass, Type[] lowerBounds) {
        int i$ = 0;
        Type[] arr$ = lowerBounds;
        int len$ = arr$.length;
        if (i$ >= len$) throw new IllegalArgumentException((String)(subclass + " isn't a subclass of " + this));
        Type lowerBound = arr$[i$];
        TypeToken<?> bound = TypeToken.of((Type)lowerBound);
        return bound.getSubtype(subclass);
    }

    private TypeToken<? super T> getArraySupertype(Class<? super T> supertype) {
        TypeToken<?> componentType = Preconditions.checkNotNull(this.getComponentType(), (String)"%s isn't a super type of %s", supertype, (Object)this);
        TypeToken<?> componentSupertype = componentType.getSupertype(supertype.getComponentType());
        return TypeToken.of((Type)TypeToken.newArrayClassOrGenericArrayType((Type)componentSupertype.runtimeType));
    }

    private TypeToken<? extends T> getArraySubtype(Class<?> subclass) {
        TypeToken<?> componentSubtype = this.getComponentType().getSubtype(subclass.getComponentType());
        return TypeToken.of((Type)TypeToken.newArrayClassOrGenericArrayType((Type)componentSubtype.runtimeType));
    }

    private Type resolveTypeArgsForSubclass(Class<?> subclass) {
        if (this.runtimeType instanceof Class) {
            if (subclass.getTypeParameters().length == 0) return subclass;
            if (this.getRawType().getTypeParameters().length != 0) {
                return subclass;
            }
        }
        TypeToken<?> genericSubtype = TypeToken.toGenericType(subclass);
        Type supertypeWithArgsFromSubtype = genericSubtype.getSupertype(this.getRawType()).runtimeType;
        return new TypeResolver().where((Type)supertypeWithArgsFromSubtype, (Type)this.runtimeType).resolveType((Type)genericSubtype.runtimeType);
    }

    private static Type newArrayClassOrGenericArrayType(Type componentType) {
        return Types.JavaVersion.JAVA7.newArrayType((Type)componentType);
    }

    static /* synthetic */ Type[] access$000(TypeToken x0, Type[] x1) {
        return x0.resolveInPlace((Type[])x1);
    }

    static /* synthetic */ ImmutableSet access$200(TypeToken x0) {
        return x0.getRawTypes();
    }

    static /* synthetic */ Type access$400(TypeToken x0) {
        return x0.runtimeType;
    }
}

