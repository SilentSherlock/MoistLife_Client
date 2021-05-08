package com.program.moist.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.util.Date;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/8
 * Description: 对象存储工具类
 */
public class OssUtil {

    public interface OssUpCallback {

        void onSuccess(String imgName,String imgUrl);

        void onFail(String message);

        void onProgress(long progress,long totalSize);

    }

    private OSSClient ossClient;
    private static OssUtil ossUtil;
    private String endpoint = "oss-cn-shanghai.aliyuncs.com";
    private String bucket = "moist-life";

    public OssUtil() {

    }

    public static OssUtil getInstance() {
        if (ossUtil == null) {
            ossUtil = new OssUtil();
        }
        return ossUtil;
    }

    /**
     * 上传图片 上传文件
     * @param context application上下文对象
     * @param ossUpCallback 成功的回调
     * @param imgName 上传到oss后的文件名称，图片要记得带后缀 如：.jpg
     * @param imgPath 图片的本地路径
     */
    public void uploadImage(Context context, String accessKeyId, String secretKeyId, String securityToken,
                            final OssUtil.OssUpCallback ossUpCallback,
                            String imgName, String imgPath) {
        Log.i(TAG, "upImage: 进入upImage");
        getOSS(context,accessKeyId,secretKeyId,securityToken);
        Log.i(TAG, "upImage: 构建OssClient");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, imgName, imgPath);
        putObjectRequest.setProgressCallback((OSSProgressCallback) (request, currentSize, totalSize) -> ossUpCallback.onProgress(currentSize, totalSize));
        Log.i(TAG, "upImage: 开始上传");
        ossClient.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.i(TAG, "onSuccess: " + result.getStatusCode());
                ossUpCallback.onSuccess(imgName, imgPath);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {

            }
        });

    }

    public void uploadMultiImage(Context context, String accessKeyId, String secretKeyId, String securityToken,
                                 final OssUtil.OssUpCallback ossUpCallback,
                                 String imgName, String imgPath) {

    }

    public void deleteImage(Context context,
                            String accessKeyId, String accessKeySecret, String securityToken,
                            String objectKey) {
        getOSS(context, accessKeyId, accessKeySecret, securityToken);
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, objectKey);
        ossClient.asyncDeleteObject(deleteObjectRequest, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                Log.i(TAG, "onSuccess: deleteImage success " + result.getStatusCode());
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientException, ServiceException serviceException) {
                Log.e(TAG, "onFailure: deleteImage failed", serviceException);
            }
        });
    }
    private void getOSS(Context context, String accessKeyId, String accessKeySecret, String token) {
        OSSCredentialProvider ossCredentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, token);
        Log.i(TAG, "upImage: 构建OssClient配置");
        ClientConfiguration conf = ClientConfiguration.getDefaultConf();
        conf.setConnectionTimeout(15 *1000);// 连接超时，默认15秒
        conf.setSocketTimeout(15 *1000);// socket超时，默认15秒
        conf.setMaxConcurrentRequest(5);// 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2);// 失败后最大重试次数，默认2次
        ossClient = new OSSClient(context, endpoint, ossCredentialProvider);
    }

}
