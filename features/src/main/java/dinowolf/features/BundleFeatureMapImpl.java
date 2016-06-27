package dinowolf.features;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import dinowolf.annotation.FromTo;

public class BundleFeatureMapImpl implements BundleFeaturesMap {

	private FeaturesMap map;
	private WorkflowBundle wf;

	public BundleFeatureMapImpl(WorkflowBundle wf, FeaturesMap features) {
		this.wf = wf;
		this.map = features;
	}

	@Override
	public Set<FromTo> getPortPairs() {
		return map.getPortPairs();
	}

	@Override
	public FeatureSet getFeatures(FromTo portPair) {
		return map.getFeatures(portPair);
	}
	
	@Override
	public FeatureSet allFeatures() {
		return map.allFeatures();
	}

	@Override
	public WorkflowBundle getWorkflowBundle() {
		return wf;
	}

	@Override
	public FeatureSet put(FromTo key, FeatureSet value) {
		return map.put(key, value);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public FeatureSet get(Object key) {
		return map.get(key);
	}

	@Override
	public FeatureSet remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends FromTo, ? extends FeatureSet> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<FromTo> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<FeatureSet> values() {
		return map.values();
	}

	@Override
	public Set<java.util.Map.Entry<FromTo, FeatureSet>> entrySet() {
		return map.entrySet();
	}
}
