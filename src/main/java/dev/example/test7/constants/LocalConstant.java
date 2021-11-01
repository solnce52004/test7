package dev.example.test7.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LocalConstant {

    public static final String STORAGE = "./storage/";
    public static final String UPLOADS = "uploads";
    public static final String LOCAL_UPLOADED_FILE_NAME_FORMAT = "%s_fid_%s.%s";
}
