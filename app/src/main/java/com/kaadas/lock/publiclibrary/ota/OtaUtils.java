package com.kaadas.lock.publiclibrary.ota;

import java.io.File;

public class OtaUtils {

    public static void createFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
