package dev.example.test7.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Route {

    public static final String REDIRECT = "redirect:";

    public static final String CHECK_USER = "/check-user";
    public static final String HOME = "/home";
    public static final String FAILED = "/failed";

    public static final String UPLOAD_INDEX = "/upload";
    public static final String REDIRECT_UPLOAD_INDEX = REDIRECT + UPLOAD_INDEX;
    public static final String UPLOAD_FILE = "/upload-file";

    public static final String UPLOAD_MULTIPLE_INDEX = "/upload-multiple";
    public static final String REDIRECT_UPLOAD_MULTIPLE_INDEX = REDIRECT + UPLOAD_MULTIPLE_INDEX;
    public static final String UPLOAD_FILE_MULTIPLE = "/upload-file-multiple";

    //auth
    public static final String AUTH = "/auth";

    public static final String REGISTRATION = "/registration";
    public static final String AUTH_REGISTRATION = AUTH + REGISTRATION;
    public static final String REDIRECT_AUTH_REGISTRATION = REDIRECT + AUTH_REGISTRATION;

    public static final String LOGIN = "/login";
    public static final String AUTH_LOGIN = AUTH + LOGIN;
    public static final String REDIRECT_AUTH_LOGIN = REDIRECT + AUTH_LOGIN;

    public static final String SEND_VERIFY = "/send-verify";
    public static final String AUTH_SEND_VERIFY = AUTH + SEND_VERIFY;
    public static final String REDIRECT_AUTH_SEND_VERIFY = REDIRECT + AUTH_SEND_VERIFY;

    public static final String RESET_PASSWORD = "/reset-password";
    public static final String AUTH_RESET_PASSWORD = AUTH + RESET_PASSWORD;
    public static final String REDIRECT_AUTH_RESET_PASSWORD = REDIRECT + AUTH_RESET_PASSWORD;

    public static final String SEND_RESET_PASSWORD = "/send-reset-password";
    public static final String AUTH_SEND_RESET_PASSWORD = AUTH + SEND_RESET_PASSWORD;
    public static final String REDIRECT_AUTH_SEND_RESET_PASSWORD = REDIRECT + AUTH_SEND_RESET_PASSWORD;

    public static final String SUCCESS = "/success";
    public static final String AUTH_SUCCESS = AUTH + SUCCESS;
    public static final String REDIRECT_AUTH_SUCCESS = REDIRECT + AUTH_SUCCESS;
}
