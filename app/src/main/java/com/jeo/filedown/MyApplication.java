package com.jeo.filedown;

import android.app.Application;
import android.os.Environment;

import com.jeo.downlibrary.DownLoadConfig;
import com.jeo.downlibrary.DownLoadManager;

import java.io.File;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DownLoadManager downLoadManager = DownLoadManager.getInstance();
        //use the default config
//        downLoadManager.init(getApplicationContext());

        DownLoadConfig.Builder builder = new DownLoadConfig.Builder(getApplicationContext());

        String downPath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            downPath = Environment.getExternalStorageDirectory() + File.separator + "wisp";
        } else {
            downPath = Environment.getDataDirectory() + File.separator + "wisp";
        }
        File downFile = new File(downPath);
        if (!downFile.isDirectory() && !downFile.mkdirs()) {
            throw new IllegalAccessError("can not create downLoad folder");
        }
        builder.setDownloadSavePath(downPath);
        builder.setMaxDownloadThread(5);//线程池大小
        downLoadManager.init(builder.build(), getApplicationContext());
    }

}
