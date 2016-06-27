package dinowolf.features;

public interface Feature {
	public void setName(String name);

	public void setValue(String value);

	public String getName();

	public String getId();

	public String getValue();

	public String serialize();

	/**
	 * Does it make sense to convert the value of this feature into a bag of
	 * words?
	 * 
	 * @return
	 */
	public boolean isTokenizable();

	/**
	 * Is this a feature of what workflow component?
	 * 
	 * @return
	 */
	public FeatureDepth getLevel();
}
