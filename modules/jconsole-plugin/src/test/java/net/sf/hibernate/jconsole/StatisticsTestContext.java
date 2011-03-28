package net.sf.hibernate.jconsole;/*
 * Copyright (c) 2011
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

import net.sf.hibernate.jconsole.stats.*;
import org.mockito.Mockito;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sf.hibernate.jconsole.stats.Names.*;

/**
 * Implements AbstractStatisticsContext for unit testing.
 *
 * @author juergen, 28.03.11
 */
public class StatisticsTestContext extends AbstractStatisticsContext {

	private static final long serialVersionUID = -7823070792158435643L;

	protected EntityStatistics entityStatistics = Mockito.mock(EntityStatistics.class);
	protected CollectionStatistics collectionStatistics = Mockito.mock(CollectionStatistics.class);
	protected QueryStatistics queryStatistics = Mockito.mock(QueryStatistics.class);
	protected SecondLevelCacheStatistics cacheStatistics = Mockito.mock(SecondLevelCacheStatistics.class);
	protected Map<Names, Object> attributes = new HashMap<Names, Object>();

	{
		for (Names names : values())
			attributes.put(names, 0L);
		for (Names names : EnumSet.of(EntityNames, CollectionRoleNames, Queries, SecondLevelCacheRegionNames))
			attributes.put(names, new String[]{"dummy"});
	}

	@Override
	protected Object getEntityStatisticsFor(String name) throws Exception {
		return entityStatistics;
	}

	@Override
	protected Object getCollectionStatisticsFor(String name) throws Exception {
		return collectionStatistics;
	}

	@Override
	protected Object getQueryStatisticsFor(String name) throws Exception {
		return queryStatistics;
	}

	@Override
	protected Object getCacheStatisticsFor(String cacheRegion) throws Exception {
		return cacheStatistics;
	}

	@Override
	protected Map<Names, Object> getAttributes(List<Names> attributeNames) throws Exception {
		return attributes;
	}

	@Override
	protected boolean isEnabled() {
		return true;
	}
}
