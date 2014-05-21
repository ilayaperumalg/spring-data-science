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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * @author Thomas Darimont
 */
public class RprojectCliConnection extends AbstractRprojectConnection {

	@Override
	protected RprojectResult doEvalInScriptContext(String initScriptPath, String expression) {

		RScriptCommandLine cmd = new RScriptCommandLine(initScriptPath, expression);

		try {
			cmd.execute();
			return new RprojectResult(cmd.getCommandOutput());
		}
		catch (Exception ex) {
			return new RprojectResult(ex, cmd.getCommandOutput());
		}
	}

	static class RScriptCommandLine {

		private static final String RSCRIPT_COMMAND = "Rscript";

		private final String initScriptPath;

		private final String expression;

		private String commandOutput;

		public RScriptCommandLine(String initScriptPath, String expression) {

			Assert.isTrue(StringUtils.hasText(initScriptPath) || StringUtils.hasText(expression),
					"At least initScriptPath or expressions must not be null or empty!");

			this.initScriptPath = initScriptPath;
			this.expression = expression;
		}

		public String getCommandOutput() {
			return commandOutput;
		}

		public void execute() throws Exception {

			CommandLine cmdLine = createCommandLine();

			prepareArguments(cmdLine);

			execute(cmdLine);
		}


		private CommandLine createCommandLine() {
			return new CommandLine(RSCRIPT_COMMAND);
		}


		private void execute(CommandLine cmdLine) throws Exception {

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

			DefaultExecutor exec = new DefaultExecutor();
			exec.setWorkingDirectory(new File("."));
			exec.setStreamHandler(streamHandler);
			try {
				exec.execute(cmdLine);
			}
			finally {
				this.commandOutput = outputStream.toString();
			}
		}


		private void prepareArguments(CommandLine cmdLine) throws FileNotFoundException {

			Map<String, String> substitutionMap = new HashMap<String, String>();
			if (initScriptPath != null) {

				//we "source" the given scriptLocation to potentially enrich the context of the r-session.
				cmdLine.addArguments("-e 'source(\"${scriptLocation}\")'", false);

				substitutionMap.put("scriptLocation", ResourceUtils.getFile(initScriptPath).getAbsolutePath());
			}

			if (expression != null) {
				/*
				 * In order to capture the output from stdout we wrap the command in a 'cat' function. We additionally wrap it
				 * with a closure in order to be able to execute multiple expressions potentially delimited via ';' at once.
				 * The value of the last expression is returned.
				 */
				cmdLine.addArguments("-e 'cat((function(){${command}})())'", false);

				substitutionMap.put("command", expression);
			}

			cmdLine.setSubstitutionMap(substitutionMap);
		}
	}
}
