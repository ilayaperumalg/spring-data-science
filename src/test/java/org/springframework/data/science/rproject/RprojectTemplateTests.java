/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.science.rproject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Thomas Darimont
 */
public class RprojectTemplateTests {

	RprojectOperations template;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		template = new RprojectTemplate(new RprojectCliConnectionFactory());
	}

	@Test
	public void testExecuteSimpleExpression() {

		assertThat(template.eval("1+1", Integer.class), is((Object) 2));
		assertThat(template.eval("3+1*2", Integer.class), is((Object) 5));
		assertThat(template.eval("(3+1)*2", Integer.class), is((Object) 8));
	}

	@Test
	public void testExecuteMultipleExpressions() {

		assertThat(template.eval("a=1+2;a*2", Integer.class), is((Object) 6));
	}

	@Test
	public void testExecuteSimpleFunctionExpression() {

		assertThat(template.eval("log(10)", Double.class), is((Object) 2.302585));
	}

	@Test
	public void testExecuteScript() {

		assertThat(template.evalScript("classpath:models/r/simple-script-1.r", Integer.class), is((Object) 42));
	}

	@Test
	public void shouldThrowRprojectExceptionForUnknownCommand() {

		expectedException.expect(RprojectException.class);
		expectedException.expectMessage("could not find function \"lug\"");

		assertThat(template.eval("lug(10)", Double.class), is((Object) 2.302585));
	}

	@Test
	public void testExecuteExpressionInContextOfInitScript() throws Exception {

		String result = template.evalInScriptContext("classpath:models/r/simple-linear-regression-4-iris-model.r",
				"rebuildModel(\"model-name-bubu\",\"model-id-42\")", String.class);

		assertThat(result, is(notNullValue()));
		assertThat(result, CoreMatchers.containsString("<PMML version="));
		assertThat(result, CoreMatchers.containsString("model-id-42"));
	}

	@Test
	public void testExecuteExpressionInContextOfInitScriptWithCallback() throws Exception {

		String result = template.execute(new RprojectCallback<String>() {

			@Override
			public String doInR(RprojectConnection connection) throws Exception {
				return connection.evalInScriptContext(
						"classpath:models/r/simple-linear-regression-4-iris-model.r",
						"rebuildModel(\"model-name-bubu\",\"model-id-42\")",
						String.class);
			}
		});

		assertThat(result, is(notNullValue()));
		assertThat(result, CoreMatchers.containsString("<PMML version="));
		assertThat(result, CoreMatchers.containsString("model-id-42"));
	}
}
