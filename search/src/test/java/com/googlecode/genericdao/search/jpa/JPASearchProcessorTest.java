package com.googlecode.genericdao.search.jpa;

import com.googlecode.genericdao.search.*;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JPASearchProcessorTest {

    @SuppressWarnings("unchecked")
    private List transformResults(JPASearchProcessor processor, List results, ISearch search) throws Exception {
        Method method = JPASearchProcessor.class.getDeclaredMethod("transformResults", List.class, ISearch.class);
        method.setAccessible(true);
        return (List) method.invoke(processor, results, search);
    }

    @Test
    public void transformResultsAutoMapUsesKeys() throws Exception {
        JPASearchProcessor processor = new JPASearchProcessor(new DummyMetadataUtil());
        Search search = new Search();
        search.getFields().add(new Field("name", "nameKey"));

        List<Object[]> results = new ArrayList<Object[]>();
        results.add(new Object[]{"alpha"});
        results.add(new Object[]{"beta"});

        List<Map<String, Object>> mapped = transformResults(processor, results, search);

        assertEquals(2, mapped.size());
        assertEquals("alpha", mapped.get(0).get("nameKey"));
        assertEquals("beta", mapped.get(1).get("nameKey"));
    }

    @Test
    public void transformResultsAutoArrayWrapsSingleValues() throws Exception {
        JPASearchProcessor processor = new JPASearchProcessor(new DummyMetadataUtil());
        Search search = new Search();
        search.getFields().add(new Field("name"));
        search.getFields().add(new Field("age"));

        List<String> results = Arrays.asList("alpha", "beta");

        List<Object[]> arrays = transformResults(processor, results, search);

        assertEquals(2, arrays.size());
        assertEquals("alpha", arrays.get(0)[0]);
        assertEquals("beta", arrays.get(1)[0]);
    }

    @Test
    public void transformResultsListModeConvertsArrays() throws Exception {
        JPASearchProcessor processor = new JPASearchProcessor(new DummyMetadataUtil());
        Search search = new Search();
        search.setResultMode(ISearch.RESULT_LIST);
        search.getFields().add(new Field("name"));

        List<Object[]> results = new ArrayList<Object[]>();
        results.add(new Object[]{"alpha", "beta"});

        List<List> lists = transformResults(processor, results, search);

        assertEquals(1, lists.size());
        assertEquals(2, lists.get(0).size());
        assertEquals("alpha", lists.get(0).get(0));
        assertEquals("beta", lists.get(0).get(1));
    }

    private static final class DummyMetadata implements Metadata {
        private final Class<?> javaClass;
        private final String entityName;

        private DummyMetadata(Class<?> javaClass, String entityName) {
            this.javaClass = javaClass;
            this.entityName = entityName;
        }

        @Override
        public boolean isEntity() {
            return entityName != null;
        }

        @Override
        public boolean isEmbeddable() {
            return false;
        }

        @Override
        public boolean isCollection() {
            return false;
        }

        @Override
        public boolean isString() {
            return String.class.equals(javaClass);
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
            return new DummyMetadata(klass, klass.getSimpleName());
        }

        @Override
        public Metadata get(Class<?> rootEntityClass, String propertyPath) throws IllegalArgumentException {
            if (propertyPath == null || propertyPath.isEmpty()) {
                return get(rootEntityClass);
            }
            return new DummyMetadata(Object.class, null);
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
}
