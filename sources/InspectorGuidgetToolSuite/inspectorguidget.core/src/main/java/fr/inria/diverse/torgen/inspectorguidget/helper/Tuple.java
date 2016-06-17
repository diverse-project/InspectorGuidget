package fr.inria.diverse.torgen.inspectorguidget.helper;

public class Tuple<A, B> {
	public final A a;
	public final B b;

	public Tuple(final A a, final B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public String toString() {
		return "Tuple{" +
			"a=" + a +
			", b=" + b +
			'}';
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}
}
