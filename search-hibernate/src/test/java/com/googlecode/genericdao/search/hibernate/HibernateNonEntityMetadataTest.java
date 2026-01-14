package com.googlecode.genericdao.search.hibernate;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;
import org.junit.Test;

import java.sql.Types;

import static org.junit.Assert.*;

public class HibernateNonEntityMetadataTest {

    @Test
    public void isNumericReturnsTrueForNumberTypes() {
        Type type = TestSupport.simpleType(TestSupport.TypeConfig.of(Integer.class, false, false, false,
                new int[]{Types.INTEGER}));
        SessionFactoryImplementor sessionFactory = TestSupport.sessionFactory(null);
        HibernateNonEntityMetadata metadata = new HibernateNonEntityMetadata(sessionFactory, type, null);

        assertTrue(metadata.isNumeric());
        assertFalse(metadata.isString());
    }

    @Test
    public void isStringReturnsTrueForVarcharSqlType() {
        Type type = TestSupport.simpleType(TestSupport.TypeConfig.of(String.class, false, false, false,
                new int[]{Types.VARCHAR}));
        SessionFactoryImplementor sessionFactory = TestSupport.sessionFactory(null);
        HibernateNonEntityMetadata metadata = new HibernateNonEntityMetadata(sessionFactory, type, null);

        assertTrue(metadata.isString());
    }

    @Test
    public void componentMetadataIsNullWhenTypeIsNotComponent() {
        Type type = TestSupport.simpleType(TestSupport.TypeConfig.of(Object.class, false, false, false,
                new int[]{Types.OTHER}));
        SessionFactoryImplementor sessionFactory = TestSupport.sessionFactory(null);
        HibernateNonEntityMetadata metadata = new HibernateNonEntityMetadata(sessionFactory, type, null);

        assertNull(metadata.getProperties());
        assertNull(metadata.getPropertyType("anything"));
    }
}
