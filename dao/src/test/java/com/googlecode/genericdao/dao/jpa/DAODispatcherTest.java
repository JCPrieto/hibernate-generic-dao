package com.googlecode.genericdao.dao.jpa;

import com.googlecode.genericdao.dao.DAODispatcherException;
import com.googlecode.genericdao.search.*;
import org.junit.Before;
import org.junit.Test;

import javax.activation.UnsupportedDataTypeException;
import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;

public class DAODispatcherTest {

    private DAODispatcher dispatcher;
    private RecordingGeneralDAO generalDAO;
    private RecordingGenericDAO genericDAO;
    private ReflectiveDAO reflectiveDAO;
    private Map<String, Object> specificDAOs;
    private SampleEntity entity;
    private OtherEntity otherEntity;

    @Before
    public void setUp() {
        dispatcher = new DAODispatcher();
        generalDAO = new RecordingGeneralDAO("general");
        genericDAO = new RecordingGenericDAO("generic");
        reflectiveDAO = new ReflectiveDAO("reflective");
        specificDAOs = new HashMap<>();
        entity = new SampleEntity("sample");
        otherEntity = new OtherEntity("other");

        dispatcher.setGeneralDAO(generalDAO);
    }

    @Test
    public void countUsesGenericSpecificDAOWhenConfigured() {
        configureSpecificDAO(SampleEntity.class, genericDAO);

        assertEquals(11, dispatcher.count(searchFor(SampleEntity.class)));

        assertEquals("count", genericDAO.lastCall);
        assertNull(generalDAO.lastCall);
    }

    @Test
    public void countUsesReflectiveSpecificDAOWhenConfiguredObjectIsNotGenericDAO() {
        configureSpecificDAO(SampleEntity.class, reflectiveDAO);

        assertEquals(21, dispatcher.count(searchFor(SampleEntity.class)));

        assertEquals("count", reflectiveDAO.lastCall);
        assertNull(generalDAO.lastCall);
    }

    @Test
    public void countFallsBackToGeneralDAOWhenSpecificDAOIsNotConfigured() {
        assertEquals(31, dispatcher.count(searchFor(SampleEntity.class)));

        assertEquals("count", generalDAO.lastCall);
    }

    @Test
    public void findDispatchesToGenericSpecificDAOWithoutPassingClass() {
        configureSpecificDAO(SampleEntity.class, genericDAO);

        assertSame(genericDAO.entity, dispatcher.find(SampleEntity.class, "id-1"));

        assertEquals("find", genericDAO.lastCall);
        assertEquals("id-1", genericDAO.lastId);
    }

    @Test
    public void findDispatchesToReflectiveSpecificDAOWithoutPassingClass() {
        configureSpecificDAO(SampleEntity.class, reflectiveDAO);

        assertSame(reflectiveDAO.entity, dispatcher.find(SampleEntity.class, "id-1"));

        assertEquals("find", reflectiveDAO.lastCall);
        assertEquals("id-1", reflectiveDAO.lastId);
    }

    @Test
    public void findFallsBackToGeneralDAOWithClassAndId() {
        assertSame(generalDAO.entity, dispatcher.find(SampleEntity.class, "id-1"));

        assertEquals("find", generalDAO.lastCall);
        assertSame(SampleEntity.class, generalDAO.lastType);
        assertEquals("id-1", generalDAO.lastId);
    }

    @Test
    public void findManyDispatchesToSpecificOrGeneralDAO() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.entities, dispatcher.find(SampleEntity.class, "a", "b"));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.entities, dispatcher.find(SampleEntity.class, "a", "b"));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.entities, dispatcher.find(SampleEntity.class, "a", "b"));
    }

    @Test
    public void findAllDispatchesToEachDAOShape() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.list, dispatcher.findAll(SampleEntity.class));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.list, dispatcher.findAll(SampleEntity.class));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.list, dispatcher.findAll(SampleEntity.class));
    }

    @Test
    public void flushWithoutClassIsRejectedBecauseDispatcherCannotChooseDAO() {
        try {
            dispatcher.flush();
            fail("Expected DAODispatcherException");
        } catch (DAODispatcherException e) {
            assertTrue(e.getMessage().contains("flush(Class<?>)"));
        }
    }

    @Test
    public void flushWithClassDispatchesToConfiguredOrGeneralDAO() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        dispatcher.flush(SampleEntity.class);
        assertEquals("flush", genericDAO.lastCall);

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        dispatcher.flush(SampleEntity.class);
        assertEquals("flush", reflectiveDAO.lastCall);

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        dispatcher.flush(SampleEntity.class);
        assertEquals("flush", generalDAO.lastCall);
    }

    @Test
    public void getReferenceDispatchesToSpecificOrGeneralDAO() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.entity, dispatcher.getReference(SampleEntity.class, "id-1"));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.entity, dispatcher.getReference(SampleEntity.class, "id-2"));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.entity, dispatcher.getReference(SampleEntity.class, "id-3"));
    }

    @Test
    public void getReferencesDispatchesToSpecificOrGeneralDAO() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.entities, dispatcher.getReferences(SampleEntity.class, "a", "b"));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.entities, dispatcher.getReferences(SampleEntity.class, "a", "b"));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.entities, dispatcher.getReferences(SampleEntity.class, "a", "b"));
    }

    @Test
    public void isAttachedDispatchesByEntityClass() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertTrue(dispatcher.isAttached(entity));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertFalse(dispatcher.isAttached(entity));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertTrue(dispatcher.isAttached(entity));
    }

    @Test
    public void refreshReturnsForNullOrEmptyInput() {
        dispatcher.refresh((Object[]) null);
        dispatcher.refresh();

        assertNull(generalDAO.lastCall);
    }

    @Test
    public void refreshDispatchesUniformArraysAndSplitsMixedArrays() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        configureSpecificDAO(OtherEntity.class, reflectiveDAO);

        dispatcher.refresh(entity);
        assertEquals("refresh", genericDAO.lastCall);

        dispatcher.refresh(entity, otherEntity);
        assertEquals(Arrays.asList("refresh", "refresh"), genericDAO.calls);
        assertEquals("refresh", reflectiveDAO.lastCall);
    }

    @Test
    public void removeSingleEntityDispatchesByEntityClass() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertTrue(dispatcher.remove(entity));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertFalse(dispatcher.remove(entity));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertTrue(dispatcher.remove(entity));
    }

    @Test
    public void removeArrayReturnsForNullOrEmptyAndSplitsMixedArrays() {
        dispatcher.remove((Object[]) null);
        dispatcher.remove();
        assertNull(generalDAO.lastCall);

        configureSpecificDAO(SampleEntity.class, genericDAO);
        configureSpecificDAO(OtherEntity.class, reflectiveDAO);

        dispatcher.remove(entity, otherEntity);

        assertEquals("remove", genericDAO.lastCall);
        assertEquals("remove", reflectiveDAO.lastCall);
    }

    @Test
    public void removeArrayDispatchesUniformArraysToSpecificOrGeneralDAO() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        dispatcher.remove(entity, entity);
        assertEquals("removeMany", genericDAO.lastCall);

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        dispatcher.remove(entity, entity);
        assertEquals("removeMany", reflectiveDAO.lastCall);

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        dispatcher.remove(entity, entity);
        assertEquals("removeMany", generalDAO.lastCall);
    }

    @Test
    public void removeByIdDispatchesToSpecificOrGeneralDAO() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertTrue(dispatcher.removeById(SampleEntity.class, "id-1"));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertFalse(dispatcher.removeById(SampleEntity.class, "id-2"));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertTrue(dispatcher.removeById(SampleEntity.class, "id-3"));
    }

    @Test
    public void removeByIdsDispatchesToSpecificOrGeneralDAO() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        dispatcher.removeByIds(SampleEntity.class, "a", "b");
        assertEquals("removeByIds", genericDAO.lastCall);

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        dispatcher.removeByIds(SampleEntity.class, "a", "b");
        assertEquals("removeByIds", reflectiveDAO.lastCall);

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        dispatcher.removeByIds(SampleEntity.class, "a", "b");
        assertEquals("removeByIds", generalDAO.lastCall);
    }

    @Test
    public void saveSingleEntityDispatchesByEntityClass() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.entity, dispatcher.save(entity));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.entity, dispatcher.save(entity));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.entity, dispatcher.save(entity));
    }

    @Test
    public void saveArrayCoversNullEmptyMixedSpecificAndGeneralPaths() {
        assertNull(dispatcher.save((Object[]) null));
        assertEquals(0, dispatcher.save().length);
        assertNull(generalDAO.lastCall);

        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.entities, dispatcher.save(entity, entity));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.entities, dispatcher.save(entity, entity));

        configureSpecificDAO(OtherEntity.class, reflectiveDAO);
        Object[] mixed = dispatcher.save(entity, otherEntity);
        assertSame(reflectiveDAO.entity, mixed[0]);
        assertSame(reflectiveDAO.entity, mixed[1]);

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.entities, dispatcher.save(entity, entity));
    }

    @Test
    public void searchMethodsDispatchToSpecificOrGeneralDAO() {
        ISearch search = searchFor(SampleEntity.class);

        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.list, dispatcher.search(search));
        assertSame(genericDAO.entity, dispatcher.searchUnique(search));
        assertSame(genericDAO.searchResult, dispatcher.searchAndCount(search));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.list, dispatcher.search(search));
        assertSame(reflectiveDAO.entity, dispatcher.searchUnique(search));
        assertSame(reflectiveDAO.searchResult, dispatcher.searchAndCount(search));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.list, dispatcher.search(search));
        assertSame(generalDAO.entity, dispatcher.searchUnique(search));
        assertSame(generalDAO.searchResult, dispatcher.searchAndCount(search));
    }

    @Test
    public void getFilterFromExampleDispatchesToSpecificOrGeneralDAO() throws UnsupportedDataTypeException {
        ExampleOptions options = new ExampleOptions();

        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.filter, dispatcher.getFilterFromExample(entity));
        assertSame(genericDAO.filter, dispatcher.getFilterFromExample(entity, options));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.filter, dispatcher.getFilterFromExample(entity));
        assertSame(reflectiveDAO.filter, dispatcher.getFilterFromExample(entity, options));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.filter, dispatcher.getFilterFromExample(entity));
        assertSame(generalDAO.filter, dispatcher.getFilterFromExample(entity, options));
    }

    @Test
    public void mergeSingleEntityDispatchesByEntityClass() {
        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.entity, dispatcher.merge(entity));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.entity, dispatcher.merge(entity));

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.entity, dispatcher.merge(entity));
    }

    @Test
    public void mergeArrayCoversNullEmptyMixedSpecificAndGeneralPaths() {
        assertNull(dispatcher.merge((Object[]) null));
        assertEquals(0, dispatcher.merge().length);

        configureSpecificDAO(SampleEntity.class, genericDAO);
        assertSame(genericDAO.entities, dispatcher.merge(entity, entity));

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        assertSame(reflectiveDAO.entities, dispatcher.merge(entity, entity));

        configureSpecificDAO(OtherEntity.class, reflectiveDAO);
        Object[] mixed = dispatcher.merge(entity, otherEntity);
        assertSame(reflectiveDAO.entity, mixed[0]);
        assertSame(reflectiveDAO.entity, mixed[1]);

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        assertSame(generalDAO.entities, dispatcher.merge(entity, entity));
    }

    @Test
    public void persistReturnsForNullOrEmptyAndDispatchesMixedArrays() {
        dispatcher.persist((Object[]) null);
        dispatcher.persist();
        assertNull(generalDAO.lastCall);

        configureSpecificDAO(SampleEntity.class, genericDAO);
        dispatcher.persist(entity);
        assertEquals("persist", genericDAO.lastCall);

        configureSpecificDAO(SampleEntity.class, reflectiveDAO);
        dispatcher.persist(entity);
        assertEquals("persist", reflectiveDAO.lastCall);

        configureSpecificDAO(OtherEntity.class, reflectiveDAO);
        dispatcher.persist(entity, otherEntity);
        assertEquals("persist", reflectiveDAO.lastCall);

        dispatcher.setSpecificDAOs(Collections.emptyMap());
        dispatcher.persist(entity);
        assertEquals("persist", generalDAO.lastCall);
    }

    private void configureSpecificDAO(Class<?> type, Object dao) {
        specificDAOs.put(type.getName(), dao);
        dispatcher.setSpecificDAOs(specificDAOs);
    }

    private ISearch searchFor(Class<?> type) {
        return new Search(type);
    }

    public static class SampleEntity {
        private final String name;

        public SampleEntity(String name) {
            this.name = name;
        }
    }

    public static class OtherEntity extends SampleEntity {
        public OtherEntity(String name) {
            super(name);
        }
    }

    private static class RecordingGenericDAO implements GenericDAO<SampleEntity, String> {
        final SampleEntity entity;
        final SampleEntity[] entities;
        final List<SampleEntity> list;
        final SearchResult<SampleEntity> searchResult = new SearchResult<>();
        final Filter filter = new Filter("source", "generic");
        final List<String> calls = new java.util.ArrayList<>();
        String lastCall;
        Serializable lastId;

        RecordingGenericDAO(String source) {
            entity = new SampleEntity(source);
            entities = new SampleEntity[]{entity};
            list = Collections.singletonList(entity);
        }

        public SampleEntity find(String id) {
            record("find");
            lastId = id;
            return entity;
        }

        public SampleEntity[] find(String... ids) {
            record("findMany");
            return entities;
        }

        public SampleEntity getReference(String id) {
            record("getReference");
            lastId = id;
            return entity;
        }

        public SampleEntity[] getReferences(String... ids) {
            record("getReferences");
            return entities;
        }

        public void persist(SampleEntity... entities) {
            record("persist");
        }

        public SampleEntity merge(SampleEntity entity) {
            record("merge");
            return this.entity;
        }

        public SampleEntity[] merge(SampleEntity... entities) {
            record("mergeMany");
            return this.entities;
        }

        public SampleEntity save(SampleEntity entity) {
            record("save");
            return this.entity;
        }

        public SampleEntity[] save(SampleEntity... entities) {
            record("saveMany");
            return this.entities;
        }

        public boolean remove(SampleEntity entity) {
            record("remove");
            return true;
        }

        public void remove(SampleEntity... entities) {
            record("removeMany");
        }

        public boolean removeById(String id) {
            record("removeById");
            return true;
        }

        public void removeByIds(String... ids) {
            record("removeByIds");
        }

        public List<SampleEntity> findAll() {
            record("findAll");
            return list;
        }

        public <RT> List<RT> search(ISearch search) {
            record("search");
            return (List<RT>) list;
        }

        public <RT> RT searchUnique(ISearch search) {
            record("searchUnique");
            return (RT) entity;
        }

        public int count(ISearch search) {
            record("count");
            return 11;
        }

        public <RT> SearchResult<RT> searchAndCount(ISearch search) {
            record("searchAndCount");
            return (SearchResult<RT>) searchResult;
        }

        public boolean isAttached(SampleEntity entity) {
            record("isAttached");
            return true;
        }

        public void refresh(SampleEntity... entities) {
            record("refresh");
        }

        public void flush() {
            record("flush");
        }

        public Filter getFilterFromExample(SampleEntity example) {
            record("getFilterFromExample");
            return filter;
        }

        public Filter getFilterFromExample(SampleEntity example, ExampleOptions options) {
            record("getFilterFromExampleOptions");
            return filter;
        }

        private void record(String call) {
            lastCall = call;
            calls.add(call);
        }
    }

    private static class RecordingGeneralDAO implements GeneralDAO {
        final SampleEntity entity;
        final SampleEntity[] entities;
        final List<SampleEntity> list;
        final SearchResult<SampleEntity> searchResult = new SearchResult<>();
        final Filter filter = new Filter("source", "general");
        String lastCall;
        Class<?> lastType;
        Serializable lastId;

        RecordingGeneralDAO(String source) {
            entity = new SampleEntity(source);
            entities = new SampleEntity[]{entity};
            list = Collections.singletonList(entity);
        }

        public <T> T find(Class<T> type, Serializable id) {
            record("find");
            lastType = type;
            lastId = id;
            return (T) entity;
        }

        public <T> T[] find(Class<T> type, Serializable... ids) {
            record("findMany");
            return (T[]) entities;
        }

        public <T> T getReference(Class<T> type, Serializable id) {
            record("getReference");
            return (T) entity;
        }

        public <T> T[] getReferences(Class<T> type, Serializable... ids) {
            record("getReferences");
            return (T[]) entities;
        }

        public void persist(Object... entities) {
            record("persist");
        }

        public <T> T merge(T entity) {
            record("merge");
            return (T) this.entity;
        }

        public Object[] merge(Object... entities) {
            record("mergeMany");
            return this.entities;
        }

        public <T> T save(T entity) {
            record("save");
            return (T) this.entity;
        }

        public Object[] save(Object... entities) {
            record("saveMany");
            return this.entities;
        }

        public boolean remove(Object entity) {
            record("remove");
            return true;
        }

        public void remove(Object... entities) {
            record("removeMany");
        }

        public boolean removeById(Class<?> type, Serializable id) {
            record("removeById");
            return true;
        }

        public void removeByIds(Class<?> type, Serializable... ids) {
            record("removeByIds");
        }

        public <T> List<T> findAll(Class<T> type) {
            record("findAll");
            return (List<T>) list;
        }

        public List search(ISearch search) {
            record("search");
            return list;
        }

        public Object searchUnique(ISearch search) {
            record("searchUnique");
            return entity;
        }

        public int count(ISearch search) {
            record("count");
            return 31;
        }

        public SearchResult searchAndCount(ISearch search) {
            record("searchAndCount");
            return searchResult;
        }

        public boolean isAttached(Object entity) {
            record("isAttached");
            return true;
        }

        public void refresh(Object... entities) {
            record("refresh");
        }

        public void flush() {
            record("flush");
        }

        public Filter getFilterFromExample(Object example) {
            record("getFilterFromExample");
            return filter;
        }

        public Filter getFilterFromExample(Object example, ExampleOptions options) {
            record("getFilterFromExampleOptions");
            return filter;
        }

        private void record(String call) {
            lastCall = call;
        }
    }

    public static class ReflectiveDAO {
        final SampleEntity entity;
        final SampleEntity[] entities;
        final List<SampleEntity> list;
        final SearchResult<SampleEntity> searchResult = new SearchResult<>();
        final Filter filter = new Filter("source", "reflective");
        String lastCall;
        Serializable lastId;

        ReflectiveDAO(String source) {
            entity = new SampleEntity(source);
            entities = new SampleEntity[]{entity};
            list = Collections.singletonList(entity);
        }

        public int count(ISearch search) {
            record("count");
            return 21;
        }

        public SampleEntity find(Serializable id) {
            record("find");
            lastId = id;
            return entity;
        }

        public SampleEntity[] find(Serializable... ids) {
            record("findMany");
            return entities;
        }

        public List<SampleEntity> findAll() {
            record("findAll");
            return list;
        }

        public void flush() {
            record("flush");
        }

        public SampleEntity getReference(Serializable id) {
            record("getReference");
            return entity;
        }

        public SampleEntity[] getReferences(Serializable... ids) {
            record("getReferences");
            return entities;
        }

        public boolean isAttached(Object entity) {
            record("isAttached");
            return false;
        }

        public void refresh(Object... entities) {
            record("refresh");
        }

        public boolean remove(Object entity) {
            record("remove");
            return false;
        }

        public void remove(Object... entities) {
            record("removeMany");
        }

        public boolean removeById(Serializable id) {
            record("removeById");
            return false;
        }

        public void removeByIds(Serializable... ids) {
            record("removeByIds");
        }

        public SampleEntity save(Object entity) {
            record("save");
            return this.entity;
        }

        public Object[] save(Object... entities) {
            record("saveMany");
            return this.entities;
        }

        public List<SampleEntity> search(ISearch search) {
            record("search");
            return list;
        }

        public SampleEntity searchUnique(ISearch search) {
            record("searchUnique");
            return entity;
        }

        public SearchResult<SampleEntity> searchAndCount(ISearch search) {
            record("searchAndCount");
            return searchResult;
        }

        public Filter getFilterFromExample(Object example) {
            record("getFilterFromExample");
            return filter;
        }

        public Filter getFilterFromExample(Object example, ExampleOptions options) {
            record("getFilterFromExampleOptions");
            return filter;
        }

        public SampleEntity merge(Object entity) {
            record("merge");
            return this.entity;
        }

        public Object[] merge(Object... entities) {
            record("mergeMany");
            return this.entities;
        }

        public void persist(Object... entities) {
            record("persist");
        }

        private void record(String call) {
            lastCall = call;
        }
    }
}
