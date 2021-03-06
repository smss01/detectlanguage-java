package com.detectlanguage;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.errors.APIError;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import java.util.List;
import java.util.Date;

import com.detectlanguage.responses.StatusResponse;

public class DetectLanguageTest extends BaseTest {

	@Test
	public void testSimpleDetect() throws APIError {
		String language = DetectLanguage.simpleDetect("Hello world");

		assertEquals(language, "en");
	}

	@Test
	public void testDetect() throws APIError {
		List<Result> results = DetectLanguage.detect("Hello world");

		Result result = results.get(0);

		assertEquals(result.language, "en");
		assertFalse(result.reliable);
		assertTrue(result.confidence > 0);
	}

	@Test(expected = APIError.class)
	public void testDetectError() throws APIError {
		DetectLanguage.apiKey = "INVALID";
		DetectLanguage.detect("Hello world");
	}

	@Test
	public void testBatchDetect() throws APIError {
		String[] texts = { "Hello world", "Kabo kabikas, žiūri žiūrikas" };

		List<List<Result>> results = DetectLanguage.detect(texts);
		Result result;

		result = results.get(0).get(0);

		assertEquals(result.language, "en");
		assertFalse(result.reliable);
		assertTrue(result.confidence > 0);

		result = results.get(1).get(0);

		assertEquals(result.language, "lt");
		assertFalse(result.reliable);
		assertTrue(result.confidence > 0);
	}

	@Test(expected = APIError.class)
	public void testBatchDetectError() throws APIError {
		DetectLanguage.apiKey = "INVALID";

		String[] texts = { "Hello world", "Kabo kabikas, žiūri žiūrikas" };

		DetectLanguage.detect(texts);
	}

	@Test
	public void testGetStatus() throws APIError {
		StatusResponse statusResponse = DetectLanguage.getStatus();

		assertThat(statusResponse.getDate(), is(instanceOf(Date.class)));
		assertTrue(statusResponse.getRequests() >= 0);
		assertTrue(statusResponse.getBytes() >= 0);
		assertThat(statusResponse.getPlan(), is(instanceOf(String.class)));
		assertThat(statusResponse.getPlanExpires(),
				anyOf(nullValue(), instanceOf(Date.class)));
		assertTrue(statusResponse.getDailyRequestsLimit() > 0);
		assertTrue(statusResponse.getDailyBytesLimit() > 0);
		assertEquals(statusResponse.getStatus(), "ACTIVE");
	}

	@Test(expected = APIError.class)
	public void testStatusError() throws APIError {
		DetectLanguage.apiKey = "INVALID";
		DetectLanguage.getStatus();
	}
}
