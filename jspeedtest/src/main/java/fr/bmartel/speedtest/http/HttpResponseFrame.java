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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseFrame {
    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private StatusCodeObject statusCode = StatusCodeList.OK;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];

    public HttpResponseFrame(StatusCodeObject statusCode, HttpVersion version, Map<String, String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.httpVersion = version;
        if (headers != null) {
            this.headers.putAll(headers);
        }
        if (body != null) {
            this.body = body;
        }
    }

    public HttpResponseFrame(StatusCodeObject statusCode, HttpVersion version, Map<String, String> headers, String body) {
        this(statusCode, version, headers, body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0]);
    }

    public byte[] build() {
        final StringBuilder sb = new StringBuilder();
        sb.append(httpVersion.toString())
          .append(" ")
          .append(statusCode.getCode())
          .append(" ")
          .append(statusCode.getReasonPhrase())
          .append("\r\n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey())
              .append(": ")
              .append(entry.getValue())
              .append("\r\n");
        }

        if (body.length > 0 && !headers.containsKey(HttpHeader.CONTENT_LENGTH)) {
            sb.append(HttpHeader.CONTENT_LENGTH)
              .append(": ")
              .append(body.length)
              .append("\r\n");
        }

        sb.append("\r\n");

        final byte[] headerBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        if (body.length > 0) {
            final byte[] result = new byte[headerBytes.length + body.length];
            System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
            System.arraycopy(body, 0, result, headerBytes.length, body.length);
            return result;
        }

        return headerBytes;
    }

    public static byte[] buildFrame(StatusCodeObject statusCode, HttpVersion version, Map<String, String> headers, byte[] body) {
        return new HttpResponseFrame(statusCode, version, headers, body).build();
    }

    public static byte[] buildFrame(StatusCodeObject statusCode, HttpVersion version, Map<String, String> headers, String body) {
        return new HttpResponseFrame(statusCode, version, headers, body).build();
    }
}
