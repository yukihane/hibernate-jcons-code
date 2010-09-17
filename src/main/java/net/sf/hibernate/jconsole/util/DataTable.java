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

import java.io.Serializable;
import java.util.*;

/**
 * Implements a fixed size data table that can be used to record timed data.
 * <p/>
 * Note: This implementation is rather low level and acts as the data backend for
 * the charts.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class DataTable extends AbstractCollection<DataTable.Row> implements Serializable {

	private static final long serialVersionUID = -4602723362334733326L;

	/**
	 * Specifies the type of data contained in a column.
	 */
	public enum DataFlavour {
		absolute,
		average,
		percentage,;

		double combine(List<Row> rows, Column column) {
			if (column.dataFlavour != this)
				throw new IllegalArgumentException("Invalid usage of combine!");

			double combinedValue = 0;
			for (Row row : rows)
				combinedValue += row.values[column.index];

			switch (this) {
				case average:
				case percentage:
					combinedValue /= rows.size();
					break;
			}

			return combinedValue;
		}
	}

	/**
	 * Defines a column inside the table.
	 */
	public static class Column implements Serializable {

		private static final long serialVersionUID = 8287642667472933997L;

		int index;
		String name;
		DataFlavour dataFlavour;

		public Column(String name, DataFlavour dataFlavour) {
			if (name == null || dataFlavour == null)
				throw new IllegalArgumentException("Name and dataFlavour must not be null.");
			this.name = name;
			this.dataFlavour = dataFlavour;
		}

		public String getName() {
			return name;
		}

		public int getIndex() {
			return index;
		}

		public DataFlavour getDataFlavour() {
			return dataFlavour;
		}
	}

	/**
	 * Stores the data of a single row.
	 */
	public static class Row implements Serializable {

		private static final long serialVersionUID = 470467266282223088L;

		double[] values;
		long timeStamp;

		private Row(long timeStamp, double[] values) {
			this.timeStamp = timeStamp;
			this.values = values;
		}

		public long getTimeStamp() {
			return timeStamp;
		}

		public double getValue(int columnIndex) {
			return values[columnIndex];
		}
	}

	private int maxEntries;
	private Column[] columns;
	private LinkedList<Row> rows = new LinkedList<Row>();

	/**
	 * Used for the serialization option.
	 */
	public DataTable() {
	}

	/**
	 * Used to construct a new fixed size data table.
	 *
	 * @param maxEntries The maximum entries inside the table.
	 * @param columns	The columns for this table.
	 */
	public DataTable(int maxEntries, Column... columns) {
		if (columns == null || columns.length == 0)
			throw new IllegalArgumentException("No columns were defined for this data table.");
		this.maxEntries = maxEntries;
		this.columns = columns;
		for (int i = 0; i < columns.length; i++)
			columns[i].index = i;
	}

	public Column getColumn(String name) {
		for (Column column : columns) {
			if (column.name.equals(name))
				return column;
		}
		return null;
	}

	public List<Column> getColumns() {
		return Arrays.asList(columns);
	}

	/**
	 * Returns all row values for the specified column.
	 *
	 * @param column The column to return the row values for.
	 * @return An array containing a copy of the row values.
	 */
	public double[] getColumnValues(Column column) {
		int i = 0;
		double[] values = new double[size()];
		for (Row row : rows)
			values[i++] = row.values[column.index];
		return values;
	}

	/**
	 * Returns the minimum timestamp in this data table.
	 *
	 * @return the minimum timestamp in this data table.
	 */
	public long getMinTimestamp() {
		return rows.isEmpty() ? -1 : rows.getFirst().getTimeStamp();
	}

	/**
	 * Returns the maximum timestamp in this data table.
	 *
	 * @return the maximum timestamp in this data table.
	 */
	public long getMaxTimestamp() {
		return rows.isEmpty() ? -1 : rows.getLast().getTimeStamp();
	}

	/**
	 * Returns the minmum value in this data table.
	 *
	 * @return the minmum value in this data table or Double.MAX_VALUE if the table is empty.
	 */
	public double getMinValue() {
		double v = Double.MAX_VALUE;
		for (Row row : rows) {
			for (double value : row.values)
				if (value < v)
					v = value;
		}
		return v;
	}

	/**
	 * Returns the maximum value in this data table.
	 *
	 * @return the maximum value in this data table or Long.MIN_VALUE if the table is empty.
	 */
	public double getMaxValue() {
		double v = Long.MIN_VALUE;
		for (Row row : rows) {
			for (double value : row.values)
				if (value > v)
					v = value;
		}
		return v;
	}

	/**
	 * Returns a new DataTable that contains only elements of the specified time range.
	 *
	 * @param startTime The start time (inclusive)
	 * @param endTime   The end time (inclusive)
	 * @return A new instance of data table sharing the matching rows and columns.
	 */
	public DataTable createRange(long startTime, long endTime) {
		DataTable range = new DataTable(maxEntries, columns);
		for (Row row : rows) {
			if (row.timeStamp >= startTime || row.timeStamp <= endTime)
				range.rows.add(row);
		}
		return range;
	}

	/**
	 * Returns a new DataTable that is shrinked to the new given size.
	 * <p/>
	 * All rows inside the source table are re-caclulated based on the given data flavour.
	 *
	 * @return A new instance of data table having the given size.
	 * @param	newSize	The new size of the resulting data table.
	 */
	@SuppressWarnings("unchecked")
	public DataTable shrinkToSize(int newSize) {
		if (newSize >= rows.size())
			return this;

		DataTable shrinked = new DataTable(newSize, columns);
		int combineCount = Math.max(1, (int) Math.ceil((double) rows.size() / (double) newSize));
		List<Row> rowPackage = new ArrayList<Row>(combineCount);
		for (Row row : rows) {
			rowPackage.add(row);
			if (rowPackage.size() == combineCount) {
				shrinked.rows.add(newCombinedRow(rowPackage));
				rowPackage.clear();
			}
		}
		if (!rowPackage.isEmpty())
			shrinked.rows.add(newCombinedRow(rowPackage));
		return shrinked;
	}

	private Row newCombinedRow(List<Row> rows) {
		if (rows == null || rows.isEmpty())
			return null;
		double[] values = new double[columns.length];
		for (int i = 0; i < values.length; i++) {
			Column c = columns[i];
			values[i] = c.dataFlavour.combine(rows, c);
		}
		return new Row(rows.get(0).getTimeStamp(), values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Row> iterator() {
		return rows.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return rows.size();
	}

	/**
	 * Adds a new row to the end of this data table.
	 *
	 * @param rowValues The row values to add.
	 * @return True if the rows were added.
	 */
	public boolean add(double... rowValues) {
		return add(System.currentTimeMillis(), rowValues);
	}

	/**
	 * Adds a new row to the end of this data table.
	 *
	 * @param timeStamp The timestamp of the local system time in MS when the row was sampled.
	 * @param rowValues The row values to add.
	 * @return True if the rows were added.
	 */
	public boolean add(long timeStamp, double... rowValues) {
		if (rowValues == null || rowValues.length != columns.length)
			throw new IllegalArgumentException("Row values may not be empty and must match the column count.");
		while (rows.size() >= maxEntries)
			rows.removeFirst();
		return rows.add(new Row(timeStamp, rowValues));
	}
}
