package org.springframework.data.science.analytic;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Darimont
 */
public class DummyInputMapper implements InputMapper<Map<String, Object>, DummyMappedAnalytic, Map<String, Object>> {

	@Override
	public Map<String, Object> mapInput(DummyMappedAnalytic analytic, Map<String, Object> input) {

		Map<String, Object> newInput = new HashMap<>();

		for (String fieldName : input.keySet()) {
			newInput.put(fieldName, input.get(fieldName));
		}

		return newInput;
	}
}
