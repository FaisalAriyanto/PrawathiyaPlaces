package pkp.faisal.prawathiyaplaces.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import pkp.faisal.prawathiyaplaces.Function.CnCImage;
import pkp.faisal.prawathiyaplaces.Function.CurrentLocation;
import pkp.faisal.prawathiyaplaces.Function.FirebaseConstant;
import pkp.faisal.prawathiyaplaces.Model.PlacesModel;
import pkp.faisal.prawathiyaplaces.R;

public class AddPlacesActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner mSprPlaceType;

    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;

    double mLatitude = 0;
    double mLongitude = 0;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CATEGORY_REQUEST_CODE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 3;
    private ImageView mPhoto;
    private ProgressBar mProgress;
    private TextView mCategory;
    private TextView mCheat;

    private String selectedCategoryId;
    private String currentLat;
    private String currentLon;

    private Uri selectedUri;
    private String newPlacesId;
    private EditText mName;
    private EditText mDesc;
    private CurrentLocation currentLocation;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Place");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);
        mPhoto = (ImageView) findViewById(R.id.photo);
        mPhoto.setOnClickListener(this);
        mCategory = (TextView) findViewById(R.id.pilih_category);
        mCheat = (TextView) findViewById(R.id.tingkat_lanjut);
        mName = (EditText) findViewById(R.id.nama);
        mDesc = (EditText) findViewById(R.id.desc);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Mengunggah");
        pDialog.setCancelable(false);
        mCheat.setOnClickListener(this);
        currentLocation = new CurrentLocation(this, MY_PERMISSIONS_REQUEST_LOCATION, new CurrentLocation.MyLocation() {
            @Override
            public void callback(String lat, String longitude) {
                currentLat = lat;
                currentLon = longitude;
            }
        });
        findViewById(R.id.category_btn).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_check:

                String name = mName.getText().toString();
                String desc = mDesc.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    mName.setError("Nama Harus Diisi");
                    return false;
                }

                if (TextUtils.isEmpty(desc)) {
                    mDesc.setError("Vicinity Harus Diisi");
                    return false;
                }


                if (TextUtils.isEmpty(selectedCategoryId)) {
                    mCategory.setError("Pilih Kategoru");
                    return false;
                }

                if (selectedUri == null) {
                    Toast.makeText(getApplicationContext(), "Belum mengambil photo", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (currentLat == null) {
                    Toast.makeText(getApplicationContext(), "Belum mengambil lokasi", Toast.LENGTH_SHORT).show();
                    return false;
                }
                pDialog.show();

                final PlacesModel placesModel = new PlacesModel(
                        name,
                        desc,
                        "",
                        selectedCategoryId,
                        currentLat,
                        currentLon);

                DatabaseReference newPlaces =
                        FirebaseDatabase
                                .getInstance()
                                .getReference(FirebaseConstant.PLACES)
                                .push();
                newPlacesId = newPlaces.getKey();
                newPlaces.setValue(placesModel, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            onUploadPhoto(selectedUri);

                            FirebaseDatabase.getInstance().getReference(FirebaseConstant.PLACE_CATEGORY)
                                    .child(selectedCategoryId)
                                    .child(newPlacesId)
                                    .setValue(placesModel.Name);

                        } else {
                            Toast.makeText(getApplicationContext(), "Error save " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        CnCImage.profileCrop(this);
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    mProgress.setVisibility(View.VISIBLE);
                    selectedUri = resultUri;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        mPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Sorry! Failed to crop image" + e, Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;

            case CATEGORY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    selectedCategoryId = data.getStringExtra("Uid");
                    String name = data.getStringExtra("Name");
                    mCategory.setText(name);
                } else {

                }

                break;

            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    currentLocation.startLocationUpdates();
                } else {
                    Toast.makeText(getApplicationContext(), "Lokasi Diperlukan!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;

        }
    }

    private void onUploadPhoto(Uri file) {
        StorageReference masterBarangRef =
                FirebaseStorage
                        .getInstance()
                        .getReference(FirebaseConstant.IMAGES)
                        .child(FirebaseConstant.PLACES)
                        .child(newPlacesId);

        UploadTask uploadTask = masterBarangRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                onImageUploadSuccess(downloadUrl);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") long n = taskSnapshot.getBytesTransferred();
                @SuppressWarnings("VisibleForTests") long v = taskSnapshot.getTotalByteCount();
                long percent = n * 100 / v;
                onImageUploadProgress(percent);
            }
        });
    }

    private void onImageUploadProgress(long precent) {
        mProgress.setProgress(Integer.valueOf(String.valueOf(precent)));
    }

    private void onImageUploadSuccess(Uri downloadUrl) {
        pDialog.dismiss();
        FirebaseDatabase.getInstance().getReference(FirebaseConstant.PLACES)
                .child(newPlacesId)
                .child("PhotoUrl")
                .setValue(downloadUrl.toString());
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CnCImage.profileTakePhoto(this, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else {
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION:
                currentLocation.startLocationUpdates();
                break;

        }
    }

    public void onPopover(Context context, View anchor) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.inflate(R.menu.upload_photo_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.camera:
                        CnCImage.onCapture(AddPlacesActivity.this,
                                MY_PERMISSIONS_REQUEST_CAMERA,
                                CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        break;
                    case R.id.gallery:

                        break;
                }
                return false;
            }
        });
        popup.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo:
                onPopover(getApplicationContext(), v);
                break;
            case R.id.category_btn:
                startActivityForResult(new Intent(this, CategoryActivity.class), CATEGORY_REQUEST_CODE);
                break;
            case R.id.tingkat_lanjut:
                startActivity(new Intent(this, CheatActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_category, menu);
        return true;
    }

}
