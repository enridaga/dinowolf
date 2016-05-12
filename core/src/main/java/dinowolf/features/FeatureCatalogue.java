package dinowolf.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public final class FeatureCatalogue {
	private Feature __f(String name, String value, FeatureDepth level, boolean t) {
		return new FeatureImpl(name, value, level, t);
	}

	private Feature __f(String name, String value, FeatureDepth level) {
		return new FeatureImpl(name, value, level);
	}

	@SuppressWarnings("unused")
	private Feature __workflow(String name, String value) {
		return __f(name, value, FeatureDepth.Workflow);
	}

	private Feature __processor(String name, String value) {
		return __f(name, value, FeatureDepth.Processor);
	}

	@SuppressWarnings("unused")
	private Feature __singleport(String name, String value) {
		return __f(name, value, FeatureDepth.SinglePort);
	}

	@SuppressWarnings("unused")
	private Feature __portpair(String name, String value) {
		return __f(name, value, FeatureDepth.FromToPorts);
	}

	@SuppressWarnings("unused")
	private Feature __workflow(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.Workflow, t);
	}

	private Feature __processor(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.Processor, t);
	}

	private Feature __activity(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.Activity, t);
	}

	private Feature __activity(String name, String value) {
		return __f(name, value, FeatureDepth.Activity);
	}

	private Feature __singleport(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.SinglePort, t);
	}

	private Feature __portpair(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.FromToPorts, t);
	}

	private Feature __annotation(String property, String value, FeatureDepth ofwhat, boolean tokenizable) {
		return __f(property, value, ofwhat, tokenizable);
	}

	/*
	 * CATALOGUE
	 * 
	 */

	public Feature ProcessorName(String value) {
		return __processor("ProcessorName", value, true);
	}

	public Feature ProcessorType(String value) {
		return __processor("ProcessorType", value);
	}

	public Feature ActivityName(String value) {
		return __activity("ActivityName", value, true);
	}

	public Feature ActivityType(String value) {
		return __activity("ActivityType", value);
	}

	public Feature ActivityConfField(List<String> path) {
		return __activity("ActivityConfField", StringUtils.join(path.toArray(new String[path.size()]), "/"));
	}

	public Feature ActivityConfFieldValue(List<String> path, String value) {
		List<String> l = new ArrayList<String>();
		l.add("ActivityConfField");
		l.addAll(path);
		return __activity(StringUtils.join(l, '/'), value);
	}

	public Feature InputPortName(String value) {
		return __singleport("InputPortName", value, true);
	}

	public Feature OutputPortName(String value) {
		return __singleport("OutputPortName", value, true);
	}

	public Feature FromPort(String value) {
		return __portpair("FromPort", value, true);
	}

	public Feature ToPort(String value) {
		return __portpair("ToPort", value, true);
	}

	public Feature FromToRoles(String value) {
		return __portpair("FromToRoles", value, true);
	}

	public Feature Annotation(String property, String value, FeatureDepth ofwhat, boolean tokenizable) {
		return __annotation(property, value, ofwhat, tokenizable);
	}

	public Feature Title(String value, FeatureDepth ofwhat){
		return Annotation("Title", value, ofwhat, true);
	}
	
	public Feature Description(String value, FeatureDepth ofwhat){
		return Annotation("Description", value, ofwhat, true);
	}

	public Feature ProcessorTitle(String value){
		return Annotation("Title", value, FeatureDepth.Processor, true);
	}
	
	public Feature ProcessorDescription(String value){
		return Annotation("Description", value, FeatureDepth.Processor, true);
	}
	
	public Feature ActivityTitle(String value){
		return Annotation("Title", value, FeatureDepth.Processor, true);
	}
	
	public Feature ActivityDescription(String value){
		return Annotation("Description", value, FeatureDepth.Processor, true);
	}

	public Feature OtherPortTitle(String value){
		return Annotation("Title", value, FeatureDepth.SinglePort, true);
	}
	
	public Feature OtherPortDescription(String value){
		return Annotation("Description", value, FeatureDepth.SinglePort, true);
	}
	
	public Feature FromPortTitle(String value){
		return Annotation("Title", value, FeatureDepth.From, true);
	}
	
	public Feature FromPortDescription(String value){
		return Annotation("Description", value, FeatureDepth.From, true);
	}
	
	public Feature ToPortTitle(String value){
		return Annotation("Title", value, FeatureDepth.To, true);
	}
	
	public Feature ToPortDescription(String value){
		return Annotation("Description", value, FeatureDepth.To, true);
	}
	
}
