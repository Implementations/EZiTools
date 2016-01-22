package com.kanishk.ezetools.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kanishk on 1/12/16.
 */
public class Util {

    public static void copyAsset(Context context, int soundId, File destFile) throws IOException {
        InputStream stream  = context.getResources().openRawResource(soundId);
        FileOutputStream out = new FileOutputStream(destFile);
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            stream.close();
            out.close();
        }
    }
}
