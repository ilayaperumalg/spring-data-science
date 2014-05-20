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

/**
 * Base interface for the definition of an analytic.
 * 
 * @author Thomas Darimont
 * @param <I> the input type
 * @param <O> the output type
 */
public interface Analytic<I, O> {

	/**
	 * Evaluates the encoded {@code Analytic} against the given {@code input}.
	 * 
	 * @param input must not be {@literal null}
	 * @return
	 */
	O evaluate(I input);
}