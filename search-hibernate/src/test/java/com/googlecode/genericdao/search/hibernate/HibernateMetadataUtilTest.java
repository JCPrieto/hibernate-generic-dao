package com.googlecode.genericdao.search.hibernate;

import com.googlecode.genericdao.search.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.Type;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HibernateMetadataUtilTest {

    @Test
    public void getUnproxiedClassReturnsSuperclassForHibernateProxyName() {
        HibernateMetadataUtil util = HibernateMetadataUtil.getInstanceForSessionFactory(TestSupport.sessionFactory(null));

        Class<?> result = util.getUnproxiedClass(BaseEntity$HibernateProxy$abc.class);

        assertEquals(BaseEntity.class, result);
    }

    @Test
    public void getUnproxiedClassReturnsSuperclassForHibernateProxyInterface() {
        HibernateMetadataUtil util = HibernateMetadataUtil.getInstanceForSessionFactory(TestSupport.sessionFactory(null));

        Class<?> result = util.getUnproxiedClass(ProxyEntity.class);

        assertEquals(BaseEntity.class, result);
    }

    @Test
    public void getResolvesMetadataFromMetamodel() {
        Type idType = TestSupport.simpleType(TestSupport.TypeConfig.of(Long.class, false, false, false, new int[]{1}));
        EntityPersister persister = TestSupport.entityPersister(TestSupport.EntityPersisterConfig.of(
                BaseEntity.class.getName(), "id", idType, 7L, BaseEntity.class, new String[0], null, null));
        SessionFactoryImplementor sessionFactory = TestSupport.sessionFactory(persister);
        HibernateMetadataUtil util = HibernateMetadataUtil.getInstanceForSessionFactory(sessionFactory);

        Metadata metadata = util.get(BaseEntity.class);

        assertEquals("id", metadata.getIdProperty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThrowsWhenEntityIsNotRegistered() {
        HibernateMetadataUtil util = HibernateMetadataUtil.getInstanceForSessionFactory(TestSupport.sessionFactory(null));

        util.get(BaseEntity.class);
    }

    private static class BaseEntity {
    }

    @SuppressWarnings("unused")
    private static final class BaseEntity$HibernateProxy$abc extends BaseEntity {
    }

    private static final class ProxyEntity extends BaseEntity implements HibernateProxy {
        @Override
        public Object writeReplace() {
            return null;
        }

        @Override
        public LazyInitializer getHibernateLazyInitializer() {
            return null;
        }
    }
}
