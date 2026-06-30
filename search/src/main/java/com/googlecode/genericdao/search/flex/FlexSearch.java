/* Copyright 2013 David Wolverton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.genericdao.search.flex;

import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a search DTO that is suitable for both Flex and Java. Is can be used
 * to pass search parameters to and from a remote Flex client.
 * 
 * @author dwolverton
 * 
 */
public class FlexSearch implements Serializable {

	private static final long serialVersionUID = 1L;

	protected int firstResult = -1; // -1 stands for unspecified

	protected int maxResults = -1; // -1 stands for unspecified

	protected int page = -1; // -1 stands for unspecified

	protected String searchClassName;

	protected List<Filter> filters = new ArrayList<>();

	protected boolean disjunction;

	protected List<Sort> sorts = new ArrayList<>();

	protected List<Field> fields = new ArrayList<>();
	
	protected boolean distinct;

	protected List<String> fetches = new ArrayList<>();

	protected int resultMode = ISearch.RESULT_AUTO;

	public void setSearchClassName(String searchClassName) {
		this.searchClassName = searchClassName;
	}

	public String getSearchClassName() {
		return searchClassName;
	}

	public Filter[] getFilters() {
		return filters.toArray(new Filter[0]);
	}

	public void setFilters(Filter[] filters) {
		this.filters.clear();
		if (filters != null) {
			for (Filter filter : filters) {
				if (filter != null) {
					this.filters.add(filter);
				}
			}
		}
	}

	public Sort[] getSorts() {
		return sorts.toArray(new Sort[0]);
	}

	public void setSorts(Sort[] sorts) {
		this.sorts.clear();
		if (sorts != null) {
			for (Sort sort : sorts) {
				if (sort != null) {
					this.sorts.add(sort);
				}
			}
		}
	}

	public Field[] getFields() {
		return fields.toArray(new Field[0]);
	}

	public void setFields(Field[] fields) {
		this.fields.clear();
		if (fields != null) {
			for (Field f : fields) {
				if (f != null && f.getProperty() != null && !f.getProperty().isEmpty()) {
					if (f.getKey() == null)
						f.setKey(f.getProperty());
					this.fields.add(f);
				}
			}
		}
	}

	public String[] getFetches() {
		return fetches.toArray(new String[0]);
	}

	public void setFetches(String[] fetches) {
		this.fetches.clear();
		if (fetches != null) {
			for (String fetch : fetches) {
				if (fetch != null && !fetch.isEmpty())
					this.fetches.add(fetch);
			}
		}
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isDisjunction() {
		return disjunction;
	}

	public void setDisjunction(boolean disjunction) {
		this.disjunction = disjunction;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public int getResultMode() {
		return resultMode;
	}

	public void setResultMode(int resultMode) {
		this.resultMode = resultMode;
	}
}
