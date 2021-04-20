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

    public static void loadImage(Context context, String imgUrl, ImageView imageView) {
        Glide.with(context)
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .dontAnimate()
                .error(R.mipmap.ic_launcher)
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
    public void clearMemoryCache() {

    }
}
