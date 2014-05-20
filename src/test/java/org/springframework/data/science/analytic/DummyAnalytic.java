package org.springframework.data.science.analytic;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author Thomas Darimont
 */
public class DummyAnalytic implements Analytic<Map<String, Object>, Map<String, Object>> {

	@Override
	public Map<String, Object> evaluate(Map<String, Object> input) {

		Map<String, Object> output = Maps.newHashMap(input);
		output.put("k3", "v3");

		return output;
	}
}
