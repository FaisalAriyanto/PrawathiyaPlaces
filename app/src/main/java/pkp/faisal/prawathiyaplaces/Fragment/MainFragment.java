package pkp.faisal.prawathiyaplaces.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pkp.faisal.prawathiyaplaces.Function.DividerItemDecoration;
import pkp.faisal.prawathiyaplaces.Function.FirebaseConstant;
import pkp.faisal.prawathiyaplaces.Function.HttpRequest;
import pkp.faisal.prawathiyaplaces.Model.PlacesModel;
import pkp.faisal.prawathiyaplaces.Function.OnLoadMoreListener;
import pkp.faisal.prawathiyaplaces.Adapter.PlacesAdapter;
import pkp.faisal.prawathiyaplaces.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements
        PlacesAdapter.onCallback,
        OnLoadMoreListener {
    private static final String CATEGORY_PARAM = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private PlacesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager1;
    private ArrayList<PlacesModel> placesBase = new ArrayList<>();
    private String category;
    private String mParam2;
    private String currentCategoryId;
    private String lastKey = null;
    private final static int QUERY_LIMIT = 10;
    private int page = 0;
    private OnFragmentInteractionListener mListener;
    final int[] dataLoaded = {0};
    private HttpRequest http;

    public MainFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_PARAM, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(CATEGORY_PARAM);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_places);
        recyclerView.setHasFixedSize(true);
        mLayoutManager1 = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager1);
        mAdapter = new PlacesAdapter(placesBase, getActivity(), recyclerView, this, this);
        recyclerView.setAdapter(mAdapter);
        http = new HttpRequest(getContext());
        setHasOptionsMenu(true);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        seedData();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(PlacesModel PlacesModel) {

    }

    @Override
    public void onLoadMore() {
        loadComments();

//        seedData();
//        if (placesBase.size() <= 20) {
//            placesBase.add(null);
//            mAdapter.notifyItemInserted(placesBase.size() - 1);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    placesBase.remove(placesBase.size() - 1);
//                    mAdapter.notifyItemRemoved(placesBase.size());
//
//                    //Generating more data
//                    int index = placesBase.size();
//                    int end = index + 10;
//                    for (int i = index; i < end; i++) {
//                        PlacesModel contact = new PlacesModel("" + i, "" + i, "" + i, "" + i, "" + i, "" + i);
//
//                        placesBase.add(contact);
//                    }
//                    mAdapter.notifyDataSetChanged();
//                    mAdapter.setLoaded();
//                }
//            }, 1000);
//        } else {
//            Toast.makeText(getActivity(), "Loading data completed", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_act_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                Log.d("", "");
                SyncAsTask sync = new SyncAsTask();
                sync.execute();

                return true;
//            case R.id.action_sync:
//                syncData();
//                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private class SyncAsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
//            if (!currentCategoryId.equals("-Kjf8dgaeCYlpnHRQ2sg")) {
//                return null;
//            }

            final DatabaseReference placeRef = FirebaseDatabase.getInstance().getReference(FirebaseConstant.PLACES);

            placeRef.orderByChild(FirebaseConstant.MASTER_CATEGORY + "Id")
                    .equalTo(currentCategoryId)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            PlacesModel placesModel = dataSnapshot.getValue(PlacesModel.class);
//                            placesModel.Uid = dataSnapshot.getKey();
                            placeRef
                                    .child(dataSnapshot.getKey())
                                    .child("PlaceId")
                                    .setValue(dataSnapshot.getKey());
//                            switch (placesModel.MasterCategoryId) {
//                                case "-Kjf8dgaeCYlpnHRQ2sg":
//                                    placesModel.MasterCategoryId = "1";
//                                    break;
//                                case "-Kjf93xTJiQrPEoFnqIz":
//                                    placesModel.MasterCategoryId = "2";
//                                    break;
//                                case "-KjgANirWW5TLxhtcJov":
//                                    placesModel.MasterCategoryId = "3";
//                                    break;
//                                case "-KjkfHX6h8PcDn4pVxtW":
//                                    placesModel.MasterCategoryId = "4";
//                                    break;
//                                case "-KjkfMaa7EMrMBmKAAbD":
//                                    placesModel.MasterCategoryId = "5";
//                                    break;
//                                case "-KjkfV15ZmY9lLPEWXny":
//                                    placesModel.MasterCategoryId = "6";
//                                    break;
//                            }
//                            http.savePlaces(placesModel, new HttpRequest.SuccessCallback() {
//                                @Override
//                                public void onHttpPostSuccess(String result) {
//                                    Log.d("", "");
//                                }
//                            }, new HttpRequest.ErrorCallback() {
//                                @Override
//                                public void onHttpPostError(VolleyError error) {
//                                    Log.d("", "");
//                                }
//                            });
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            return null;
        }

    }

    private void loadComments() {
        page++;
        if (dataLoaded[0] > 0 && dataLoaded[0] < QUERY_LIMIT) {
            Toast.makeText(getContext(), "All data loaaded", Toast.LENGTH_SHORT).show();
            return;
        }

        if (placesBase.size() == 0) {
            placesBase.add(null);
            mAdapter.notifyItemInserted(placesBase.size() - 1);
        } else if (placesBase.get(placesBase.size() - 1) != null) {
            placesBase.add(null);
            mAdapter.notifyItemInserted(placesBase.size() - 1);
        }

        dataLoaded[0] = 0;
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                dataLoaded[0]++;
                if (dataLoaded[0] == QUERY_LIMIT) {
                    lastKey = dataSnapshot.getKey();
                } else if (dataLoaded[0] == 1) {
                    placesBase.remove(placesBase.size() - 1);
                    mAdapter.notifyItemRemoved(placesBase.size());
                } else {
                    final PlacesModel places = new PlacesModel();
                    places.Uid = dataSnapshot.getKey();
                    places.Name = dataSnapshot.getValue().toString();
                    placesBase.add(places);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setLoaded();

                    FirebaseDatabase.getInstance().getReference(FirebaseConstant.PLACES)
                            .child(dataSnapshot.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    PlacesModel placesModel = dataSnapshot.getValue(PlacesModel.class);
                                    placesModel.Uid = dataSnapshot.getKey();
                                    for (int i = 0; i < placesBase.size(); i++) {
                                        if (placesBase.get(i) != null)
                                            if (placesBase.get(i).Uid.equals(placesModel.Uid)) {
                                                placesBase.set(i, placesModel);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", "");
            }
        };


        if (lastKey != null) {
            FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstant.PLACE_CATEGORY)
                    .child(currentCategoryId)
                    .orderByKey()
                    .limitToFirst(QUERY_LIMIT)
                    .startAt(lastKey)
                    .addChildEventListener(childEventListener);
        } else {
            FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstant.PLACE_CATEGORY)
                    .child(currentCategoryId)
                    .orderByKey()
                    .limitToFirst(QUERY_LIMIT)
                    .addChildEventListener(childEventListener);
        }
    }

    private void seedData() {
        FirebaseDatabase.getInstance().getReference(FirebaseConstant.MASTER_CATEGORY)
                .orderByChild("Value")
                .equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            currentCategoryId = data.getKey();
                            loadComments();
//                            CategoryModel categoryModel = data.getValue(CategoryModel.class);
//                            FirebaseDatabase.getInstance()
//                                    .getReference(FirebaseConstant.PLACES)
//                                    .orderByChild("MasterCategoryId")
//                                    .equalTo(data.getKey())
//                                    .endAt(lastKey)
//                                    .limitToLast(QUERY_LIMIT * page)
//                                    .addChildEventListener(new ChildEventListener() {
//                                        @Override
//                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                            final PlacesModel places = dataSnapshot.getValue(PlacesModel.class);
//                                            places.Uid = dataSnapshot.getKey();
//                                            placesBase.add(places);
//                                            mAdapter.notifyDataSetChanged();
//
////                                            FirebaseDatabase.getInstance().getReference(FirebaseConstant.MASTER_CATEGORY)
////                                                    .child(places.MasterCategoryId)
////                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
////                                                        @Override
////                                                        public void onDataChange(DataSnapshot dataSnapshot) {
////                                                            CategoryModel categoryModel = dataSnapshot.getValue(CategoryModel.class);
////                                                            for (PlacesModel pModel : placesBase) {
////                                                                if (pModel.Uid.equals(places.Uid)) {
////                                                                    pModel.Category = categoryModel.Text;
////                                                                    mAdapter.notifyDataSetChanged();
////                                                                    break;
////                                                                }
////                                                            }
////                                                        }
////
////                                                        @Override
////                                                        public void onCancelled(DatabaseError databaseError) {
////
////                                                        }
////                                                    });
//                                        }
//
//                                        @Override
//                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                                            final PlacesModel places = dataSnapshot.getValue(PlacesModel.class);
//                                            places.Uid = dataSnapshot.getKey();
//                                            for (PlacesModel placesModel : placesBase) {
//                                                if (placesModel.Uid.equals(places.Uid)) {
//                                                    placesModel.PhotoUrl = places.PhotoUrl;
//                                                    mAdapter.notifyDataSetChanged();
//                                                    break;
//                                                }
//
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                                        }
//
//                                        @Override
//                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
}
