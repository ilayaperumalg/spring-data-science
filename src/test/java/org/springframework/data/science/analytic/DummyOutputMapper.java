package org.springframework.data.science.analytic;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Darimont
 */
public class DummyOutputMapper implements
		OutputMapper<Map<String, Object>, Map<String, Object>, DummyMappedAnalytic, Map<String, Object>> {

	/**
	 * Ordering of fields in the resulting {@link org.springframework.xd.tuple.Tuple} can be different than the ordering
	 * of the input {@code Tuple}.
	 * 
	 * @param analytic the {@link org.springframework.xd.analytics.ml.Analytic} that can be used to retrieve mapping
	 *          information.
	 * @param input the input for this {@link org.springframework.xd.analytics.ml.Analytic} that could be used to build
	 *          the model {@code O}.
	 * @param modelOutput the raw unmapped model output {@code MO}.
	 * @return
	 */
	@Override
	public Map<String, Object> mapOutput(DummyMappedAnalytic analytic, Map<String, Object> input,
			Map<String, Object> modelOutput) {
		return new HashMap<>(modelOutput);
	}

}
