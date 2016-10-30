package com.sneakred.securitycam.services;

import android.content.Context;

import com.sneakred.securitycam.Constants;
import com.sneakred.securitycam.imgurmodel.ImageResponse;
import com.sneakred.securitycam.imgurmodel.ImgurAPI;
import com.sneakred.securitycam.imgurmodel.Upload;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class UploadService {
    public final static String TAG = UploadService.class.getSimpleName();

    private WeakReference<Context> mContext;

    public UploadService(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public void Execute(Upload upload, Callback<ImageResponse> callback) {
        final Callback<ImageResponse> cb = callback;

        RestAdapter restAdapter = buildRestAdapter();

        restAdapter.create(ImgurAPI.class).postImage(
                Constants.getClientAuth(),
                upload.title,
                upload.description,
                upload.albumId,
                null,
                new TypedFile("image/*", upload.image),
                new Callback<ImageResponse>() {
                    @Override
                    public void success(ImageResponse imageResponse, Response response) {
                        if (cb != null) cb.success(imageResponse, response);
                        if (response == null) {
                            // Image not uploaded
                            return;
                        }

                        if (imageResponse.success) {
                            //notificationHelper.createUploadedNotification(imageResponse);
                            //System.out.println(imageResponse.data.link);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (cb != null) cb.failure(error);
                        //notificationHelper.createFailedUploadNotification();
                    }
                });
    }

    private RestAdapter buildRestAdapter() {
        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setEndpoint(ImgurAPI.server)
                .build();

        /*
        Set rest adapter logging if we're already logging
        */
        if (Constants.LOGGING)
            imgurAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        return imgurAdapter;
    }
}
