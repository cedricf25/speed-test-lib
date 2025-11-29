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

public class StatusCodeList {
    public static final StatusCodeObject OK = new StatusCodeObject(200, "OK");
    public static final StatusCodeObject CREATED = new StatusCodeObject(201, "Created");
    public static final StatusCodeObject ACCEPTED = new StatusCodeObject(202, "Accepted");
    public static final StatusCodeObject NO_CONTENT = new StatusCodeObject(204, "No Content");
    public static final StatusCodeObject MOVED_PERMANENTLY = new StatusCodeObject(301, "Moved Permanently");
    public static final StatusCodeObject FOUND = new StatusCodeObject(302, "Found");
    public static final StatusCodeObject SEE_OTHER = new StatusCodeObject(303, "See Other");
    public static final StatusCodeObject NOT_MODIFIED = new StatusCodeObject(304, "Not Modified");
    public static final StatusCodeObject TEMPORARY_REDIRECT = new StatusCodeObject(307, "Temporary Redirect");
    public static final StatusCodeObject BAD_REQUEST = new StatusCodeObject(400, "Bad Request");
    public static final StatusCodeObject UNAUTHORIZED = new StatusCodeObject(401, "Unauthorized");
    public static final StatusCodeObject FORBIDDEN = new StatusCodeObject(403, "Forbidden");
    public static final StatusCodeObject NOT_FOUND = new StatusCodeObject(404, "Not Found");
    public static final StatusCodeObject INTERNAL_SERVER_ERROR = new StatusCodeObject(500, "Internal Server Error");
    public static final StatusCodeObject BAD_GATEWAY = new StatusCodeObject(502, "Bad Gateway");
    public static final StatusCodeObject SERVICE_UNAVAILABLE = new StatusCodeObject(503, "Service Unavailable");
}
