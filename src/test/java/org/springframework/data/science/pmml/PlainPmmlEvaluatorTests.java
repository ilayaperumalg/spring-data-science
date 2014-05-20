package org.springframework.data.science.pmml;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.SAXSource;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.manager.PMMLManager;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.junit.Test;
import org.xml.sax.InputSource;

public class PlainPmmlEvaluatorTests {

	@Test
	public void testNaiveBayesClassifier() throws Exception {

		assertPmmlModelProducesExpectedOutput("./models/pmml/iris-flower-classification-naive-bayes-1.pmml.xml",
				new HashMap<String, Object>() {
					{
						put("Sepal.Length", 6.4);
						put("Sepal.Width", 3.2);
						put("Petal.Length", 4.5);
						put("Petal.Width", 1.5);
					}
				}, "versicolor");

		assertPmmlModelProducesExpectedOutput("./models/pmml/iris-flower-classification-naive-bayes-1.pmml.xml",
				new HashMap<String, Object>() {
					{
						put("Sepal.Length", 6.9);
						put("Sepal.Width", 3.1);
						put("Petal.Length", 5.4);
						put("Petal.Width", 2.1);
					}
				}, "virginica");
	}

	private void assertPmmlModelProducesExpectedOutput(String pmmlModelPath, Map<String, Object> input,
			Object expectedOutput) throws Exception {

		// Load the PMML definition form the given path
		InputSource pmmlSource = new InputSource(getClass().getClassLoader().getResourceAsStream(pmmlModelPath));

		// Clean up version references ... to circumvent potential PMML version incompatibilities.
		SAXSource transformedSource = ImportFilter.apply(pmmlSource);

		// Build up the actual PMML object structure.
		PMML pmml = JAXBUtil.unmarshalPMML(transformedSource);

		PMMLManager pmmlManager = new PMMLManager(pmml);

		// Create a model evaluator -> this is used for the "scoring"
		ModelEvaluator<?> modelEvaluator = (ModelEvaluator<?>) pmmlManager.getModelManager(null,
				ModelEvaluatorFactory.getInstance());

		Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();

		// Active fields are the list of "input variables".
		List<FieldName> activeFields = modelEvaluator.getActiveFields();

		for (FieldName activeField : activeFields) {

			// The raw (ie. user-supplied) value could be any Java primitive value
			Object rawValue = input.get(activeField.getValue());

			// The raw value is passed through: 1) outlier treatment, 2) missing value treatment, 3) invalid value treatment
			// and 4) type conversion
			FieldValue activeValue = modelEvaluator.prepare(activeField, rawValue);

			arguments.put(activeField, activeValue);
		}

		// We perform the actual evaluation
		Map<FieldName, ?> results = modelEvaluator.evaluate(arguments);

		// extract the value (e.g. the predicted value) from the result
		FieldName targetName = modelEvaluator.getTargetField();
		Object targetValue = results.get(targetName);

		assertThat(targetValue, is((Object) expectedOutput));
	}
}
