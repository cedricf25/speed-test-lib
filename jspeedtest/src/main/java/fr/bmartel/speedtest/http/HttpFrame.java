/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016-2024 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fr.bmartel.speedtest.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpFrame implements IHttpFrame {

    private static final int MAX_LINE_LENGTH = 8192;
    private static final int MAX_HEADER_SIZE = 65536;

    private int statusCode = -1;
    private String reasonPhrase = "";
    private long contentLength = -1;
    private boolean chunkedTransfer = false;
    private final Map<String, String> headers = new HashMap<>();
    private String httpVersion = "";
    private String uri = "";
    private String method = "";
    private String body = "";

    private String readLineLimited(final BufferedReader reader, final int maxLength) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            if (ch == '\n') {
                break;
            }
            if (ch != '\r') {
                sb.append((char) ch);
            }
            if (sb.length() > maxLength) {
                throw new IOException("Line exceeds maximum length: " + maxLength);
            }
        }
        if (sb.length() == 0 && ch == -1) {
            return null;
        }
        return sb.toString();
    }

    public HttpStates decodeFrame(final InputStream inputStream) {
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String statusLine = readLineLimited(reader, MAX_LINE_LENGTH);

            if (statusLine == null || statusLine.isEmpty()) {
                return HttpStates.HTTP_FRAME_ERROR;
            }

            return parseFirstLine(statusLine);
        } catch (IOException e) {
            return HttpStates.HTTP_READING_ERROR;
        }
    }

    public HttpStates parseHeader(final InputStream inputStream) {
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            int totalSize = 0;
            while ((line = readLineLimited(reader, MAX_LINE_LENGTH)) != null) {
                if (line.isEmpty()) {
                    break;
                }
                totalSize += line.length();
                if (totalSize > MAX_HEADER_SIZE) {
                    return HttpStates.HTTP_FRAME_ERROR;
                }
                parseHeaderLine(line);
            }

            return HttpStates.HTTP_FRAME_OK;
        } catch (IOException e) {
            return HttpStates.HTTP_READING_ERROR;
        }
    }

    public HttpStates parseHttp(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final String firstLine = readLineLimited(reader, MAX_LINE_LENGTH);

        if (firstLine == null || firstLine.isEmpty()) {
            return HttpStates.HTTP_FRAME_ERROR;
        }

        final HttpStates statusState = parseFirstLine(firstLine);
        if (statusState != HttpStates.HTTP_FRAME_OK) {
            return statusState;
        }

        String line;
        int totalSize = 0;
        while ((line = readLineLimited(reader, MAX_LINE_LENGTH)) != null) {
            if (line.isEmpty()) {
                break;
            }
            totalSize += line.length();
            if (totalSize > MAX_HEADER_SIZE) {
                return HttpStates.HTTP_FRAME_ERROR;
            }
            parseHeaderLine(line);
        }

        if (contentLength > 0) {
            final char[] bodyChars = new char[(int) contentLength];
            final int read = reader.read(bodyChars, 0, (int) contentLength);
            if (read > 0) {
                body = new String(bodyChars, 0, read);
            }
        }

        return HttpStates.HTTP_FRAME_OK;
    }

    private HttpStates parseFirstLine(final String firstLine) {
        final String[] parts = firstLine.split(" ", 3);

        if (parts.length < 2) {
            return HttpStates.HTTP_FRAME_ERROR;
        }

        if (parts[0].startsWith("HTTP/")) {
            httpVersion = parts[0];

            try {
                statusCode = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return HttpStates.HTTP_FRAME_ERROR;
            }

            if (parts.length >= 3) {
                reasonPhrase = parts[2];
            }
        } else {
            method = parts[0];
            uri = parts[1];
            if (parts.length >= 3) {
                httpVersion = parts[2];
            }
        }

        return HttpStates.HTTP_FRAME_OK;
    }

    private void parseHeaderLine(final String line) {
        final int colonIndex = line.indexOf(':');
        if (colonIndex > 0) {
            final String name = line.substring(0, colonIndex).trim().toLowerCase();
            final String value = line.substring(colonIndex + 1).trim();
            headers.put(name, value);

            if ("content-length".equals(name)) {
                try {
                    contentLength = Long.parseLong(value);
                } catch (NumberFormatException ignored) {
                }
            } else if ("transfer-encoding".equals(name) && value.toLowerCase().contains("chunked")) {
                chunkedTransfer = true;
            }
        }
    }

    public boolean isChunkedTransfer() {
        return chunkedTransfer;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers.clear();
        if (headers != null) {
            this.headers.putAll(headers);
            final String contentLengthValue = headers.get(HttpHeader.CONTENT_LENGTH);
            if (contentLengthValue != null) {
                try {
                    contentLength = Long.parseLong(contentLengthValue);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    @Override
    public String getHttpVersion() {
        return httpVersion;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public boolean isHttpRequestFrame() {
        return method != null && !method.isEmpty();
    }
}
