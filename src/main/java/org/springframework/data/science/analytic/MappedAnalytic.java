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
package org.springframework.data.science.analytic;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@link Analytic} that supports the mapping of {@code input} and {@code output} to and from internal
 * representations by applying the given {@link InputMapper} and {@link OutputMapper} respectively.
 * 
 * @author Thomas Darimont
 * @param <I> the input type
 * @param <O> the output type
 * @param <MI> the model input type
 * @param <MO> the model output type
 * @param <A> the analytic model type
 */
public abstract class MappedAnalytic<I, O, MI, MO, A extends MappedAnalytic<I, O, MI, MO, A>> implements Analytic<I, O> {

	private final InputMapper<I, A, MI> inputMapper;

	private final OutputMapper<I, O, A, MO> outputMapper;

	/**
	 * Creates a new {@link MappedAnalytic}.
	 * 
	 * @param inputMapper must not be {@literal null}.
	 * @param outputMapper must not be {@literal null}.
	 */
	public MappedAnalytic(InputMapper<I, A, MI> inputMapper, OutputMapper<I, O, A, MO> outputMapper) {

		this.inputMapper = checkNotNull(inputMapper, "inputMapper");
		this.outputMapper = checkNotNull(outputMapper, "outputMapper");
	}

	/**
	 * Evaluates the encoded {@code Analytic} against the given {@code input}. The {@code inputMapper} is used to map the
	 * given {@code input} to an appropriate input for the internal evaluation of the {@code Analytic}. The resulting
	 * output of internal evaluation is mapped via the {@code outputMapper} to an appropriate result.
	 * 
	 * @param input must not be {@literal null}
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public O evaluate(I input) {

		MI modelInput = inputMapper.mapInput((A) this, checkNotNull(input, "input"));

		MO modelOutput = evaluateInternal(modelInput);

		O output = outputMapper.mapOutput((A) this, input, modelOutput);

		return output;
	}

	/**
	 * Performs the actual evaluation with the mapped {@code input} values.
	 * 
	 * @param modelInput must not be {@iteral null}
	 * @return the unmapped actual output of the internal evaluation.
	 */
	protected abstract MO evaluateInternal(MI modelInput);
}
