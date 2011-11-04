/*
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

package net.sf.hibernate.jconsole.stats;

/**
 * Statistics related utilities.
 *
 * @author juergen, 27.03.11
 */
public final class StatisticsUtil {

	public static double toRatio(long leftValue, long rightValue) {
		if (leftValue == 0 && rightValue == 0)
			return 0;
		return (double) leftValue / (double) (rightValue + leftValue);
	}

	public static double toTotalAverageTime(QueryStatistics statistics) {
		return (double) statistics.getExecutionCount() * toAverageExecutionTime(statistics);
	}

	public static double toAverageExecutionTime(QueryStatistics statistics) {
		return statistics.getExecutionAvgTime() == 0 ? 0.001D : statistics.getExecutionAvgTime();
	}

	public static double toQueryPerformance(QueryStatistics statistics) {
		return toQueryPerformance(toTotalAverageTime(statistics), statistics.getCacheHitCount() + statistics.getExecutionCount());
	}

	public static double toQueryPerformance(double totalTimeOnDb, double invocations) {
		return totalTimeOnDb / invocations;
	}

	private StatisticsUtil() {
	}
}
