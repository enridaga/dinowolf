package dinowolf.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.Port;

public class FromToImpl implements FromTo {
	private static Map<String, Class<?>> types;

	static {
		types = new HashMap<String, Class<?>>();
		types.put("Input", InputProcessorPort.class);
		types.put("Output", OutputProcessorPort.class);
	}

	private Workflow workflow;
	private Processor processor;
	private Port from;
	private Port to;
	private String fromType = null;
	private String toType = null;
	private String id = null;

	public FromToImpl(Workflow w, Processor p, Port from, Port to) {
		this.workflow = w;
		this.processor = p;
		this.from = from;
		this.to = to;
		String[] s = new String[] { workflow.getName(), processor.getName(), "from:" + from.getName(),
				"to:" + to.getName() };
		id = StringUtils.join(s, '/');

		for (Entry<String, Class<?>> e : types.entrySet()) {
			if (e.getValue().isAssignableFrom(this.from.getClass())) {
				fromType = e.getKey();
			}
			if (e.getValue().isAssignableFrom(this.to.getClass())) {
				toType = e.getKey();
			}
		}
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
	public String roleFrom() {
		return fromType;
	}

	@Override
	public String roleTo() {
		return toType;
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
		return id;
	}

	@Override
	public String getId() {
		return id;
	};
}
