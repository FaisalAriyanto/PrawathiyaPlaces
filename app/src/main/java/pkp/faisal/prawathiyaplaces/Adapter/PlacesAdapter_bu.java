package pkp.faisal.prawathiyaplaces.Adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pkp.faisal.prawathiyaplaces.Model.PlacesModel;
import pkp.faisal.prawathiyaplaces.Function.OnLoadMoreListener;
import pkp.faisal.prawathiyaplaces.R;


/**
 * Created by Faisal on 3/8/2017.
 */

public class PlacesAdapter_bu extends RecyclerView.Adapter<PlacesAdapter_bu.DataObjectHolder> {
    public ArrayList<PlacesModel> mDataSet;
    private Activity activity;
    private onCallback callback;

    private boolean loading;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading = false;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;


    public PlacesAdapter_bu(ArrayList<PlacesModel> myDataSet, Activity activity, RecyclerView recyclerView, onCallback callback, OnLoadMoreListener mOnLoadMoreListener) {
        mDataSet = myDataSet;
        this.activity = activity;
        this.callback = callback;
//        this.onLoadMoreListener = mOnLoadMoreListener;
//
//        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                totalItemCount = linearLayoutManager.getItemCount();
//                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                    if (onLoadMoreListener != null) {
//                        onLoadMoreListener.onLoadMore();
//                    }
//                    isLoading = true;
//                }
//            }
//        });
//        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//
//            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
//                    .getLayoutManager();
//
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView,
//                                       int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//
//                    totalItemCount = linearLayoutManager.getItemCount();
//                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                        // End has been reached
//                        // Do something
//                        if (onLoadMoreListener != null) {
//                            onLoadMoreListener.onLoadMore();
//                        }
//                        loading = true;
//                    }
//                }
//            });
//        }
    }

    public void setLoaded() {
        isLoading = false;
    }
//    @Override
//    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_places, parent, false);
//        DataObjectHolder dataObjectHolder = new DataObjectHolder(view, parent);
//        return dataObjectHolder;
//    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == VIEW_TYPE_ITEM) {
//            View view = LayoutInflater.from(activity).inflate(R.layout.item_places, parent, false);
//            return new DataObjectHolder(view, parent);
//        } else if (viewType == VIEW_TYPE_LOADING) {
//            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
//            return new DataObjectHolder(view, parent);
//        }


        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_places, parent, false);
        return new DataObjectHolder(view, parent);

//        return null;
    }



    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        PlacesModel data = mDataSet.get(position);
        holder.setItem(mDataSet, activity, callback);

        try {
            holder.name.setText(data.Name);
            holder.desc.setText(data.Vicinity);
            holder.photo.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_image));

            Glide.with(activity)
                    .load(data.PhotoUrl)
                    .into(holder.photo);

        } catch (Exception e) {
//            holder.name.setText("Null");
//            holder.desc.setText("Null");

        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class DataObjectHolder extends
            RecyclerView.ViewHolder {
        public ArrayList<PlacesModel> mItemSet;
        TextView name;
        TextView desc;
        ImageView photo;
        Activity activity;

        private onCallback callback;

        public DataObjectHolder(final View view,
                                final ViewGroup viewGroup) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            desc = (TextView) view.findViewById(R.id.desc);
            photo = (ImageView) view.findViewById(R.id.photo);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onClick(mItemSet.get(getAdapterPosition()));
                }
            });
        }



        public void setItem(ArrayList<PlacesModel> mDataSet, Activity activity, onCallback callback) {
            mItemSet = mDataSet;
            this.activity = activity;
            this.callback = callback;
        }

//        @OnClick(R.id.request)
//        public void request() {
//            Log.d("", "");
//
//            Intent i = new Intent(activity, RequestActivity.class);
//            i.putExtra("request", mItemSet.get(getAdapterPosition()).Request);
//            i.putExtra("id", mItemSet.get(getAdapterPosition()).MasterTokoId);
//
//            activity.startActivity(i);
//        }
    }

    public interface onCallback {
        void onClick(PlacesModel PlacesModel);
    }


}
