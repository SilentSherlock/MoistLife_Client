package com.program.moist.utils;

import android.util.Log;

import com.program.moist.base.AppConst;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/4/27
 * Description: describe the class
 */
public class FTPUtil implements Runnable {

    private String filePath;
    private String newPath;
    private String newFileName;

    /**
     *
     * @param filePath
     * @param newPath
     * @param newFileName 若不提供，即提供空字符串，则将newFileName设置成下载的文件名
     */
    public static void download(String filePath, String newPath, String newFileName) {
        FTPUtil ftpUtil = new FTPUtil(AppConst.Server.FTP_IP, AppConst.Server.FTP_PORT, AppConst.Server.FTP_USER, AppConst.Server.FTP_PASS);
        ftpUtil.filePath = filePath;
        ftpUtil.newPath = newPath;
        ftpUtil.newFileName = newFileName;

        new Thread(ftpUtil).start();
    }

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    private FTPUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
        ftpClient = new FTPClient();
    }

    private boolean downloadFile(String filePath, String newFilePath, String newFileName) throws IOException {
        boolean status = true;
        FileOutputStream fileOutputStream = null;
        if (connect()) {
            try {
                Log.i(TAG, "downFile: " + ftpClient.printWorkingDirectory());
                String fileName = "";
                if (!ftpClient.changeWorkingDirectory(filePath)) {
                    String[] paths = filePath.split("/");
                    fileName = paths[paths.length-1];
                    for (int i = 0;i < paths.length-1;i++) {
                        ftpClient.changeWorkingDirectory(paths[i]);
                    }
                }
                Log.i(TAG, "downFile: " + ftpClient.printWorkingDirectory());
                File targetPath = new File(newFilePath);
                if (!targetPath.exists()) {
                    targetPath.mkdir();
                    targetPath.setWritable(true);
                }

                if (newFileName.equals("")) newFileName = fileName;
                fileOutputStream = new FileOutputStream(new File(newFilePath + "/" + newFileName));
                ftpClient.enterLocalPassiveMode();
                status = ftpClient.retrieveFile(fileName, fileOutputStream);
            } catch (IOException e) {
                Log.e(TAG, "downFile: ", e);
                status = false;
            } finally {
                if (fileOutputStream != null) fileOutputStream.close();
                ftpClient.disconnect();
            }
        }
        return status;
    }


    private boolean connect() {
        boolean isConnect = false;
        try {
            ftpClient.connect(ip, port);
            isConnect = ftpClient.login(user, password);
        } catch (IOException e) {
            Log.e(TAG, "FTP服务器登录失败");
        }
        return isConnect;
    }

    @Override
    public void run() {
        Log.i(TAG, "download: start download file");
        boolean result = false;
        try {
            result = downloadFile(filePath, newPath, newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "download: " + result);
    }
}
