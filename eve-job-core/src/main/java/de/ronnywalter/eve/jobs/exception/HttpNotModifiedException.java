package de.ronnywalter.eve.jobs.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.Charset;

public class HttpNotModifiedException extends HttpStatusCodeException {
    private static final long serialVersionUID = -5163181110183146369L;

    public HttpNotModifiedException(HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] responseBody, Charset charset) {
        super(statusCode, statusText, headers, responseBody, charset);
    }
}
