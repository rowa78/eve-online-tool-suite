package de.ronnywalter.eve.jobs.util;

import de.ronnywalter.eve.jobs.exception.HttpNotModifiedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;

public class DefaultResponseErrorHandler extends org.springframework.web.client.DefaultResponseErrorHandler {

    @Override
    protected boolean hasError(HttpStatus statusCode) {
        return super.hasError(statusCode) || statusCode == HttpStatus.NOT_MODIFIED;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        switch (statusCode) {
            case NOT_MODIFIED:
                throw new HttpNotModifiedException(statusCode, response.getStatusText(), response.getHeaders(), getResponseBody(response),
                        getCharset(response));
            default:
                super.handleError(response);
        }
    }

}
