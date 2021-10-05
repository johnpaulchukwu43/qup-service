package com.jworks.qup.service.controllers;


import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.framework.ResourceTestSupport;
import com.jworks.qup.service.providers.impl.EndUserProvider;
import com.jworks.qup.service.security.AuthenticationMocks;
import com.jworks.qup.service.security.MockSecurityContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
@SpringBootTest
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@ActiveProfiles("test")
public class AbstractResourceTest extends ResourceTestSupport {

    protected static final String RUN_ALL_TESTS_PROFILE = "full"; // Includes slow running integration tests in tests to run.

    @Autowired
    private EndUserProvider endUserProvider;

    public AbstractResourceTest(Class<?> controllerType) {
        super(controllerType);
    }

    @PostConstruct
    public void init() {
        // Save admin user to db.
        final String userReference = ((User) AuthenticationMocks.adminAuthentication().getPrincipal()).getUsername();
        if (!endUserProvider.getRepository().existsByUserReference(userReference)) {
            EndUser endUser = endUserProvider.provide();
            endUser.setUserReference(userReference);
            endUserProvider.save(endUser);
        }
    }

    // Subclasses can extend this to include additional set up logic
    @Override
    protected void settingUp() throws Exception {
        super.settingUp();
    }

    private BigDecimal nextPercentage() {
        return BigDecimal.valueOf(random.nextInt(100) + random.nextDouble()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nextAmount() {
        return BigDecimal.valueOf(1_000_000L * random.nextDouble()).setScale(2, RoundingMode.HALF_UP);
    }

    protected BigDecimal nextBigDecimal() {
        return BigDecimal.valueOf(nextNumber() + random.nextDouble());
    }

    protected ResultActions asAdmin(MockHttpServletRequestBuilder request) throws Exception {
        TestSecurityContextHolder.setContext(new MockSecurityContext(
                AuthenticationMocks.adminAuthentication()
        ));
        return mockMvc.perform(request);
    }

    /**
     * Returns a matcher that ensures that every object in the given collection is matched.
     * @param collection
     * @return
     */
    protected Matcher<Iterable<? extends Map<? extends String, ? extends Object>>> matchList(Collection<?> collection) {
        return Matchers.containsInAnyOrder(collection.stream().map(this::toLinkedHashMap)
                .map(foo -> (LinkedHashMap<String, ?>) foo).map(foo -> Matchers.allOf(foo.entrySet().stream()
                .map(entry -> (Map.Entry<String, ?>) entry).map(entry -> Matchers.hasEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()))).collect(Collectors.toList()));
    }

    protected LinkedHashMap<String, ?> toLinkedHashMap(Object object) {
        LinkedHashMap linkedHashMap = objectMapper.convertValue(object, LinkedHashMap.class);
        for (Object o : linkedHashMap.entrySet()) {
            // Convert all BigDecimals to Double so that JUnit assertEqual can work.
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
            if (entry.getValue() instanceof BigDecimal)
                entry.setValue(((BigDecimal) entry.getValue()).doubleValue());
            }
        return linkedHashMap;
    }

    protected static ResultMatcher transactionFailStatus() {
        return data("status").value("fail");
    }

    protected static ResultMatcher transactionSuccessStatus() {
        return data("status").value("success");
    }

    protected static ResultMatcher transactionPendingStatus() {
        return data("status").value("pending");
    }

    protected static ResultMatcher pendingStatus() {
        return jsonPath("$.status", equalTo("pending"));
    }
}
