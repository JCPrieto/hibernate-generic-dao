package com.googlecode.genericdao.search.hibernate;

import com.googlecode.genericdao.search.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HibernateEntityMetadataTest {

    @Test
    public void getPropertiesIncludesIdFirst() {
        Type idType = TestSupport.simpleType(TestSupport.TypeConfig.of(Long.class, false, false, false, new int[]{1}));
        EntityPersister persister = TestSupport.entityPersister(TestSupport.EntityPersisterConfig.of(
                ExampleEntity.class.getName(), "id", idType, 11L, ExampleEntity.class,
                new String[]{"name", "age"}, null, null));
        SessionFactoryImplementor sessionFactory = TestSupport.sessionFactory(persister);

        HibernateEntityMetadata metadata = new HibernateEntityMetadata(sessionFactory, persister, null);
        String[] properties = metadata.getProperties();

        assertEquals(3, properties.length);
        assertEquals("id", properties[0]);
        assertEquals("name", properties[1]);
        assertEquals("age", properties[2]);
    }

    @Test
    public void getIdValueUsesPersisterIdentifier() {
        Serializable idValue = 42L;
        Type idType = TestSupport.simpleType(TestSupport.TypeConfig.of(Long.class, false, false, false, new int[]{1}));
        EntityPersister persister = TestSupport.entityPersister(TestSupport.EntityPersisterConfig.of(
                ExampleEntity.class.getName(), "id", idType, idValue, ExampleEntity.class,
                new String[0], null, null));
        SessionFactoryImplementor sessionFactory = TestSupport.sessionFactory(persister);

        HibernateEntityMetadata metadata = new HibernateEntityMetadata(sessionFactory, persister, null);

        assertEquals(idValue, metadata.getIdValue(new ExampleEntity()));
    }

    @Test
    public void getPropertyTypeReturnsNonEntityMetadataWhenTypeIsNotEntity() {
        Type propertyType = TestSupport.simpleType(
                TestSupport.TypeConfig.of(String.class, false, false, false, new int[]{12}));
        Type idType = TestSupport.simpleType(TestSupport.TypeConfig.of(Long.class, false, false, false, new int[]{1}));
        EntityPersister persister = TestSupport.entityPersister(TestSupport.EntityPersisterConfig.of(
                ExampleEntity.class.getName(), "id", idType, 5L, ExampleEntity.class,
                new String[]{"name"}, propertyType, "value"));
        SessionFactoryImplementor sessionFactory = TestSupport.sessionFactory(persister);

        HibernateEntityMetadata metadata = new HibernateEntityMetadata(sessionFactory, persister, null);
        Metadata type = metadata.getPropertyType("name");

        assertTrue(type instanceof HibernateNonEntityMetadata);
        assertEquals(String.class, type.getJavaClass());
    }

    private static final class ExampleEntity {
    }
}
