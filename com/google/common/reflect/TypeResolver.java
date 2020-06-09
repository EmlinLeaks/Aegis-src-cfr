/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeResolver;
import com.google.common.reflect.TypeVisitor;
import com.google.common.reflect.Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;

@Beta
public final class TypeResolver {
    private final TypeTable typeTable;

    public TypeResolver() {
        this.typeTable = new TypeTable();
    }

    private TypeResolver(TypeTable typeTable) {
        this.typeTable = typeTable;
    }

    static TypeResolver accordingTo(Type type) {
        return new TypeResolver().where(TypeMappingIntrospector.getTypeMappings((Type)type));
    }

    public TypeResolver where(Type formal, Type actual) {
        HashMap<TypeVariableKey, Type> mappings = Maps.newHashMap();
        TypeResolver.populateTypeMappings(mappings, (Type)Preconditions.checkNotNull(formal), (Type)Preconditions.checkNotNull(actual));
        return this.where(mappings);
    }

    TypeResolver where(Map<TypeVariableKey, ? extends Type> mappings) {
        return new TypeResolver((TypeTable)this.typeTable.where(mappings));
    }

    private static void populateTypeMappings(Map<TypeVariableKey, Type> mappings, Type from, Type to) {
        if (from.equals((Object)to)) {
            return;
        }
        new TypeVisitor(mappings, (Type)to){
            final /* synthetic */ Map val$mappings;
            final /* synthetic */ Type val$to;
            {
                this.val$mappings = map;
                this.val$to = type;
            }

            void visitTypeVariable(TypeVariable<?> typeVariable) {
                this.val$mappings.put(new TypeVariableKey(typeVariable), this.val$to);
            }

            void visitWildcardType(WildcardType fromWildcardType) {
                int i;
                if (!(this.val$to instanceof WildcardType)) {
                    return;
                }
                WildcardType toWildcardType = (WildcardType)this.val$to;
                Type[] fromUpperBounds = fromWildcardType.getUpperBounds();
                Type[] toUpperBounds = toWildcardType.getUpperBounds();
                Type[] fromLowerBounds = fromWildcardType.getLowerBounds();
                Type[] toLowerBounds = toWildcardType.getLowerBounds();
                Preconditions.checkArgument((boolean)(fromUpperBounds.length == toUpperBounds.length && fromLowerBounds.length == toLowerBounds.length), (String)"Incompatible type: %s vs. %s", (Object)fromWildcardType, (Object)this.val$to);
                for (i = 0; i < fromUpperBounds.length; ++i) {
                    TypeResolver.access$000((Map)this.val$mappings, (Type)fromUpperBounds[i], (Type)toUpperBounds[i]);
                }
                i = 0;
                while (i < fromLowerBounds.length) {
                    TypeResolver.access$000((Map)this.val$mappings, (Type)fromLowerBounds[i], (Type)toLowerBounds[i]);
                    ++i;
                }
            }

            void visitParameterizedType(ParameterizedType fromParameterizedType) {
                if (this.val$to instanceof WildcardType) {
                    return;
                }
                ParameterizedType toParameterizedType = (ParameterizedType)TypeResolver.access$100(ParameterizedType.class, (Object)this.val$to);
                if (fromParameterizedType.getOwnerType() != null && toParameterizedType.getOwnerType() != null) {
                    TypeResolver.access$000((Map)this.val$mappings, (Type)fromParameterizedType.getOwnerType(), (Type)toParameterizedType.getOwnerType());
                }
                Preconditions.checkArgument((boolean)fromParameterizedType.getRawType().equals((Object)toParameterizedType.getRawType()), (String)"Inconsistent raw type: %s vs. %s", (Object)fromParameterizedType, (Object)this.val$to);
                Type[] fromArgs = fromParameterizedType.getActualTypeArguments();
                Type[] toArgs = toParameterizedType.getActualTypeArguments();
                Preconditions.checkArgument((boolean)(fromArgs.length == toArgs.length), (String)"%s not compatible with %s", (Object)fromParameterizedType, (Object)toParameterizedType);
                int i = 0;
                while (i < fromArgs.length) {
                    TypeResolver.access$000((Map)this.val$mappings, (Type)fromArgs[i], (Type)toArgs[i]);
                    ++i;
                }
            }

            void visitGenericArrayType(GenericArrayType fromArrayType) {
                if (this.val$to instanceof WildcardType) {
                    return;
                }
                Type componentType = Types.getComponentType((Type)this.val$to);
                Preconditions.checkArgument((boolean)(componentType != null), (String)"%s is not an array type.", (Object)this.val$to);
                TypeResolver.access$000((Map)this.val$mappings, (Type)fromArrayType.getGenericComponentType(), (Type)componentType);
            }

            void visitClass(Class<?> fromClass) {
                if (!(this.val$to instanceof WildcardType)) throw new IllegalArgumentException((String)("No type mapping from " + fromClass + " to " + this.val$to));
            }
        }.visit((Type[])new Type[]{from});
    }

    public Type resolveType(Type type) {
        Preconditions.checkNotNull(type);
        if (type instanceof TypeVariable) {
            return this.typeTable.resolve((TypeVariable)type);
        }
        if (type instanceof ParameterizedType) {
            return this.resolveParameterizedType((ParameterizedType)((ParameterizedType)type));
        }
        if (type instanceof GenericArrayType) {
            return this.resolveGenericArrayType((GenericArrayType)((GenericArrayType)type));
        }
        if (!(type instanceof WildcardType)) return type;
        return this.resolveWildcardType((WildcardType)((WildcardType)type));
    }

    private Type[] resolveTypes(Type[] types) {
        Type[] result = new Type[types.length];
        int i = 0;
        while (i < types.length) {
            result[i] = this.resolveType((Type)types[i]);
            ++i;
        }
        return result;
    }

    private WildcardType resolveWildcardType(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        Type[] upperBounds = type.getUpperBounds();
        return new Types.WildcardTypeImpl((Type[])this.resolveTypes((Type[])lowerBounds), (Type[])this.resolveTypes((Type[])upperBounds));
    }

    private Type resolveGenericArrayType(GenericArrayType type) {
        Type componentType = type.getGenericComponentType();
        Type resolvedComponentType = this.resolveType((Type)componentType);
        return Types.newArrayType((Type)resolvedComponentType);
    }

    private ParameterizedType resolveParameterizedType(ParameterizedType type) {
        Type owner = type.getOwnerType();
        Type resolvedOwner = owner == null ? null : this.resolveType((Type)owner);
        Type resolvedRawType = this.resolveType((Type)type.getRawType());
        Type[] args = type.getActualTypeArguments();
        Type[] resolvedArgs = this.resolveTypes((Type[])args);
        return Types.newParameterizedTypeWithOwner((Type)resolvedOwner, (Class)resolvedRawType, (Type[])resolvedArgs);
    }

    private static <T> T expectArgument(Class<T> type, Object arg) {
        try {
            return (T)type.cast((Object)arg);
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException((String)(arg + " is not a " + type.getSimpleName()));
        }
    }

    static /* synthetic */ void access$000(Map x0, Type x1, Type x2) {
        TypeResolver.populateTypeMappings((Map<TypeVariableKey, Type>)x0, (Type)x1, (Type)x2);
    }

    static /* synthetic */ Object access$100(Class x0, Object x1) {
        return TypeResolver.expectArgument(x0, (Object)x1);
    }

    static /* synthetic */ Type[] access$300(TypeResolver x0, Type[] x1) {
        return x0.resolveTypes((Type[])x1);
    }
}

