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

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Extends data sampler with timeboxing support.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class TimeboxedDataSampler<E extends Number> extends DataSampler<E> {

	private static final long serialVersionUID = 3450387935184516437L;

	private long timeboxLength;
	private Map<E, Long> timeStamps = new IdentityHashMap<E, Long>();

	/**
	 * Creates a new timeboxed data sampler.
	 *
	 * @param timeboxLength The length of the timebox in ms.
	 */
	public TimeboxedDataSampler(long timeboxLength) {
		super();
		this.timeboxLength = timeboxLength;
	}

	@Override
	public boolean add(E e) {
		long time = System.currentTimeMillis();

		boolean removed;
		do {
			removed = false;
			E first = isEmpty() ? null : getFirst();
			Long firstTime = timeStamps.get(first);

			if (firstTime != null && firstTime < time - timeboxLength) {
				timeStamps.remove(first);
				super.removeFirst();
				removed = true;
			}
		} while (removed);

		timeStamps.put(e, time);
		return super.add(e);
	}

	@Override
	public void clear() {
		timeStamps.clear();
		super.clear();
	}

	@Override
	public void addLast(E e) {
		add(e);
	}

	// Disable most standard functions.

	@Override
	public void addFirst(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void push(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offerLast(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offerFirst(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E removeFirst() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E removeLast() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E poll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E pop() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}
}
