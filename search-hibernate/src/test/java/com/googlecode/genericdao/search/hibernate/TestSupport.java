package com.googlecode.genericdao.search.hibernate;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

final class TestSupport {

    private TestSupport() {
    }

    static SessionFactoryImplementor sessionFactory(final EntityPersister persister) {
        final MetamodelImplementor metamodel = metamodel(persister);
        return (SessionFactoryImplementor) Proxy.newProxyInstance(
                TestSupport.class.getClassLoader(),
                new Class<?>[]{SessionFactoryImplementor.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getMetamodel".equals(method.getName())) {
                            return metamodel;
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    static EntityPersister entityPersister(final EntityPersisterConfig config) {
        return (EntityPersister) Proxy.newProxyInstance(
                TestSupport.class.getClassLoader(),
                new Class<?>[]{EntityPersister.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        String name = method.getName();
                        if ("getEntityName".equals(name)) {
                            return config.entityName;
                        }
                        if ("getIdentifierPropertyName".equals(name)) {
                            return config.idProperty;
                        }
                        if ("getIdentifierType".equals(name)) {
                            return config.idType;
                        }
                        if ("getIdentifier".equals(name)) {
                            return config.idValue;
                        }
                        if ("getMappedClass".equals(name)) {
                            return config.mappedClass;
                        }
                        if ("getPropertyNames".equals(name)) {
                            return config.propertyNames;
                        }
                        if ("getPropertyType".equals(name)) {
                            return config.propertyType;
                        }
                        if ("getPropertyValue".equals(name)) {
                            return config.propertyValue;
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    static Type simpleType(final TypeConfig config) {
        return (Type) Proxy.newProxyInstance(
                TestSupport.class.getClassLoader(),
                new Class<?>[]{Type.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        String name = method.getName();
                        if ("getReturnedClass".equals(name)) {
                            return config.returnedClass;
                        }
                        if ("isEntityType".equals(name)) {
                            return config.entityType;
                        }
                        if ("isCollectionType".equals(name)) {
                            return config.collectionType;
                        }
                        if ("isComponentType".equals(name)) {
                            return config.componentType;
                        }
                        if ("sqlTypes".equals(name)) {
                            return config.sqlTypes;
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private static MetamodelImplementor metamodel(final EntityPersister persister) {
        return (MetamodelImplementor) Proxy.newProxyInstance(
                TestSupport.class.getClassLoader(),
                new Class<?>[]{MetamodelImplementor.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        String name = method.getName();
                        if ("entityPersister".equals(name) || "locateEntityPersister".equals(name)) {
                            return persister;
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive())
            return null;
        if (returnType == boolean.class)
            return false;
        if (returnType == byte.class)
            return (byte) 0;
        if (returnType == short.class)
            return (short) 0;
        if (returnType == int.class)
            return 0;
        if (returnType == long.class)
            return 0L;
        if (returnType == float.class)
            return 0f;
        if (returnType == double.class)
            return 0d;
        if (returnType == char.class)
            return '\0';
        return null;
    }

    static final class EntityPersisterConfig {
        private final String entityName;
        private final String idProperty;
        private final Type idType;
        private final Serializable idValue;
        private final Class<?> mappedClass;
        private final String[] propertyNames;
        private final Type propertyType;
        private final Object propertyValue;

        private EntityPersisterConfig(String entityName, String idProperty, Type idType, Serializable idValue,
                                      Class<?> mappedClass, String[] propertyNames, Type propertyType, Object propertyValue) {
            this.entityName = entityName;
            this.idProperty = idProperty;
            this.idType = idType;
            this.idValue = idValue;
            this.mappedClass = mappedClass;
            this.propertyNames = propertyNames;
            this.propertyType = propertyType;
            this.propertyValue = propertyValue;
        }

        static EntityPersisterConfig of(String entityName, String idProperty, Type idType, Serializable idValue,
                                        Class<?> mappedClass, String[] propertyNames, Type propertyType, Object propertyValue) {
            return new EntityPersisterConfig(entityName, idProperty, idType, idValue, mappedClass, propertyNames,
                    propertyType, propertyValue);
        }
    }

    static final class TypeConfig {
        private final Class<?> returnedClass;
        private final boolean entityType;
        private final boolean collectionType;
        private final boolean componentType;
        private final int[] sqlTypes;

        private TypeConfig(Class<?> returnedClass, boolean entityType, boolean collectionType, boolean componentType,
                           int[] sqlTypes) {
            this.returnedClass = returnedClass;
            this.entityType = entityType;
            this.collectionType = collectionType;
            this.componentType = componentType;
            this.sqlTypes = sqlTypes;
        }

        static TypeConfig of(Class<?> returnedClass, boolean entityType, boolean collectionType, boolean componentType,
                             int[] sqlTypes) {
            return new TypeConfig(returnedClass, entityType, collectionType, componentType, sqlTypes);
        }
    }
}
