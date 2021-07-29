package com.jay.android.dispatcher.register.utils;

import com.android.SdkConstants;

import java.io.File;
import java.io.FileOutputStream;

public class ClassUtils {

    public static String path2Classname(String entryName) {
        // 感谢大佬 dingshaoran
        //ClassUtils.path2Classname(className); File.separator donot match jar entryName on windows
        return entryName.replace(".class", "")
                .replace('\\', '.')
                .replace('/', '.');
    }

    public static String classname2path(String canonicalName, boolean hasClass) {
        if (canonicalName == null || canonicalName.length() == 0) {
            return "";
        }
        String result = canonicalName.replace('.', '/');
        if (hasClass) {
            result += SdkConstants.DOT_CLASS;
        }
        return result;
    }

    public static boolean checkClassName(String className) {
        return (!className.contains("R\\$") && !className.endsWith("R")
                && !className.endsWith("BuildConfig"));
    }

    public static File saveFile(File tempDir, byte[] modifiedClassBytes) {
        File modified = null;
        try {
            if (modifiedClassBytes != null) {
                modified = tempDir;
                if (modified.exists()) {
                    modified.delete();
                }
                modified.createNewFile();
                FileOutputStream stream = new FileOutputStream(modified);
                stream.write(modifiedClassBytes);
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modified;
    }


}