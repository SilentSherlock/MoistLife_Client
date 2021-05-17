package com.program.moist.utils;

import android.util.Log;

import com.program.moist.entity.item.Message;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: SilentSherlock
 * Date: 2021/4/27
 * Description: 保存用户数据，聊天记录等，使用内部存储
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

    /**
     * 保存消息到文件中，需要对象实现序列化
     * @return 文件首次创建-true 文件已经存在-false
     * 将message以list的形式保存，便于取出
     */
    public static synchronized boolean saveObjectToFile(Message message, String filePath, String fileName) {
        boolean isCreated = false;
        File targetPath = new File(filePath);
        File file = new File(filePath + fileName);
        if (!targetPath.exists()) {
            targetPath.mkdirs();
        }

        ArrayList<Message> messages;
        if (!file.exists()) {
            try {
                file.createNewFile();
                isCreated = true;
                Log.i(TAG, "saveObjectToFile: 新建文件成功");
            } catch (IOException e) {
                Log.e(TAG, "saveObjectToFile: createNewFile失败", e);
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            if (isCreated) {//新建文件则新建list
                messages = new ArrayList<>();
            } else {
                messages = readObjectFromFile(filePath + fileName);
            }
            if (messages != null) {
                messages.add(message);
                Log.i(TAG, "saveObjectToFile: messages " + messages.toString());
                //以覆盖的形式保存内容时
                //需要在已经读取到文件内容再打开流，不然会导致文件内容被清空
                fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(messages);
                //objectOutputStream.writeChars("\n");
                objectOutputStream.flush();
                objectOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "saveObjectToFile: file not found", e);
        } catch (IOException e) {
            Log.e(TAG, "saveObjectToFile: IO wrong", e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "saveObjectToFile: close wrong", e);
                }
            }
        }

        return isCreated;
    }

    /**
     * 从文件中读取消息对象
     * @param filePath
     * @return
     */
    public static synchronized ArrayList<Message> readObjectFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "readObjectFromFile: wrong 从不存在的文件中读取", new Exception());
            return null;
        }
        FileInputStream fileInputStream = null;
        ArrayList<Message> messages = null;
        Log.i(TAG, "readObjectFromFile: filePath" + filePath);
        try {
            fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            try {
                Object o = objectInputStream.readObject();
                messages = (ArrayList<Message>) o;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                Log.e(TAG, "readObjectFromFile: EOF 读取到文件末尾！但是这并没有什么，因为就是这样设计的哦", e);
            }
            objectInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "readObjectFromFile: File not found", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
