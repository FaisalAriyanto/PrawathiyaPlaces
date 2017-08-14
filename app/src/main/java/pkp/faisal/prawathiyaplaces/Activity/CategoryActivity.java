package pkp.faisal.prawathiyaplaces.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pkp.faisal.prawathiyaplaces.Adapter.CategoryAdapter;
import pkp.faisal.prawathiyaplaces.Function.DividerItemDecoration;
import pkp.faisal.prawathiyaplaces.Function.FirebaseConstant;
import pkp.faisal.prawathiyaplaces.Model.CategoryModel;
import pkp.faisal.prawathiyaplaces.R;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.onCallback {
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager1;
    private ArrayList<CategoryModel> categoryBase = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Category");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        recyclerView = (RecyclerView) findViewById(R.id.list_category);
        recyclerView.setHasFixedSize(true);
        mLayoutManager1 = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager1);
        mAdapter = new CategoryAdapter(categoryBase, this, recyclerView, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        seedData();
    }

    private void seedData() {
        FirebaseDatabase.getInstance().getReference(FirebaseConstant.MASTER_CATEGORY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            CategoryModel categoryModel = data.getValue(CategoryModel.class);
                            categoryModel.Uid = data.getKey();
                            categoryBase.add(categoryModel);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void onFabClicked(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_add_category, null);
        builder.setView(view);
        final EditText mValue = (EditText) view.findViewById(R.id.value);
        final EditText mText = (EditText) view.findViewById(R.id.text);

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Tambah", null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = mValue.getText().toString();
                String text = mText.getText().toString();
                CategoryModel categoryModel = new CategoryModel(text, value);
                FirebaseDatabase.getInstance().getReference(FirebaseConstant.MASTER_CATEGORY)
                        .push()
                        .setValue(categoryModel);
                dialog.dismiss();
//                //TODO check if store exist
//                final DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference(FireConstant.MASTERTOKO);
//                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FireConstant.USER);
//
//                storeRef.child(mKey.getText().toString())
//                        .child(FireConstant.NAMA_TOKO)
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.getValue() != null) {
//                                    storeRef.child(mKey.getText().toString())
//                                            .child(FireConstant.REQUEST)
//                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                            .setValue(true);
//
//                                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                            .child(FireConstant.REQUEST)
//                                            .child(mKey.getText().toString())
//                                            .setValue(true);
//                                    dialog.dismiss();
//                                } else {
//                                    mKey.setError(context.getString(R.string.wrong_store_key));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });


            }
        });
    }

    @Override
    public void onClick(CategoryModel CategoryModel) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Uid", CategoryModel.Uid);
        returnIntent.putExtra("Name", CategoryModel.Text);
        returnIntent.putExtra("Value", CategoryModel.Value);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
