/*
 * Copyright (c) 2010
 *
 * This file is part of HibernateJConsole.
 *
 *     HibernateJConsole is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     HibernateJConsole is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with HibernateJConsole.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.hibernate.jconsole;

import net.sf.hibernate.jconsole.stats.*;
import net.sf.hibernate.jconsole.util.DataSampler;
import net.sf.hibernate.jconsole.util.DataTable;
import net.sf.hibernate.jconsole.util.FixedSizeDataSampler;
import net.sf.hibernate.jconsole.util.TimeboxedDataSampler;

import javax.management.MBeanServerConnection;
import java.io.Serializable;
import java.util.*;

/**
 * Partially implemented abstraction layer for JPA providers.
 *
 * @author Juergen_Kellerer, 2010-09-16
 * @version 1.0
 */
public abstract class AbstractStatisticsContext implements Serializable {

	private static final long serialVersionUID = 1682035090240809541L;

	private static final int HISTORY_LENGTH = Integer.getInteger("hibernate.history.length", 5000);

	private static final ServiceLoader<AbstractStatisticsContext> CONTEXT_LOADER =
			ServiceLoader.load(AbstractStatisticsContext.class);

	public static List<AbstractStatisticsContext> getAvailableContexts() {
		List<AbstractStatisticsContext> contexts = new ArrayList<AbstractStatisticsContext>();
		for (AbstractStatisticsContext context : CONTEXT_LOADER) {
			if (context.isEnabled())
				contexts.add(context);
		}
		return contexts;
	}

	private transient MBeanServerConnection connection;

	private Map<Names, Object> attributes = new HashMap<Names, Object>();

	private Map<String, EntityStatistics> entityStatistics = new LinkedHashMap<String, EntityStatistics>();
	private Map<String, CollectionStatistics> collectionStatistics = new LinkedHashMap<String, CollectionStatistics>();

	private Map<String, QueryStatistics> queryStatistics = new LinkedHashMap<String, QueryStatistics>();
	private Map<String, SecondLevelCacheStatistics> cacheStatistics =
			new LinkedHashMap<String, SecondLevelCacheStatistics>();

	private DataTable statisticsTable = new DataTable(HISTORY_LENGTH,
			new DataTable.Column("Queries", DataTable.DataFlavour.average),
			new DataTable.Column("UnCachedQueries", DataTable.DataFlavour.average),
			new DataTable.Column("Loads", DataTable.DataFlavour.average),
			new DataTable.Column("Fetches", DataTable.DataFlavour.average),
			new DataTable.Column("Modifications", DataTable.DataFlavour.average));

	private Map<String, DataSampler<Long>> samplers = new HashMap<String, DataSampler<Long>>();

	{
		samplers.put("QueriesPerMinute", new TimeboxedDataSampler<Long>(60 * 1000));
		samplers.put("UnCachedQueriesPerMinute", new TimeboxedDataSampler<Long>(60 * 1000));
		samplers.put("LoadsPerMinute", new TimeboxedDataSampler<Long>(60 * 1000));
		samplers.put("FetchesPerMinute", new TimeboxedDataSampler<Long>(60 * 1000));
		samplers.put("ModificationsPerMinute", new TimeboxedDataSampler<Long>(60 * 1000));

		samplers.put("Queries", new FixedSizeDataSampler<Long>(2));
		samplers.put("UnCachedQueries", new FixedSizeDataSampler<Long>(2));
		samplers.put("Loads", new FixedSizeDataSampler<Long>(2));
		samplers.put("Fetches", new FixedSizeDataSampler<Long>(2));
		samplers.put("Modifications", new FixedSizeDataSampler<Long>(2));
	}

	protected abstract Object getEntityStatisticsFor(String name) throws Exception;

	protected abstract Object getCollectionStatisticsFor(String name) throws Exception;

	protected abstract Object getQueryStatisticsFor(String name) throws Exception;

	protected abstract Object getCacheStatisticsFor(String name) throws Exception;

	protected abstract <T> T proxyFor(Class<T> interfaceClass, Object delegate);

	protected abstract Map<String, Object> getAttributes(List<String> attributeNames) throws Exception;

	protected abstract boolean isEnabled();

	public void refreshAll() throws Exception {
		if (getConnection() == null)
			throw new IllegalStateException("Cannot call refresh when not connected.");

		refreshAttributes();
		refreshStatisticsTable();
		refreshEntityStatistics();
		refreshCollectionStatistics();

		refreshQueryStatistics();
		refreshCacheStatistics();
	}

	void refreshAttributes() throws Exception {
		List<String> attributeNames = Names.getAllAttributes();
		for (Map.Entry<String, Object> e : getAttributes(attributeNames).entrySet())
			this.attributes.put(Names.valueOf(e.getKey()), e.getValue());
	}

	void applyAttributesToSamplers() {
		sample("Queries", sample("QueriesPerMinute",
				(Long) attributes.get(Names.QueryExecutionCount) +
						(Long) attributes.get(Names.QueryCacheHitCount)));

		sample("UnCachedQueries", sample("UnCachedQueriesPerMinute",
				(Long) attributes.get(Names.QueryExecutionCount)));

		sample("Loads", sample("LoadsPerMinute",
				(Long) attributes.get(Names.CollectionLoadCount) +
						(Long) attributes.get(Names.EntityLoadCount)));

		sample("Fetches", sample("FetchesPerMinute",
				(Long) attributes.get(Names.CollectionFetchCount) +
						(Long) attributes.get(Names.EntityFetchCount)));

		sample("Modifications", sample("ModificationsPerMinute",
				(Long) attributes.get(Names.CollectionRemoveCount) +
						(Long) attributes.get(Names.CollectionUpdateCount) +
						(Long) attributes.get(Names.EntityDeleteCount) +
						(Long) attributes.get(Names.EntityUpdateCount) +
						(Long) attributes.get(Names.EntityInsertCount)));
	}

	void refreshStatisticsTable() {
		applyAttributesToSamplers();
		applySamples(statisticsTable);
	}

	private Long sample(String name, Long value) {
		samplers.get(name).add(value);
		return value;
	}

	private void applySamples(DataTable table) {
		List<DataTable.Column> columns = table.getColumns();
		int i = 0;
		double[] values = new double[columns.size()];
		for (DataTable.Column column : columns)
			values[i++] = samplers.get(column.getName()).getDifference();
		table.add(values);
	}

	void refreshEntityStatistics() throws Exception {
		for (String name : getEntityNames())
			entityStatistics.put(name, proxyFor(EntityStatistics.class, getEntityStatisticsFor(name)));
	}

	void refreshCollectionStatistics() throws Exception {
		for (String name : getCollectionRoleNames())
			collectionStatistics.put(name, proxyFor(CollectionStatistics.class, getCollectionStatisticsFor(name)));
	}

	void refreshQueryStatistics() throws Exception {
		for (String name : getQueries())
			queryStatistics.put(name, proxyFor(QueryStatistics.class, getQueryStatisticsFor(name)));
	}

	void refreshCacheStatistics() throws Exception {
		for (String name : getCacheRegionNames())
			cacheStatistics.put(name, proxyFor(SecondLevelCacheStatistics.class, getCacheStatisticsFor(name)));
	}

	private String[] toNames(Names attribute) {
		String[] names = (String[]) attributes.get(attribute);
		return names == null ? new String[0] : names;
	}

	public MBeanServerConnection getConnection() {
		return connection;
	}

	public void setConnection(MBeanServerConnection connection) {
		this.connection = connection;
	}

	public DataTable getStatisticsTable() {
		return statisticsTable;
	}

	public String[] getEntityNames() {
		return toNames(Names.EntityNames);
	}

	public String[] getCollectionRoleNames() {
		return toNames(Names.CollectionRoleNames);
	}

	public String[] getQueries() {
		return toNames(Names.Queries);
	}

	public String[] getCacheRegionNames() {
		return toNames(Names.SecondLevelCacheRegionNames);
	}

	public Map<Names, Object> getAttributes() {
		return attributes;
	}

	public Map<String, EntityStatistics> getEntityStatistics() {
		return entityStatistics;
	}

	public Map<String, CollectionStatistics> getCollectionStatistics() {
		return collectionStatistics;
	}

	public Map<String, QueryStatistics> getQueryStatistics() {
		return queryStatistics;
	}

	public Map<String, SecondLevelCacheStatistics> getCacheStatistics() {
		return cacheStatistics;
	}
}
