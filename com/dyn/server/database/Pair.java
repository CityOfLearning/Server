package com.dyn.server.database;

public class Pair<A, B> {
	private A first;
	private B second;

	public Pair(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair otherPair = (Pair) other;
			return (((this.first == otherPair.first)
					|| ((this.first != null) && (otherPair.first != null) && this.first.equals(otherPair.first)))
					&& ((this.second == otherPair.second) || ((this.second != null) && (otherPair.second != null)
							&& this.second.equals(otherPair.second))));
		}

		return false;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;

		return ((hashFirst + hashSecond) * hashSecond) + hashFirst;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}