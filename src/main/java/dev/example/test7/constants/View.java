package dev.example.test7.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class View {
    public static final String VIEW_LOGIN = "login";
    public static final String VIEW_HOME = "home";
    public static final String VIEW_FAILED = "failed";

    public static final String VIEW_UPLOAD_INDEX = "upload";
    public static final String VIEW_UPLOAD_MULTIPLE_INDEX = "upload_multiple";
}
