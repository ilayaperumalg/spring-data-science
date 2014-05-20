package org.springframework.data.science.analytic;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Darimont
 */
public class DummyMappedAnalytic
		extends
		MappedAnalytic<Map<String, Object>, Map<String, Object>, Map<String, Object>, Map<String, Object>, DummyMappedAnalytic> {

	/**
	 * Creates a new {@link MappedAnalytic}.
	 * 
	 * @param inputMapper must not be {@literal null}.
	 * @param outputMapper must not be {@literal null}.
	 */
	public DummyMappedAnalytic(InputMapper<Map<String, Object>, DummyMappedAnalytic, Map<String, Object>> inputMapper,
			OutputMapper<Map<String, Object>, Map<String, Object>, DummyMappedAnalytic, Map<String, Object>> outputMapper) {
		super(inputMapper, outputMapper);
	}

	@Override
	protected Map<String, Object> evaluateInternal(Map<String, Object> modelInput) {

		Map<String, Object> output = new HashMap<>();
		for (Map.Entry<String, Object> entry : modelInput.entrySet()) {
			output.put(entry.getKey(), entry.getValue().toString().toUpperCase());
		}

		return output;
	}
}
