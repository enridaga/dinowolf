package dinowolf.features;

public interface Feature {
	public void setName(String name);
	public void setValue(String value);
	public String getName();
	public String getId();
	public String getValue();
	public String serialize();
	public boolean isTokenizable();
	public FeatureLevel getLevel();
}
