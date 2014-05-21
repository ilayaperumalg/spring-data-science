/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.science.rproject;

import java.util.Objects;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;


/**
 *
 * @author Thomas Darimont
 */
public abstract class AbstractRprojectConnection implements RprojectConnection {

	private ConversionService conversionService = new DefaultConversionService();

	@Override
	public <T> T eval(String expression, Class<T> resultType) {

		Assert.hasText(expression, "Expression must not be null or empty!");

		return evalInScriptContext("", expression, resultType);
	}

	@Override
	public <T> T evalInScriptContext(String initScriptPath, String expression, Class<T> resultType) {


		RprojectResult result = doEvalInScriptContext(initScriptPath, expression);

		if (result.isOk()) {
			return conversionService.convert(result.getOutput(), resultType);
		}

		throw new RuntimeException(Objects.toString(result.getOutput()), result.getException());
	}

	protected abstract RprojectResult doEvalInScriptContext(String initScriptPath, String expression);

}
