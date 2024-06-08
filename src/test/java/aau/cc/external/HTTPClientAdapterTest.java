package aau.cc.external;

import aau.cc.model.Language;
import okhttp3.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class HTTPClientAdapterTest {
    private final static String BODY = "[{\"Text\": \"Test line\"}]";
    private final static String RESPONSE_BODY = "Test";
    private final static Set<String> HEADERS = new HashSet<>(Arrays.asList("Accept-Encoding", "Accept-Language", "content-type", "X-RapidAPI-Host", "X-RapidAPI-Key"));
    private final ResponseBody responseBody = ResponseBody.create(RESPONSE_BODY, MediaType.get("application/json"));
    private HTTPClientAdapter adapter;
    private OkHttpClient mockOkHttpClient;
    private Call mockCall;
    Response mockResponse;

    @BeforeEach
    public void setUp() throws IOException {
        adapter = new HTTPClientAdapter();
        adapter.prepareTranslationRequest(BODY, Language.ENGLISH);
        mockOkHttpClient = Mockito.mock(OkHttpClient.class);
        mockCall = Mockito.mock(Call.class);
        when(mockOkHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        adapter.setHttpClient(mockOkHttpClient);
    }

    @AfterEach
    public void tearDown() {
        adapter = null;
        mockOkHttpClient = null;
    }

    @Test
    public void testPrepareTranslationRequest() {
        Request request = adapter.getNextRequest();
        assertNotNull(request);
    }

    @Test
    public void testPrepareGetLanguagesRequest() {
        adapter.prepareGetAvailableLanguagesRequest();
        Request request = adapter.getNextRequest();
        assertNotNull(request);
    }

    @Test
    public void testContentOfRequestBodyMethod() {
        Request request = adapter.getNextRequest();
        assertEquals("POST", request.method());
    }

    @Test
    public void testContentOfRequestBodyHeader() {
        adapter.prepareTranslationRequest(BODY, Language.ENGLISH);
        Headers headers = adapter.getNextRequest().headers();
        assertHeaders(headers);
    }

    @Test
    public void testContentOfGetLanguageBodyHeader() {
        adapter.prepareGetAvailableLanguagesRequest();
        Headers headers = adapter.getNextRequest().headers();
        assertHeaders(headers);
    }

    @Test
    public void testDoApiCallRequestNUll() {
        adapter.setNextRequest(null);
        String result = adapter.doAPICall();
        assertEquals("", result);
    }

    @Test
    public void testDoApiCall() {
        setUpSuccessResponse();
        String result = adapter.doAPICall();
        assertEquals(RESPONSE_BODY, result);
    }

    @Test
    public void testDoApiCallError() {
        setUpErrorResponse();
        String result = adapter.doAPICall();
        assertEquals("", result);
    }

    @Test
    public void testDoApiCallErrorNoBody() {
        setUpErrorResponseNoBody();
        assertThrows(AssertionError.class, () -> adapter.doAPICall());
    }

    @Test
    public void testDoApiCallErrorNoBody2() {
        setUpErrorResponseNoBody2();
        assertThrows(AssertionError.class, () -> adapter.doAPICall());
    }

    @Test
    public void testDoApiCallThrowError() throws IOException {
        when(mockCall.execute()).thenThrow(new IOException());
        assertEquals("", adapter.doAPICall());
    }

    @Test
    public void testDoApiCallThrowError2() {
        mockResponse = null;
        setUpMockCall();
        assertThrows(NullPointerException.class, () -> adapter.doAPICall());
    }

    private void assertHeaders(Headers headers) {
        for (String name : headers.names()) {
            assertTrue(HEADERS.contains(name));
        }
    }

    private void setUpSuccessResponse() {
        mockResponse = getBaseResponse()
                .code(200)
                .body(responseBody)
                .build();
        setUpMockCall();
    }

    private void setUpErrorResponse() {
        mockResponse = getBaseResponse()
                .code(400)
                .body(responseBody)
                .build();
        setUpMockCall();
    }

    private void setUpErrorResponseNoBody() {
        mockResponse = getBaseResponse()
                .code(400)
                .build();
        setUpMockCall();
    }

    private void setUpErrorResponseNoBody2() {
        mockResponse = getBaseResponse()
                .code(200)
                .build();
        setUpMockCall();
    }

    private Response.Builder getBaseResponse() {
        return new Response.Builder()
                .request(new Request.Builder().url("http://localhost").build())
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .message("OK");
    }

    private void setUpMockCall() {
        try {
            when(mockCall.execute()).thenReturn(mockResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
