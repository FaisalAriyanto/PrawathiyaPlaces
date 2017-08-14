package pkp.faisal.prawathiyaplaces.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pkp.faisal.prawathiyaplaces.Function.FirebaseConstant;
import pkp.faisal.prawathiyaplaces.Fragment.MainFragment;
import pkp.faisal.prawathiyaplaces.Model.CategoryModel;
import pkp.faisal.prawathiyaplaces.Adapter.PagerAdapter;
import pkp.faisal.prawathiyaplaces.R;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, MainFragment.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Prawathiya Places");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.setOnPageChangeListener(this);


        FirebaseDatabase.getInstance().getReference(FirebaseConstant.MASTER_CATEGORY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            CategoryModel categoryModel = data.getValue(CategoryModel.class);
                            pagerAdapter.addFragment(MainFragment.newInstance(categoryModel.Value, ""), categoryModel.Text);
                        }
                        viewPager.setAdapter(pagerAdapter);
//                        viewPager.setOffscreenPageLimit(Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount())));
                        tabLayout.setupWithViewPager(viewPager);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void onFabClicked(View view) {
        startActivity(new Intent(this, AddPlacesActivity.class));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
