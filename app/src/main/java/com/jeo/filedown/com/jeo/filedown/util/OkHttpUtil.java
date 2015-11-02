package com.jeo.filedown.com.jeo.filedown.util;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by 志文 on 2015/10/30 0030.
 */
public class OkHttpUtil {
    private final static String TAG = OkHttpUtil.class.getName();
    private final static OkHttpClient client = new OkHttpClient();

    public static void downFile(final String url, final ProgressListener progressListener) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();


        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        };
        client.networkInterceptors().add(interceptor);

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        response.body().string();

        //此处移除监听,以防止重复监听之前的请求,造成内存溢出
        client.networkInterceptors().remove(interceptor);
    }


    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() throws IOException {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(sink, totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    public interface ProgressListener {
        void update(Buffer sink, long bytesRead, long contentLength, boolean done);
    }
}
