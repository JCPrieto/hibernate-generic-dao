package com.googlecode.genericdao.dao;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BaseDAODispatcherTest {

    private final BaseDAODispatcher dispatcher = new BaseDAODispatcher();

    @Test
    public void getSpecificDAOReturnsNullWhenMapIsNotConfigured() {
        assertNull(dispatcher.getSpecificDAO(String.class.getName()));
    }

    @Test
    public void getSpecificDAOReturnsConfiguredDAOForClassName() {
        Object dao = new Object();
        Map<String, Object> specificDAOs = new HashMap<String, Object>();
        specificDAOs.put(String.class.getName(), dao);

        dispatcher.setSpecificDAOs(specificDAOs);

        assertSame(dao, dispatcher.getSpecificDAO(String.class.getName()));
    }

    @Test
    public void callMethodInvokesMethodByName() {
        SampleDAO dao = new SampleDAO();

        Object result = dispatcher.callMethod(dao, "findName", Integer.valueOf(7));

        assertEquals("name-7", result);
    }

    @Test
    public void callMethodWrapsReflectionExceptions() {
        try {
            dispatcher.callMethod(new SampleDAO(), "missingMethod");
            fail("Expected DAODispatcherException");
        } catch (DAODispatcherException e) {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    @Test
    public void callMethodWithParameterTypesAllowsNullArguments() {
        Object result = dispatcher.callMethod(new SampleDAO(), "echo",
                new Class<?>[]{String.class}, new Object[]{null});

        assertNull(result);
    }

    @Test
    public void getTypeFromArrayReturnsNullForNullArray() {
        assertNull(BaseDAODispatcher.getTypeFromArray(null));
    }

    @Test
    public void getTypeFromArrayReturnsTypedArrayComponentType() {
        assertEquals(String.class, BaseDAODispatcher.getTypeFromArray(new String[]{"a", "b"}));
    }

    @Test
    public void getTypeFromArrayReturnsMostGeneralObjectArrayElementType() {
        assertEquals(BaseValue.class, BaseDAODispatcher.getTypeFromArray(
                new Object[]{new ChildValue(), new BaseValue()}));
    }

    @Test
    public void getTypeFromArrayReturnsNullForObjectArrayWithoutValues() {
        assertNull(BaseDAODispatcher.getTypeFromArray(new Object[]{null, null}));
    }

    @Test
    public void getUniformArrayTypeReturnsNullForNullArray() {
        assertNull(BaseDAODispatcher.getUniformArrayType(null));
    }

    @Test
    public void getUniformArrayTypeReturnsNullForEmptyArray() {
        assertNull(BaseDAODispatcher.getUniformArrayType(new Object[0]));
    }

    @Test
    public void getUniformArrayTypeReturnsNullWhenAllElementsAreNull() {
        assertNull(BaseDAODispatcher.getUniformArrayType(new Object[]{null, null}));
    }

    @Test
    public void getUniformArrayTypeReturnsClassWhenAllValuesHaveSameType() {
        assertEquals(String.class, BaseDAODispatcher.getUniformArrayType(
                new Object[]{"a", null, "b"}));
    }

    @Test
    public void getUniformArrayTypeReturnsObjectClassWhenValuesHaveDifferentTypes() {
        assertEquals(Object.class, BaseDAODispatcher.getUniformArrayType(
                new Object[]{"a", Integer.valueOf(1)}));
    }

    public static class SampleDAO {
        public String findName(Integer id) {
            return "name-" + id;
        }

        public String echo(String value) {
            return value;
        }
    }

    public static class BaseValue {
    }

    public static class ChildValue extends BaseValue {
    }
}
