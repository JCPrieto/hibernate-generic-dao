package com.googlecode.genericdao.search;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BaseSearchProcessorTest {

    @Test
    public void generateRowCountQLUsesRootAlias() {
        TestSearchProcessor processor = new TestSearchProcessor(new DummyMetadataUtil());
        Search search = new Search();
        List<Object> params = new ArrayList<Object>();

        String ql = processor.generateRowCount(ExampleEntity.class, search, params);

        assertEquals("select count(_it) from ExampleEntity _it", ql);
        assertEquals(0, params.size());
    }

    @Test
    public void generateRowCountQLWithDistinctSingleFieldUsesPathRef() {
        TestSearchProcessor processor = new TestSearchProcessor(new DummyMetadataUtil());
        Search search = new Search();
        search.setDistinct(true);
        search.addField("name");
        List<Object> params = new ArrayList<Object>();

        String ql = processor.generateRowCount(ExampleEntity.class, search, params);

        assertEquals("select count(distinct _it.name) from ExampleEntity _it", ql);
        assertEquals(0, params.size());
    }

    @Test
    public void cleanFiltersDropsNullValues() {
        TestSearchProcessor processor = new TestSearchProcessor(new DummyMetadataUtil());
        Filter valid = new Filter("name", "alpha", Filter.OP_EQUAL);
        Filter invalid = new Filter("name", null, Filter.OP_EQUAL);

        List<Filter> cleaned = processor.cleanFilters(new ArrayList<Filter>(Arrays.asList(valid, invalid)));

        assertEquals(1, cleaned.size());
        assertEquals(valid, cleaned.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cleanFiltersRejectsEmptyCustomExpression() {
        TestSearchProcessor processor = new TestSearchProcessor(new DummyMetadataUtil());
        Filter custom = Filter.custom("");

        processor.cleanFilters(new ArrayList<Filter>(Arrays.asList(custom)));
    }

    private static final class ExampleEntity {
        private String name;

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }
    }

    private static final class DummyMetadata implements Metadata {
        private final Class<?> javaClass;
        private final boolean entity;
        private final boolean embeddable;
        private final boolean collection;
        private final boolean stringType;
        private final String entityName;

        private DummyMetadata(Class<?> javaClass, boolean entity, boolean embeddable, boolean collection,
                              boolean stringType, String entityName) {
            this.javaClass = javaClass;
            this.entity = entity;
            this.embeddable = embeddable;
            this.collection = collection;
            this.stringType = stringType;
            this.entityName = entityName;
        }

        @Override
        public boolean isEntity() {
            return entity;
        }

        @Override
        public boolean isEmbeddable() {
            return embeddable;
        }

        @Override
        public boolean isCollection() {
            return collection;
        }

        @Override
        public boolean isString() {
            return stringType;
        }

        @Override
        public boolean isNumeric() {
            return Number.class.isAssignableFrom(javaClass);
        }

        @Override
        public Class<?> getJavaClass() {
            return javaClass;
        }

        @Override
        public String getEntityName() {
            if (entityName == null) {
                throw new UnsupportedOperationException("Entity name not available.");
            }
            return entityName;
        }

        @Override
        public String[] getProperties() {
            return new String[0];
        }

        @Override
        public Object getPropertyValue(Object object, String property) {
            return null;
        }

        @Override
        public Metadata getPropertyType(String property) {
            return null;
        }

        @Override
        public String getIdProperty() {
            return null;
        }

        @Override
        public Metadata getIdType() {
            return null;
        }

        @Override
        public Serializable getIdValue(Object object) {
            return null;
        }

        @Override
        public Class<?> getCollectionClass() {
            return null;
        }
    }

    private static final class DummyMetadataUtil implements MetadataUtil {
        @Override
        public Serializable getId(Object object) {
            return null;
        }

        @Override
        public boolean isId(Class<?> rootClass, String propertyPath) {
            return "id".equals(propertyPath) || (propertyPath != null && propertyPath.endsWith(".id"));
        }

        @Override
        public Metadata get(Class<?> klass) throws IllegalArgumentException {
            return new DummyMetadata(klass, true, false, false, false, klass.getSimpleName());
        }

        @Override
        public Metadata get(Class<?> rootEntityClass, String propertyPath) throws IllegalArgumentException {
            if (propertyPath == null || propertyPath.isEmpty()) {
                return get(rootEntityClass);
            }
            if ("name".equals(propertyPath)) {
                return new DummyMetadata(String.class, false, false, false, true, null);
            }
            return new DummyMetadata(Object.class, false, false, false, false, null);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Class<T> getUnproxiedClass(Class<?> klass) {
            return (Class<T>) klass;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Class<T> getUnproxiedClass(Object entity) {
            return (Class<T>) entity.getClass();
        }
    }

    private static final class TestSearchProcessor extends BaseSearchProcessor {
        private TestSearchProcessor(MetadataUtil metadataUtil) {
            super(QLTYPE_HQL, metadataUtil);
        }

        public String generateRowCount(Class<?> entityClass, ISearch search, List<Object> params) {
            return generateRowCountQL(entityClass, search, params);
        }

        public List<Filter> cleanFilters(List<Filter> filters) {
            return checkAndCleanFilters(filters);
        }
    }
}
