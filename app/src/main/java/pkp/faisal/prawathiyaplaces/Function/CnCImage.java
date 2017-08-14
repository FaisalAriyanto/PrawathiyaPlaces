package pkp.faisal.prawathiyaplaces.Function;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

/**
 * Created by Faisal on 4/19/2017.
 */

public class CnCImage {
    private static final String IMAGE_DIRECTORY_NAME = "TEMP";

    private static Uri fileUri;

    public static void onCapture(
            Activity activity,
            final int MY_PERMISSIONS_REQUEST_CAMERA,
            int CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            profileTakePhoto(activity, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    public static void profileTakePhoto(Activity activity, int CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
        if (!profileCheckSupportCamera(activity)) {
            Toast.makeText(activity, "Device didnt support camera", Toast.LENGTH_SHORT).show();
            return;
        }
        String imageName = "img_temp";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = profileGetOutputMediaFile(imageName);
        fileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        activity.startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private static boolean profileCheckSupportCamera(Activity activity) {
        return activity.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private static File profileGetOutputMediaFile(String name) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + name + ".jpg");
        return mediaFile;
    }

    public static void profileCrop(Activity activity) {
        CropImage.activity(fileUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(100)
                .setRequestedSize(500, 500)
                .start(activity);
    }

}
