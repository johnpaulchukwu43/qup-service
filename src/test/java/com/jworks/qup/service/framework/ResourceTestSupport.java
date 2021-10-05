package com.jworks.qup.service.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
public class ResourceTestSupport extends TestSupport {

    protected MockMvc mockMvc;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<?> controllerType;

    @Value("${documentation.host:localhost}")
    private String host;
    @Value("${documentation.scheme:http}")
    private String scheme;
    @Value("${documentation.port:80}")
    private int port;
    @Value("${documentation.snippets:target/generated-snippets}")
    private String generatedSnippetsFolder;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private final boolean alwaysDocument;

    protected ResourceTestSupport(Class<?> controllerType) {
        this(controllerType, false);
    }

    protected ResourceTestSupport(Class<?> controllerType, boolean alwaysDocument) {
        this.controllerType = controllerType;
        this.alwaysDocument = alwaysDocument;
    }

    @BeforeEach
    public final void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
        final DefaultMockMvcBuilder apply = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation).uris().withPort(port).withHost(host).withScheme(scheme));
        if (alwaysDocument)
                apply.alwaysDo(documentPrettyPrintReqResp("{method-name}"));
        mockMvc = apply.build();
        settingUp();
    }

    // Subclasses can override this to include additional set up logic
    protected void settingUp() throws Exception {
    }

    protected String endpoint() {
        return endpoint("/");
    }

    protected String endpoint(String path) {
        return endpoint(controllerType, path);
    }

    protected String endpoint(Class<?> controllerType, String path) {
        return String.format("%s%s", MvcUriComponentsBuilder.fromController(controllerType).toUriString(), path);
    }

    protected static ResultMatcher successStatus() {
        return jsonPath("$.status", equalTo("success"));
    }

    protected static ResultMatcher failStatus() {
        return jsonPath("$.status", equalTo("fail"));
    }

    protected static ResultMatcher errorStatus() {
        return jsonPath("$.status", equalTo("error"));
    }

    protected static ResultMatcher errorIdExists() {
        return jsonPath("$.error_id").exists();
    }

    protected static ResultMatcher message(String message) {
        return jsonPath("$.message", equalTo(message));
    }

    protected static ResultMatcher message(Matcher<String> matcher) {
        return jsonPath("$.message", matcher);
    }

    protected static <T> JsonPathResultMatchers data(String key) {
        return jsonPath("$.data." + key);
    }

    protected static ResultMatcher code(String code) {
        return jsonPath("$.code", equalTo(code));
    }

    protected static ResultHandler print() {
        return MockMvcResultHandlers.print();
    }

    protected static StatusResultMatchers status() {
        return MockMvcResultMatchers.status();
    }

    protected MockHttpServletRequestBuilder post(Object dto, String urlTemplate, Object... uriVars) throws JsonProcessingException {
        final String writeValueAsString = objectMapper.writeValueAsString(dto);
        System.out.println("writeValueAsString = " + writeValueAsString);
        return post(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON).content(writeValueAsString);
    }

    protected MockMultipartHttpServletRequestBuilder multipart(MockMultipartFile file, String urlTemplate, Object... uriVars) throws JsonProcessingException {
        return multipart(urlTemplate, uriVars).file(file);
    }

    protected MockHttpServletRequestBuilder put(Object dto, String urlTemplate, Object... uriVars) throws JsonProcessingException {
        final String writeValueAsString = objectMapper.writeValueAsString(dto);
        System.out.println("writeValueAsString = " + writeValueAsString);
        return put(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON).content(writeValueAsString);
    }

    protected MockHttpServletRequestBuilder patch(Object dto, String urlTemplate, Object... uriVars) throws JsonProcessingException {
        final String writeValueAsString = objectMapper.writeValueAsString(dto);
        System.out.println("writeValueAsString = " + writeValueAsString);
        return patch(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON).content(writeValueAsString);
    }

    protected MockHttpServletRequestBuilder delete(Object dto, String urlTemplate, Object... uriVars) throws JsonProcessingException {
        final String writeValueAsString = objectMapper.writeValueAsString(dto);
        System.out.println("writeValueAsString = " + writeValueAsString);
        return delete(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON).content(writeValueAsString);
    }

    protected MockHttpServletRequestBuilder post(String urlTemplate, Object... uriVars) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(urlTemplate, uriVars);
    }

    protected MockMultipartHttpServletRequestBuilder multipart(String urlTemplate, Object... uriVars) throws JsonProcessingException {
        return MockMvcRequestBuilders.multipart(urlTemplate, uriVars);
    }

    protected MockHttpServletRequestBuilder get(String urlTemplate, Object... uriVars) throws JsonProcessingException {
        return MockMvcRequestBuilders.get(urlTemplate, uriVars);
    }

    protected MockHttpServletRequestBuilder delete(String urlTemplate, Object... uriVars) throws JsonProcessingException {
        return MockMvcRequestBuilders.delete(urlTemplate, uriVars);
    }

    protected MockHttpServletRequestBuilder put(String urlTemplate, Object... uriVars) throws JsonProcessingException {
        return MockMvcRequestBuilders.put(urlTemplate, uriVars);
    }

    protected MockHttpServletRequestBuilder patch(String urlTemplate, Object... uriVars) throws JsonProcessingException {
        return MockMvcRequestBuilders.patch(urlTemplate, uriVars);
    }

    protected String getLocation(ResultActions resultActions) throws UnsupportedEncodingException {
        return resultActions.andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
    }

    protected String getLastPathFromLocation(String location) throws Exception {
        Path path = Paths.get(location);
        return path.getName(path.getNameCount() - 1).toString();
    }

    protected RestDocumentationResultHandler documentPrettyPrintReqResp(String useCase, Snippet... snippets) {
        return document(useCase,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        snippets);
    }

    protected <T> T read(JsonPath jsonPath, ResultActions resultActions) throws UnsupportedEncodingException {
        return jsonPath.read(resultActions.andReturn().getResponse().getContentAsString());
    }
}
