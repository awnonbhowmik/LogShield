package com.logshield.backend.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter();
    }

    @Test
    void allowsRequestsUnderLimit() throws Exception {
        for (int i = 0; i < 20; i++) {
            MockHttpServletRequest req = postScan("10.0.0.1");
            MockHttpServletResponse res = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);

            filter.doFilterInternal(req, res, chain);

            assertThat(res.getStatus()).isNotEqualTo(429);
            verify(chain).doFilter(req, res);
        }
    }

    @Test
    void blocks21stRequestFromSameIp() throws Exception {
        for (int i = 0; i < 20; i++) {
            MockHttpServletRequest req = postScan("10.0.0.2");
            filter.doFilterInternal(req, new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletRequest req = postScan("10.0.0.2");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(429);
        assertThat(res.getContentAsString()).contains("Upload limit exceeded");
        verifyNoInteractions(chain);
    }

    @Test
    void differentIpsAreTrackedIndependently() throws Exception {
        for (int i = 0; i < 20; i++) {
            filter.doFilterInternal(postScan("192.168.1.1"), new MockHttpServletResponse(), mock(FilterChain.class));
        }

        // different IP should still be allowed
        MockHttpServletRequest req = postScan("192.168.1.2");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(req, res, chain);

        assertThat(res.getStatus()).isNotEqualTo(429);
        verify(chain).doFilter(req, res);
    }

    @Test
    void resolvesIpFromXForwardedForHeader() throws Exception {
        for (int i = 0; i < 20; i++) {
            MockHttpServletRequest req = postScan("10.0.0.3");
            req.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.3");
            filter.doFilterInternal(req, new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletRequest req = postScan("10.0.0.3");
        req.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.3");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(429);
    }

    @Test
    void nonUploadRequestsAreNotFiltered() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/scans");
        assertThat(filter.shouldNotFilter(req)).isTrue();
    }

    @Test
    void uploadRequestIsFiltered() {
        MockHttpServletRequest req = postScan("1.2.3.4");
        assertThat(filter.shouldNotFilter(req)).isFalse();
    }

    @Test
    void returns429JsonWithCorrectContentType() throws Exception {
        for (int i = 0; i < 20; i++) {
            filter.doFilterInternal(postScan("10.1.1.1"), new MockHttpServletResponse(), mock(FilterChain.class));
        }
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilterInternal(postScan("10.1.1.1"), res, mock(FilterChain.class));

        assertThat(res.getContentType()).contains("application/json");
        assertThat(res.getContentAsString()).contains("\"status\":429");
    }

    private static MockHttpServletRequest postScan(String remoteAddr) {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/scans");
        req.setRemoteAddr(remoteAddr);
        return req;
    }
}
