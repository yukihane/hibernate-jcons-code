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

package net.sf.hibernate.jconsole.stats;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.Refreshable;
import net.sf.hibernate.jconsole.util.DataSampler;
import net.sf.hibernate.jconsole.util.DataTable;
import net.sf.hibernate.jconsole.util.FixedSizeDataSampler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines an abstract base to data tables that are fed by data samplers.
 *
 * @author Juergen_Kellerer, 2010-09-20
 * @version 1.0
 */
public abstract class AbstractStatisticsTable<N extends Number> extends DataTable implements Refreshable {

	private static final long serialVersionUID = 6918151294590826570L;

	/**
	 * Converts the given string names to columns with data flavour set to 'average'.
	 *
	 * @param names the strings to convert.
	 * @return the table column instances.
	 */
	public static Column[] toColumns(String... names) {
		Column[] columns = new Column[names.length];
		for (int i = 0; i < columns.length; i++)
			columns[i] = new Column(names[i], DataFlavour.average);
		return columns;
	}

	private Map<String, DataSampler<N>> samplers = new HashMap<String, DataSampler<N>>();

	/**
	 * Used for de-serialization support.
	 */
	protected AbstractStatisticsTable() {
		super();
	}

	/**
	 * Creates a new table with the specified columns.
	 *
	 * @param maxEntries the max entries recorded in this table.
	 * @param columns	the columns of the table.
	 */
	protected AbstractStatisticsTable(int maxEntries, String... columns) {
		this(maxEntries, toColumns(columns));
	}

	/**
	 * Creates a new table with the specified columns.
	 *
	 * @param maxEntries the max entries recorded in this table.
	 * @param columns	the columns of the table.
	 */
	protected AbstractStatisticsTable(int maxEntries, Column... columns) {
		super(maxEntries, columns);
		for (Column column : columns)
			samplers.put(column.getName(), createSampler(column));
	}

	/**
	 * Creates a data sampler for the specified column.
	 *
	 * @param column the column to create the sampler for.
	 * @return a data sampler for the specified column.
	 */
	protected DataSampler<N> createSampler(Column column) {
		return new FixedSizeDataSampler<N>(2);
	}

	/**
	 * Calculates the column value to add to the table on refresh.
	 *
	 * @param column  the column to get the value for.
	 * @param sampler the sampler containing the sampled values.
	 * @return the column value to add to the table on refresh.
	 */
	protected double calculateColumnValue(Column column, DataSampler<N> sampler) {
		return sampler.getDifference();
	}

	/**
	 * Returns a human readable ID used to identify the chart bound to the table.
	 *
	 * @return a human readable ID used to identify the chart bound to the table.
	 */
	public abstract String getId();

	/**
	 * Calculates the value to sample for the specified column.
	 *
	 * @param column  the column to sample the value for.
	 * @param context the context to read the data from.
	 * @return the value to sample for the specified column.
	 */
	protected abstract N calculateSampleValue(Column column, AbstractStatisticsContext context);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(AbstractStatisticsContext context) {
		applyAttributesToSamplers(context);
		applySamples();
	}

	void applyAttributesToSamplers(AbstractStatisticsContext context) {
		for (Column column : getColumns()) {
			N value = calculateSampleValue(column, context);
			samplers.get(column.getName()).add(value);
		}
	}

	void applySamples() {
		List<Column> columns = getColumns();

		int i = 0;
		double[] values = new double[columns.size()];
		for (DataTable.Column column : columns)
			values[i++] = calculateColumnValue(column, samplers.get(column.getName()));

		add(values);
	}
}
