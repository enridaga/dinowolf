package dinowolf.features;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FeatureImpl implements Feature {
	private boolean t;
	private String v;
	private String n;
	private FeatureLevel l;
	private int hashCode;

	public FeatureImpl(String name, String value, FeatureLevel level, boolean tokenizable) {
		t = tokenizable;
		n = name;
		v = value;
		l = level;
		hashCode = new HashCodeBuilder(17, 37).append(Feature.class).append(t).append(v).append(v).append(l)
				.toHashCode();
	}

	public FeatureImpl(String name, String value, FeatureLevel level) {
		this(name, value, level, false);
	}

	@Override
	public String getName() {
		return n;
	}

	@Override
	public String getId() {
		return Integer.toString(hashCode);
	}

	@Override
	public String getValue() {
		return v;
	}

	@Override
	public void setName(String name) {
		n = name;
	}

	@Override
	public void setValue(String value) {
		v = value;
	}

	@Override
	public boolean isTokenizable() {
		return t;
	}

	@Override
	public String serialize() {
		return new StringBuilder().append(n).append(':').append(' ').append(v).toString();
	}

	public String toString() {
		return serialize();
	}

	@Override
	public FeatureLevel getLevel() {
		return l;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Feature) {
			return obj.hashCode() == this.hashCode();
		}
		return false;
	}
}
