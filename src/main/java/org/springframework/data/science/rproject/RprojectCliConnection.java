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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import org.springframework.util.ResourceUtils;

/**
 * @author Thomas Darimont
 */
public class RprojectCliConnection implements RprojectConnection {

	private final String rprojectCommand = "Rscript";

	private String scriptLocation;

	public RprojectCliConnection() {
	}

	private RprojectCliConnection(String scriptLocation) {
		this.scriptLocation = scriptLocation;
	}

	@Override
	public String eval(String command) {

		CommandLine cmdLine = new CommandLine(rprojectCommand);

		if (scriptLocation != null) {
			cmdLine.addArguments("-e 'source(\"${scriptName}\")'", false);
		}

		cmdLine.addArguments("-e '${command}'", false);

		String result = null;

		try {
			File file = ResourceUtils.getFile(scriptLocation);

			Map<String, String> substitutionMap = new HashMap<String, String>();
			substitutionMap.put("scriptName", file.getAbsolutePath());
			substitutionMap.put("command", command);
			cmdLine.setSubstitutionMap(substitutionMap);

			DefaultExecutor exec = new DefaultExecutor();
			exec.setWorkingDirectory(new File("."));

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
			exec.setStreamHandler(streamHandler);

			exec.execute(cmdLine);

			result = outputStream.toString();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return result;
	}

	@Override
	public RprojectConnection forScript(String scriptLocation) {
		return new RprojectCliConnection(scriptLocation);
	}
}
