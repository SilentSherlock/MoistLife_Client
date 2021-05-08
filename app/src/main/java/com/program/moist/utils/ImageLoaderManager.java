package com.program.moist.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.loader.ImageLoader;
import com.program.moist.R;

import java.io.File;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: describe the class
 */
public class ImageLoaderManager implements ImageLoader {

    public static void loadImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(new File(path))
                .placeholder(R.mipmap.avatar_default)
                .dontAnimate()
                .error(R.mipmap.avatar_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadImageWeb(Context context, String imgURL, ImageView imageView) {
        Glide.with(context)
                .load(imgURL)
                .placeholder(R.mipmap.background_default)
                .dontAnimate()
                .error(R.mipmap.background_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
                .load(new File(path))
                .placeholder(R.mipmap.ic_launcher)
                .dontAnimate()
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {

    }

    @Override
    public void clearMemoryCache() {

    }
}
