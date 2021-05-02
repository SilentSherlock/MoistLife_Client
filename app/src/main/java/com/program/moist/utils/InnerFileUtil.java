package com.program.moist.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: SilentSherlock
 * Date: 2021/4/27
 * Description: 保存用户数据，图片，聊天记录等，使用内部存储
 */
public class InnerFileUtil {
    public static final String TAG = "InnerFileUtil";

    /**
     * 保存文件
     * @param filePath
     * @param targetPath
     * @param newFileName
     * @return 返回文件拓展名
     */
    public static String saveFile(String filePath, String targetPath, String newFileName) {
        File file = new File(filePath);
        if (!file.exists()) {
            ToastUtil.showToastShort("要保存的文件不存在哦~");
            Log.i(TAG, "saveFile: file not exist");
            return null;
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        String exName = "";
        try {
            fileInputStream = new FileInputStream(file);
            File targetFilePath = new File(targetPath);
            if (!targetFilePath.exists()) {
                targetFilePath.mkdirs();
                targetFilePath.setWritable(true);
            }
            exName = filePath.substring(filePath.lastIndexOf("."));
            fileOutputStream = new FileOutputStream(new File(targetPath + newFileName + exName));
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) != -1) {
                fileOutputStream.write(buffer);
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            Log.e(TAG, "saveFile: step 1 wrong", e);
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "saveFile: close stream wrong", e);
            }
        }

        return exName;
    }
}
