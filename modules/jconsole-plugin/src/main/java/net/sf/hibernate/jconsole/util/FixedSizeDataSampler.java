package net.sf.hibernate.jconsole.util;

/**
 * Takes a defined amount of samples.
 *
 * @author Juergen_Kellerer, 2009-11-23
 * @version 1.0
 */
public class FixedSizeDataSampler<E extends Number> extends DataSampler<E> {

	private static final long serialVersionUID = 4470690846499493108L;

	private int maxSamples;

	/**
	 * Constructs a new fixed size sampler sampling "maxSamples".
	 *
	 * @param maxSamples the amount of samples to collect.
	 */
	public FixedSizeDataSampler(int maxSamples) {
		this.maxSamples = maxSamples;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E e) {
		while (size() >= maxSamples)
			removeFirst();
		return super.add(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addLast(E e) {
		add(e);
	}
}
