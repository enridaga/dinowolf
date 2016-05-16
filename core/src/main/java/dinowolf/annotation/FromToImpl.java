package dinowolf.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.Port;

public class FromToImpl implements FromTo {
	private static Map<String, Class<?>> types;

	static {
		types = new HashMap<String, Class<?>>();
		types.put("I", InputProcessorPort.class);
		types.put("O", OutputProcessorPort.class);
	}

	private Workflow workflow;
	private Processor processor;
	private Port from;
	private Port to;
	private String roleFrom = null;
	private String roleTo = null;
	private String id = null;
	private int hashCode;
	private String shortName;
	private FromToType type;

	/**
	 * 
	 * @param w
	 * @param p
	 * @param from
	 * @param to
	 */
	public FromToImpl(String bundleId, Workflow w, Processor p, Port from, Port to) {
		
		this.workflow = w;
		this.processor = p;
		this.from = from;
		this.to = to;
		for (Entry<String, Class<?>> e : types.entrySet()) {
			if (e.getValue().isAssignableFrom(this.from.getClass())) {
				roleFrom = e.getKey();
			}
			if (e.getValue().isAssignableFrom(this.to.getClass())) {
				roleTo = e.getKey();
			}
		}

		this.type = FromToType.valueOf(roleFrom + roleTo);
		this.shortName = new StringBuilder().append(processor.getName()).append("/").append(roleFrom).append(roleTo).append(':').append(from.getName()).append(":")
				.append(to.getName()).toString();
		String[] s = new String[] {bundleId, workflow.getName(), shortName };
		id = StringUtils.join(s, '/');
		this.hashCode = new HashCodeBuilder().append(FromTo.class).append(id).toHashCode();
		
	}

	@Override
	public Port from() {
		return from;
	}

	@Override
	public Port to() {
		return to;
	}

	@Override
	public String shortName() {
		return shortName;
	}

	@Override
	public String roleFrom() {
		return roleFrom;
	}

	@Override
	public FromToType getType() {
		return type;
	}
	
	@Override
	public String roleTo() {
		return roleTo;
	}

	@Override
	public Processor processor() {
		return processor;
	}

	@Override
	public Workflow workflow() {
		return workflow;
	}

	public String toString() {
		return shortName;
	}

	@Override
	public String getId() {
		return id;
	};

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FromTo){
			return ((FromTo) obj).getId().equals(getId());
		}
		return false;
	}
}
