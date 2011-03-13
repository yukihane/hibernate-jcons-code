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
import net.sf.hibernate.jconsole.util.DataTable;

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

	public static final int HISTORY_LENGTH = Integer.getInteger("hibernate.history.length", 5000);

	private static final ServiceLoader<AbstractStatisticsContext> CONTEXT_LOADER =
			ServiceLoader.load(AbstractStatisticsContext.class, AbstractStatisticsContext.class.getClassLoader());

	/**
	 * Returns all enabled contexts that were found in the class loading environment.
	 *
	 * @return all enabled contexts that were found in the class loading environment.
	 */
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

	private Map<String, EntityStatistics> entityStatistics =
			new LinkedHashMap<String, EntityStatistics>();
	private Map<String, CollectionStatisticsTable> entityStatisticTables =
			new LinkedHashMap<String, CollectionStatisticsTable>();

	private Map<String, CollectionStatistics> collectionStatistics =
			new LinkedHashMap<String, CollectionStatistics>();
	private Map<String, CollectionStatisticsTable> collectionStatisticTables =
			new LinkedHashMap<String, CollectionStatisticsTable>();

	private Map<String, QueryStatistics> queryStatistics =
			new LinkedHashMap<String, QueryStatistics>();
	private Map<String, QueryStatistics> queryStatisticsTables =
			new LinkedHashMap<String, QueryStatistics>();

	private Map<String, SecondLevelCacheStatistics> cacheStatistics =
			new LinkedHashMap<String, SecondLevelCacheStatistics>();
	private Map<String, SecondLevelCacheStatistics> cacheStatisticsTables =
			new LinkedHashMap<String, SecondLevelCacheStatistics>();

	private List<AbstractStatisticsTable> statisticsTables = new ArrayList<AbstractStatisticsTable>();
	private PrimaryStatisticsTable statisticsTable;

	{
		statisticsTables.add(statisticsTable = new PrimaryStatisticsTable());
		statisticsTables.add(new PrimaryPerMinuteStatisticsTable());
	}

	/**
	 * Returns the entity statistics for the given name.
	 * <p/>
	 * Note: The returned instance must be a supported delegate in {@link #proxyFor(Class, Object)}.
	 *
	 * @param name the name of the entity.
	 * @return the entity statistics for the given name.
	 * @throws Exception in case of the operation failed.
	 */
	protected abstract Object getEntityStatisticsFor(String name) throws Exception;

	/**
	 * Returns the collection statistics for the given name.
	 * <p/>
	 * Note: The returned instance must be a supported delegate in {@link #proxyFor(Class, Object)}.
	 *
	 * @param name the name of the collection.
	 * @return the collection statistics for the given name.
	 * @throws Exception in case of the operation failed.
	 */
	protected abstract Object getCollectionStatisticsFor(String name) throws Exception;

	/**
	 * Returns the query statistics for the given query.
	 * <p/>
	 * Note: The returned instance must be a supported delegate in {@link #proxyFor(Class, Object)}.
	 *
	 * @param name the name of the query.
	 * @return the query statistics for the given query.
	 * @throws Exception in case of the operation failed.
	 */
	protected abstract Object getQueryStatisticsFor(String name) throws Exception;

	/**
	 * Returns the cache statistics for the given cache region.
	 * <p/>
	 * Note: The returned instance must be a supported delegate in {@link #proxyFor(Class, Object)}.
	 *
	 * @param cacheRegion the cache region of the entity.
	 * @return the cache statistics for the given cache region.
	 * @throws Exception in case of the operation failed.
	 */
	protected abstract Object getCacheStatisticsFor(String cacheRegion) throws Exception;

	/**
	 * Returns plain attributes.
	 *
	 * @param attributeNames the names of the attributes to return.
	 * @return the names mapped to the values.
	 * @throws Exception in case of the operation failed.
	 */
	protected abstract Map<Names, Object> getAttributes(List<Names> attributeNames) throws Exception;

	/**
	 * Returns true if the context is enabled.
	 * <p/>
	 * Note: The method must return true when no connection is present and the
	 * environment is enabled to run the context. When a connection is present
	 * the method must return 'false' if the connected application doesn't
	 * offer any statistics.
	 *
	 * @return true if the context is enabled.
	 */
	protected abstract boolean isEnabled();

	/**
	 * Creates a proxy around the given delegate, implementing the interface {@code T}.
	 *
	 * @param interfaceClass the interface to be implemented by the proxy class.
	 * @param delegate	   the delegate to direct the calls to.
	 * @param <T>            the interface type.
	 * @return a proxy around the given delegate, implementing the interface {@code T}.
	 */
	protected <T> T proxyFor(Class<T> interfaceClass, Object delegate) {
		if (delegate == null)
			delegate = new Object();
		return StraightNameMappingProxy.newInstance(interfaceClass, delegate, 0L);
	}

	/**
	 * Refreshes the statistics by recording the most recent data.
	 *
	 * @throws Exception in case of the operation fails for any reason.
	 */
	public synchronized void refresh() throws Exception {
		if (getConnection() == null)
			throw new IllegalStateException("Cannot call refresh when not connected.");

		refreshAttributes();
		refreshEntityStatistics();
		refreshCollectionStatistics();

		refreshQueryStatistics();
		refreshCacheStatistics();

		refreshTables();
	}

	void refreshTables() throws Exception {
		for (AbstractStatisticsTable table : statisticsTables)
			table.refresh(this);
		for (CollectionStatisticsTable table : collectionStatisticTables.values())
			table.refresh(this);
	}

	void refreshAttributes() throws Exception {
		for (Map.Entry<Names, Object> e : getAttributes(Names.getAllAttributes()).entrySet())
			this.attributes.put(e.getKey(), e.getValue());
	}

	void refreshEntityStatistics() throws Exception {
		for (String name : getEntityNames()) {
			entityStatistics.put(name, proxyFor(EntityStatistics.class, getEntityStatisticsFor(name)));
		}
	}

	void refreshCollectionStatistics() throws Exception {
		for (String name : getCollectionRoleNames()) {
			collectionStatistics.put(name, proxyFor(CollectionStatistics.class, getCollectionStatisticsFor(name)));
			if (!collectionStatisticTables.containsKey(name))
				collectionStatisticTables.put(name, new CollectionStatisticsTable(name));
		}
	}

	void refreshQueryStatistics() throws Exception {
		for (String name : getQueries()) {
			queryStatistics.put(name, proxyFor(QueryStatistics.class, getQueryStatisticsFor(name)));
		}
	}

	void refreshCacheStatistics() throws Exception {
		for (String name : getCacheRegionNames()) {
			cacheStatistics.put(name, proxyFor(SecondLevelCacheStatistics.class, getCacheStatisticsFor(name)));
		}
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

	public DataTable getStatisticsTable() {
		return statisticsTable;
	}

	public List<AbstractStatisticsTable> getStatisticsTables() {
		return statisticsTables;
	}

	public Map<String, EntityStatistics> getEntityStatistics() {
		return entityStatistics;
	}

	public Map<String, CollectionStatisticsTable> getEntityStatisticTables() {
		return entityStatisticTables;
	}

	public Map<String, CollectionStatistics> getCollectionStatistics() {
		return collectionStatistics;
	}

	public Map<String, CollectionStatisticsTable> getCollectionStatisticTables() {
		return collectionStatisticTables;
	}

	public Map<String, QueryStatistics> getQueryStatistics() {
		return queryStatistics;
	}

	public Map<String, QueryStatistics> getQueryStatisticsTables() {
		return queryStatisticsTables;
	}

	public Map<String, SecondLevelCacheStatistics> getCacheStatistics() {
		return cacheStatistics;
	}

	public Map<String, SecondLevelCacheStatistics> getCacheStatisticsTables() {
		return cacheStatisticsTables;
	}
}
