/*
 * Copyright (c) 2009
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

package net.sf.hibernate.jconsole.util;

import java.util.LinkedList;

/**
 * Implements a simple sampler for number values.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class DataSampler<E extends Number> extends LinkedList<E> {

	private static final long serialVersionUID = 114661085411237384L;

	/**
	 * Returns the difference between the first and the last entry.
	 *
	 * @return the difference between the first and the last entry.
	 */
	public double getDifference() {
		return getLast().doubleValue() - getFirst().doubleValue();
	}

	/**
	 * Returns the average value of all samples.
	 *
	 * @return the average value of all samples.
	 */
	public double getAverage() {
		return getSum() / size();
	}

	/**
	 * Returns the sum of all samples.
	 *
	 * @return the sum of all samples.
	 */
	public double getSum() {
		double sum = 0;
		for (E e : this)
			sum += e.doubleValue();
		return sum;
	}
}
