package org.triggertracker;

public class Tuple<X, Y> {
	private final X first;
	private final Y second;
	private transient final int hash;

	public Tuple(X x, Y y) {
		this.first = x;
		this.second = y;
		hash = (first == null ? 0 : first.hashCode() * 31)
				+ (second == null ? 0 : second.hashCode());
	}

	public X getFirst() {
		return first;
	}

	public Y getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object oth) {
		if (this == oth) {
			return true;
		}
		if (oth == null || !(getClass().isInstance(oth))) {
			return false;
		}
		Tuple<X, Y> other = getClass().cast(oth);
		return (first == null ? other.first == null : first.equals(other.first))
				&& (second == null ? other.second == null : second
						.equals(other.second));
	}
}
