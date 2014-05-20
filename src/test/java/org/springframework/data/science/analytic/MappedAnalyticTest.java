package org.springframework.data.science.analytic;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;

/**
 * @author Thomas Darimont
 */
public class MappedAnalyticTest {

	Analytic<Map<String, Object>, Map<String, Object>> analytic;

	@Rule public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		analytic = new DummyMappedAnalytic(new DummyInputMapper(), new DummyOutputMapper());
	}

	@Test
	public void testShouldNotAllowNullInputMapper() {

		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("inputMapper");

		new DummyMappedAnalytic(null, new DummyOutputMapper());
	}

	@Test
	public void testShouldNotAllowNullOutputMapper() {

		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("outputMapper");

		new DummyMappedAnalytic(new DummyInputMapper(), null);
	}


	@Test
	public void testEvaluateDummyMappedAnalytic() {

		Map<String, Object> input = ImmutableMap.<String, Object> of("k1", "v1", "k2", "v2");

		Map<String, Object> output = analytic.evaluate(input);

		assertNotSame(input, output);
		assertThat(output.get("k1"), is((Object) "V1"));
		assertThat(output.get("k2"), is((Object) "V2"));
	}
}
