package dinowolf.features;

public class DerivedFeature extends FeatureImpl {

	private Feature from;

	public Feature derivedFrom() {
		return from;
	}

	public DerivedFeature(Feature from, String suffix, String value) {
		super(from.getName() + "-" + suffix, value, from.getLevel());
		this.from = from;

	}
}
