/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.web.servlet.support;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.context.support.MyWebApplicationObjectSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Convenient superclass for any kind of web content generator,
 * like {@link org.springframework.web.servlet.mvc.AbstractController}
 * and {@link org.springframework.web.servlet.mvc.WebContentInterceptor}.
 * Can also be used for custom handlers that have their own
 * {@link org.springframework.web.servlet.HandlerAdapter}.
 *
 * <p>Supports HTTP cache control options. The usage of corresponding HTTP
 * headers can be controlled via the {@link #setCacheSeconds "cacheSeconds"}
 * and {@link #setCacheControl "cacheControl"} properties.
 *
 * <p><b>NOTE:</b> As of Spring 4.2, this generator's default behavior changed when
 * using only {@link #setCacheSeconds}, sending HTTP response headers that are in line
 * with current browsers and proxies implementations (i.e. no HTTP 1.0 headers anymore)
 * Reverting to the previous behavior can be easily done by using one of the newly
 * deprecated methods {@link #setUseExpiresHeader}, {@link #setUseCacheControlHeader},
 * {@link #setUseCacheControlNoStore} or {@link #setAlwaysMustRevalidate}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @see #setCacheSeconds
 * @see #setCacheControl
 * @see #setRequireSession
 */
public abstract class MyWebContentGenerator extends MyWebApplicationObjectSupport {

    /** HTTP method "GET" */
    public static final String METHOD_GET = "GET";

    /** HTTP method "HEAD" */
    public static final String METHOD_HEAD = "HEAD";

    /** HTTP method "POST" */
    public static final String METHOD_POST = "POST";

    private static final String HEADER_PRAGMA = "Pragma";

    private static final String HEADER_EXPIRES = "Expires";

    protected static final String HEADER_CACHE_CONTROL = "Cache-Control";

    /** Checking for Servlet 3.0+ HttpServletResponse.getHeaders(String) */
    private static final boolean servlet3Present =
            ClassUtils.hasMethod(HttpServletResponse.class, "getHeaders", String.class);


    /** Set of supported HTTP methods */
    private Set<String> supportedMethods;

    private String allowHeader;

    private boolean requireSession = false;

    private CacheControl cacheControl;

    private int cacheSeconds = -1;

    private String[] varyByRequestHeaders;


    // deprecated fields

    /** Use HTTP 1.0 expires header? */
    private boolean useExpiresHeader = false;

    /** Use HTTP 1.1 cache-control header? */
    private boolean useCacheControlHeader = true;

    /** Use HTTP 1.1 cache-control header value "no-store"? */
    private boolean useCacheControlNoStore = true;

    private boolean alwaysMustRevalidate = false;


    /**
     * Create a new MyWebContentGenerator which supports
     * HTTP methods GET, HEAD and POST by default.
     */
    public MyWebContentGenerator() {
        this(true);
    }

    /**
     * Create a new MyWebContentGenerator.
     * @param restrictDefaultSupportedMethods {@code true} if this
     * generator should support HTTP methods GET, HEAD and POST by default,
     * or {@code false} if it should be unrestricted
     */
    public MyWebContentGenerator(boolean restrictDefaultSupportedMethods) {
        if (restrictDefaultSupportedMethods) {
            this.supportedMethods = new LinkedHashSet<String>(4);
            this.supportedMethods.add(METHOD_GET);
            this.supportedMethods.add(METHOD_HEAD);
            this.supportedMethods.add(METHOD_POST);
        }
        initAllowHeader();
    }

    /**
     * Create a new MyWebContentGenerator.
     * @param supportedMethods the supported HTTP methods for this content generator
     */
    public MyWebContentGenerator(String... supportedMethods) {
        setSupportedMethods(supportedMethods);
    }


    /**
     * Set the HTTP methods that this content generator should support.
     * <p>Default is GET, HEAD and POST for simple form controller types;
     * unrestricted for general controllers and interceptors.
     */
    public final void setSupportedMethods(String... methods) {
        if (!ObjectUtils.isEmpty(methods)) {
            this.supportedMethods = new LinkedHashSet<String>(Arrays.asList(methods));
        }
        else {
            this.supportedMethods = null;
        }
        initAllowHeader();
    }

    /**
     * Return the HTTP methods that this content generator supports.
     */
    public final String[] getSupportedMethods() {
        return StringUtils.toStringArray(this.supportedMethods);
    }

    private void initAllowHeader() {
        Collection<String> allowedMethods;
        if (this.supportedMethods == null) {
            allowedMethods = new ArrayList<String>(HttpMethod.values().length - 1);
            for (HttpMethod method : HttpMethod.values()) {
                if (!HttpMethod.TRACE.equals(method)) {
                    allowedMethods.add(method.name());
                }
            }
        }
        else if (this.supportedMethods.contains(HttpMethod.OPTIONS.name())) {
            allowedMethods = this.supportedMethods;
        }
        else {
            allowedMethods = new ArrayList<String>(this.supportedMethods);
            allowedMethods.add(HttpMethod.OPTIONS.name());

        }
        this.allowHeader = StringUtils.collectionToCommaDelimitedString(allowedMethods);
    }

    /**
     * Return the "Allow" header value to use in response to an HTTP OPTIONS
     * request based on the configured {@link #setSupportedMethods supported
     * methods} also automatically adding "OPTIONS" to the list even if not
     * present as a supported method. This means sub-classes don't have to
     * explicitly list "OPTIONS" as a supported method as long as HTTP OPTIONS
     * requests are handled before making a call to
     * {@link #checkRequest(HttpServletRequest)}.
     */
    protected String getAllowHeader() {
        return this.allowHeader;
    }

    /**
     * Set whether a session should be required to handle requests.
     */
    public final void setRequireSession(boolean requireSession) {
        this.requireSession = requireSession;
    }

    /**
     * Return whether a session is required to handle requests.
     */
    public final boolean isRequireSession() {
        return this.requireSession;
    }

    /**
     * Set the {@link org.springframework.http.CacheControl} instance to build
     * the Cache-Control HTTP response header.
     * @since 4.2
     */
    public final void setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    /**
     * Get the {@link org.springframework.http.CacheControl} instance
     * that builds the Cache-Control HTTP response header.
     * @since 4.2
     */
    public final CacheControl getCacheControl() {
        return this.cacheControl;
    }

    /**
     * Cache content for the given number of seconds, by writing
     * cache-related HTTP headers to the response:
     * <ul>
     * <li>seconds == -1 (default value): no generation cache-related headers</li>
     * <li>seconds == 0: "Cache-Control: no-store" will prevent caching</li>
     * <li>seconds > 0: "Cache-Control: max-age=seconds" will ask to cache content</li>
     * </ul>
     * <p>For more specific needs, a custom {@link org.springframework.http.CacheControl}
     * should be used.
     * @see #setCacheControl
     */
    public final void setCacheSeconds(int seconds) {
        this.cacheSeconds = seconds;
    }

    /**
     * Return the number of seconds that content is cached.
     */
    public final int getCacheSeconds() {
        return this.cacheSeconds;
    }

    /**
     * Configure one or more request header names (e.g. "Accept-Language") to
     * add to the "Vary" response header to inform clients that the response is
     * subject to content negotiation and variances based on the value of the
     * given request headers. The configured request header names are added only
     * if not already present in the response "Vary" header.
     * <p><strong>Note:</strong> This property is only supported on Servlet 3.0+
     * which allows checking existing response header values.
     * @param varyByRequestHeaders one or more request header names
     * @since 4.3
     */
    public final void setVaryByRequestHeaders(String... varyByRequestHeaders) {
        this.varyByRequestHeaders = varyByRequestHeaders;
    }

    /**
     * Return the configured request header names for the "Vary" response header.
     * @since 4.3
     */
    public final String[] getVaryByRequestHeaders() {
        return this.varyByRequestHeaders;
    }

    /**
     * Set whether to use the HTTP 1.0 expires header. Default is "false",
     * as of 4.2.
     * <p>Note: Cache headers will only get applied if caching is enabled
     * (or explicitly prevented) for the current request.
     * @deprecated as of 4.2, since going forward, the HTTP 1.1 cache-control
     * header will be required, with the HTTP 1.0 headers disappearing
     */
    @Deprecated
    public final void setUseExpiresHeader(boolean useExpiresHeader) {
        this.useExpiresHeader = useExpiresHeader;
    }

    /**
     * Return whether the HTTP 1.0 expires header is used.
     * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
     */
    @Deprecated
    public final boolean isUseExpiresHeader() {
        return this.useExpiresHeader;
    }

    /**
     * Set whether to use the HTTP 1.1 cache-control header. Default is "true".
     * <p>Note: Cache headers will only get applied if caching is enabled
     * (or explicitly prevented) for the current request.
     * @deprecated as of 4.2, since going forward, the HTTP 1.1 cache-control
     * header will be required, with the HTTP 1.0 headers disappearing
     */
    @Deprecated
    public final void setUseCacheControlHeader(boolean useCacheControlHeader) {
        this.useCacheControlHeader = useCacheControlHeader;
    }

    /**
     * Return whether the HTTP 1.1 cache-control header is used.
     * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
     */
    @Deprecated
    public final boolean isUseCacheControlHeader() {
        return this.useCacheControlHeader;
    }

    /**
     * Set whether to use the HTTP 1.1 cache-control header value "no-store"
     * when preventing caching. Default is "true".
     * @deprecated as of 4.2, in favor of {@link #setCacheControl}
     */
    @Deprecated
    public final void setUseCacheControlNoStore(boolean useCacheControlNoStore) {
        this.useCacheControlNoStore = useCacheControlNoStore;
    }

    /**
     * Return whether the HTTP 1.1 cache-control header value "no-store" is used.
     * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
     */
    @Deprecated
    public final boolean isUseCacheControlNoStore() {
        return this.useCacheControlNoStore;
    }

    /**
     * An option to add 'must-revalidate' to every Cache-Control header.
     * This may be useful with annotated controller methods, which can
     * programmatically do a last-modified calculation as described in
     * {@link org.springframework.web.context.request.WebRequest#checkNotModified(long)}.
     * <p>Default is "false".
     * @deprecated as of 4.2, in favor of {@link #setCacheControl}
     */
    @Deprecated
    public final void setAlwaysMustRevalidate(boolean mustRevalidate) {
        this.alwaysMustRevalidate = mustRevalidate;
    }

    /**
     * Return whether 'must-revalidate' is added to every Cache-Control header.
     * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
     */
    @Deprecated
    public final boolean isAlwaysMustRevalidate() {
        return this.alwaysMustRevalidate;
    }


    /**
     * Check the given request for supported methods and a required session, if any.
     * @param request current HTTP request
     * @throws ServletException if the request cannot be handled because a check failed
     * @since 4.2
     */
    protected final void checkRequest(HttpServletRequest request) throws ServletException {
        // Check whether we should support the request method.
        String method = request.getMethod();
        if (this.supportedMethods != null && !this.supportedMethods.contains(method)) {
            throw new HttpRequestMethodNotSupportedException(method, this.supportedMethods);
        }

        // Check whether a session is required.
        if (this.requireSession && request.getSession(false) == null) {
            throw new HttpSessionRequiredException("Pre-existing session required but none found");
        }
    }

    /**
     * Prepare the given response according to the settings of this generator.
     * Applies the number of cache seconds specified for this generator.
     * @param response current HTTP response
     * @since 4.2
     */
    protected final void prepareResponse(HttpServletResponse response) {
        if (this.cacheControl != null) {
            applyCacheControl(response, this.cacheControl);
        }
        else {
            applyCacheSeconds(response, this.cacheSeconds);
        }
        if (servlet3Present && this.varyByRequestHeaders != null) {
            for (String value : getVaryRequestHeadersToAdd(response)) {
                response.addHeader("Vary", value);
            }
        }
    }

    /**
     * Set the HTTP Cache-Control header according to the given settings.
     * @param response current HTTP response
     * @param cacheControl the pre-configured cache control settings
     * @since 4.2
     */
    protected final void applyCacheControl(HttpServletResponse response, CacheControl cacheControl) {
        String ccValue = cacheControl.getHeaderValue();
        if (ccValue != null) {
            // Set computed HTTP 1.1 Cache-Control header
            response.setHeader(HEADER_CACHE_CONTROL, ccValue);

            if (response.containsHeader(HEADER_PRAGMA)) {
                // Reset HTTP 1.0 Pragma header if present
                response.setHeader(HEADER_PRAGMA, "");
            }
            if (response.containsHeader(HEADER_EXPIRES)) {
                // Reset HTTP 1.0 Expires header if present
                response.setHeader(HEADER_EXPIRES, "");
            }
        }
    }

    /**
     * Apply the given cache seconds and generate corresponding HTTP headers,
     * i.e. allow caching for the given number of seconds in case of a positive
     * value, prevent caching if given a 0 value, do nothing else.
     * Does not tell the browser to revalidate the resource.
     * @param response current HTTP response
     * @param cacheSeconds positive number of seconds into the future that the
     * response should be cacheable for, 0 to prevent caching
     */
    @SuppressWarnings("deprecation")
    protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds) {
        if (this.useExpiresHeader || !this.useCacheControlHeader) {
            // Deprecated HTTP 1.0 cache behavior, as in previous Spring versions
            if (cacheSeconds > 0) {
                cacheForSeconds(response, cacheSeconds);
            }
            else if (cacheSeconds == 0) {
                preventCaching(response);
            }
        }
        else {
            CacheControl cControl;
            if (cacheSeconds > 0) {
                cControl = CacheControl.maxAge(cacheSeconds, TimeUnit.SECONDS);
                if (this.alwaysMustRevalidate) {
                    cControl = cControl.mustRevalidate();
                }
            }
            else if (cacheSeconds == 0) {
                cControl = (this.useCacheControlNoStore ? CacheControl.noStore() : CacheControl.noCache());
            }
            else {
                cControl = CacheControl.empty();
            }
            applyCacheControl(response, cControl);
        }
    }


    /**
     * @see #checkRequest(HttpServletRequest)
     * @see #prepareResponse(HttpServletResponse)
     * @deprecated as of 4.2, since the {@code lastModified} flag is effectively ignored,
     * with a must-revalidate header only generated if explicitly configured
     */
    @Deprecated
    protected final void checkAndPrepare(
            HttpServletRequest request, HttpServletResponse response, boolean lastModified) throws ServletException {

        checkRequest(request);
        prepareResponse(response);
    }

    /**
     * @see #checkRequest(HttpServletRequest)
     * @see #applyCacheSeconds(HttpServletResponse, int)
     * @deprecated as of 4.2, since the {@code lastModified} flag is effectively ignored,
     * with a must-revalidate header only generated if explicitly configured
     */
    @Deprecated
    protected final void checkAndPrepare(
            HttpServletRequest request, HttpServletResponse response, int cacheSeconds, boolean lastModified)
            throws ServletException {

        checkRequest(request);
        applyCacheSeconds(response, cacheSeconds);
    }

    /**
     * Apply the given cache seconds and generate respective HTTP headers.
     * <p>That is, allow caching for the given number of seconds in the
     * case of a positive value, prevent caching if given a 0 value, else
     * do nothing (i.e. leave caching to the client).
     * @param response the current HTTP response
     * @param cacheSeconds the (positive) number of seconds into the future
     * that the response should be cacheable for; 0 to prevent caching; and
     * a negative value to leave caching to the client.
     * @param mustRevalidate whether the client should revalidate the resource
     * (typically only necessary for controllers with last-modified support)
     * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
     */
    @Deprecated
    protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds, boolean mustRevalidate) {
        if (cacheSeconds > 0) {
            cacheForSeconds(response, cacheSeconds, mustRevalidate);
        }
        else if (cacheSeconds == 0) {
            preventCaching(response);
        }
    }

    /**
     * Set HTTP headers to allow caching for the given number of seconds.
     * Does not tell the browser to revalidate the resource.
     * @param response current HTTP response
     * @param seconds number of seconds into the future that the response
     * should be cacheable for
     * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
     */
    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds) {
        cacheForSeconds(response, seconds, false);
    }

    /**
     * Set HTTP headers to allow caching for the given number of seconds.
     * Tells the browser to revalidate the resource if mustRevalidate is
     * {@code true}.
     * @param response the current HTTP response
     * @param seconds number of seconds into the future that the response
     * should be cacheable for
     * @param mustRevalidate whether the client should revalidate the resource
     * (typically only necessary for controllers with last-modified support)
     * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
     */
    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
        if (this.useExpiresHeader) {
            // HTTP 1.0 header
            response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + seconds * 1000L);
        }
        else if (response.containsHeader(HEADER_EXPIRES)) {
            // Reset HTTP 1.0 Expires header if present
            response.setHeader(HEADER_EXPIRES, "");
        }

        if (this.useCacheControlHeader) {
            // HTTP 1.1 header
            String headerValue = "max-age=" + seconds;
            if (mustRevalidate || this.alwaysMustRevalidate) {
                headerValue += ", must-revalidate";
            }
            response.setHeader(HEADER_CACHE_CONTROL, headerValue);
        }

        if (response.containsHeader(HEADER_PRAGMA)) {
            // Reset HTTP 1.0 Pragma header if present
            response.setHeader(HEADER_PRAGMA, "");
        }
    }

    /**
     * Prevent the response from being cached.
     * Only called in HTTP 1.0 compatibility mode.
     * <p>See {@code http://www.mnot.net/cache_docs}.
     * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
     */
    @Deprecated
    protected final void preventCaching(HttpServletResponse response) {
        response.setHeader(HEADER_PRAGMA, "no-cache");

        if (this.useExpiresHeader) {
            // HTTP 1.0 Expires header
            response.setDateHeader(HEADER_EXPIRES, 1L);
        }

        if (this.useCacheControlHeader) {
            // HTTP 1.1 Cache-Control header: "no-cache" is the standard value,
            // "no-store" is necessary to prevent caching on Firefox.
            response.setHeader(HEADER_CACHE_CONTROL, "no-cache");
            if (this.useCacheControlNoStore) {
                response.addHeader(HEADER_CACHE_CONTROL, "no-store");
            }
        }
    }


    private Collection<String> getVaryRequestHeadersToAdd(HttpServletResponse response) {
        if (!response.containsHeader(HttpHeaders.VARY)) {
            return Arrays.asList(getVaryByRequestHeaders());
        }
        Collection<String> result = new ArrayList<String>(getVaryByRequestHeaders().length);
        Collections.addAll(result, getVaryByRequestHeaders());
        for (String header : response.getHeaders(HttpHeaders.VARY)) {
            for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
                if ("*".equals(existing)) {
                    return Collections.emptyList();
                }
                for (String value : getVaryByRequestHeaders()) {
                    if (value.equalsIgnoreCase(existing)) {
                        result.remove(value);
                    }
                }
            }
        }
        return result;
    }

}
