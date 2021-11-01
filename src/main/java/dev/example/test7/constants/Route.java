package dev.example.test7.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Route {

    public static final String ROUTE_CHECK_USER = "/check-user";
    public static final String ROUTE_HOME = "/home";
    public static final String ROUTE_FAILED = "/failed";
    public static final String ROUTE_LOGIN = "/login";
    public static final String ROUTE_UPLOAD_INDEX = "/upload";
    public static final String ROUTE_UPLOAD_FILE = "/upload-file";
}
