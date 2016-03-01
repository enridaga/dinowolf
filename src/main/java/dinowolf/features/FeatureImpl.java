package dinowolf.features;

public class FeatureImpl implements Feature {
	private boolean t;
	private String v;
	private String n;

	public FeatureImpl(String name, String value, boolean tokenizable) {
		t = tokenizable;
		n = name;
		v = value;
	}
	
	public FeatureImpl(String name, String value) {
		t = false;
		n = name;
		v = value;
	}

	@Override
	public String getName() {
		return n;
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
	
	public String toString(){
		return serialize();
	}
}
