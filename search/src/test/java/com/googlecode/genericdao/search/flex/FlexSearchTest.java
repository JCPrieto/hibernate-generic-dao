package com.googlecode.genericdao.search.flex;

import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Sort;
import org.junit.Test;

import static org.junit.Assert.*;

public class FlexSearchTest {

    @Test
    public void setFiltersClearsExistingValuesAndIgnoresNullEntries() {
        FlexSearch search = new FlexSearch();
        Filter first = new Filter("name", "Alice");
        Filter second = new Filter("status", "active");
        search.setFilters(new Filter[]{first});

        search.setFilters(new Filter[]{null, second});

        Filter[] filters = search.getFilters();
        assertEquals(1, filters.length);
        assertSame(second, filters[0]);

        search.setFilters(null);

        assertEquals(0, search.getFilters().length);
    }

    @Test
    public void setSortsClearsExistingValuesAndIgnoresNullEntries() {
        FlexSearch search = new FlexSearch();
        Sort first = Sort.asc("name");
        Sort second = Sort.desc("createdAt");
        search.setSorts(new Sort[]{first});

        search.setSorts(new Sort[]{null, second});

        Sort[] sorts = search.getSorts();
        assertEquals(1, sorts.length);
        assertSame(second, sorts[0]);

        search.setSorts(null);

        assertEquals(0, search.getSorts().length);
    }

    @Test
    public void setFieldsKeepsOnlyFieldsWithPropertyAndDefaultsMissingKey() {
        FlexSearch search = new FlexSearch();
        Field first = new Field("name");
        search.setFields(new Field[]{first});
        Field withMissingKey = new Field("createdAt");
        Field withKey = new Field("status", "state");

        search.setFields(new Field[]{null, new Field(), new Field(""), withMissingKey, withKey});

        Field[] fields = search.getFields();
        assertEquals(2, fields.length);
        assertSame(withMissingKey, fields[0]);
        assertEquals("createdAt", fields[0].getKey());
        assertSame(withKey, fields[1]);
        assertEquals("state", fields[1].getKey());

        search.setFields(null);

        assertEquals(0, search.getFields().length);
    }

    @Test
    public void setFetchesClearsExistingValuesAndIgnoresNullOrEmptyValues() {
        FlexSearch search = new FlexSearch();
        search.setFetches(new String[]{"owner"});

        search.setFetches(new String[]{null, "", "items"});

        assertArrayEquals(new String[]{"items"}, search.getFetches());

        search.setFetches(null);

        assertEquals(0, search.getFetches().length);
    }

    @Test
    public void scalarPropertiesRoundTrip() {
        FlexSearch search = new FlexSearch();

        search.setSearchClassName("com.example.Entity");
        search.setFirstResult(10);
        search.setMaxResults(20);
        search.setPage(2);
        search.setDisjunction(true);
        search.setDistinct(true);
        search.setResultMode(7);

        assertEquals("com.example.Entity", search.getSearchClassName());
        assertEquals(10, search.getFirstResult());
        assertEquals(20, search.getMaxResults());
        assertEquals(2, search.getPage());
        assertTrue(search.isDisjunction());
        assertTrue(search.isDistinct());
        assertEquals(7, search.getResultMode());

        search.setDisjunction(false);
        search.setDistinct(false);

        assertFalse(search.isDisjunction());
        assertFalse(search.isDistinct());
    }
}
