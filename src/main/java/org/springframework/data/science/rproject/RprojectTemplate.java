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

/**
 * @author Thomas Darimont
 */
public class RprojectTemplate implements RprojectOperations {

	private final RprojectConnectionFactory connectionFactory;

	private final RprojectExceptionTranslator exceptionTranslator;

	public RprojectTemplate(RprojectConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		this.exceptionTranslator = new DefaultRprojectExceptionTranslator();
	}

	@Override
	public <T> T execute(RprojectCallback<T> rprojectCallback) {
		try {
			return rprojectCallback.doInR(connectionFactory.getConnection());
		}
		catch (Exception ex) {
			throw exceptionTranslator.translate(ex, null);
		}
	}

	@Override
	public <T> T eval(final String expression, final Class<T> resultType) {

		return execute(new RprojectCallback<T>() {

			@Override
			public T doInR(RprojectConnection connection) {
				return connection.eval(expression, resultType);
			}
		});
	}

	@Override
	public <T> T evalScript(final String scriptLocation, final Class<T> resultType) {

		return execute(new RprojectCallback<T>() {

			@Override
			public T doInR(RprojectConnection connection) {
				return connection.evalInScriptContext(scriptLocation, " ", resultType);
			}
		});
	}

	/**
	 * @param string
	 * @param string2
	 * @param class1
	 * @return
	 */
	public <T> T evalInScriptContext(final String scriptLocation, final String expression, final Class<T> resultType) {

		return execute(new RprojectCallback<T>() {

			@Override
			public T doInR(RprojectConnection connection) throws Exception {
				return connection.evalInScriptContext(
						scriptLocation,
						expression,
						resultType);
			}
		});
	}
}
