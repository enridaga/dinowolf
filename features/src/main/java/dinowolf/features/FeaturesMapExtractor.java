package dinowolf.features;

import java.io.IOException;
import java.util.Arrays;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;

public class FeaturesMapExtractor  {
	private static final Logger l = LoggerFactory.getLogger(FeaturesMapExtractor.class);

	// XXX order is important
	private static final FeaturesExtractor[] E = new FeaturesExtractor[]{new StandardFeaturesExtractor(), new BagOfWordsExtractor()};
	private static final FromToCollector C = new FromToCollector();

	public final static BundleFeaturesMap generate(String bundleId, WorkflowBundle bundle, FromTo.FromToType... types) throws IOException {
		return new BundleFeatureMapImpl(bundle, extract(bundleId, bundle, types));
	}

	public final static FeaturesHashMap extract(String bundleId, WorkflowBundle bundle, FromTo.FromToType... types) throws IOException {
		FeaturesHashMap featuresMap = new FeaturesHashMap();
		for (FromTo io : C.getList(bundleId, bundle)) {
			if (types.length > 0) {
				if(!Arrays.asList(types).contains(io.getType())){
					continue;
				}
			}
			for(FeaturesExtractor f : E){
				
				if(!featuresMap.containsKey(io)){
					FeatureSet fff = f.extract(io);
					l.trace("{}: {} features extracted", io.getId(), fff.size());
					featuresMap.put(io, fff);
				}else{
					FeatureSet fff = f.extract(io, featuresMap.get(io));
					l.trace("{}: {} features appended", io.getId(), fff.size());
					featuresMap.get(io).addAll(fff);
				}
			}
			
		}

		return featuresMap;
	}
}
