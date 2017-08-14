package pkp.faisal.prawathiyaplaces.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pkp.faisal.prawathiyaplaces.Model.CategoryModel;
import pkp.faisal.prawathiyaplaces.R;


/**
 * Created by Faisal on 3/8/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.DataObjectHolder> {
    public ArrayList<CategoryModel> mDataSet;
    private Activity activity;
    private onCallback callback;

    public CategoryAdapter(ArrayList<CategoryModel> myDataSet, Activity activity, RecyclerView recyclerView, onCallback callback) {
        mDataSet = myDataSet;
        this.activity = activity;
        this.callback = callback;
    }


    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view, parent);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        CategoryModel data = mDataSet.get(position);
        holder.setItem(mDataSet, activity, callback);
        holder.name.setText(data.Text);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class DataObjectHolder extends
            RecyclerView.ViewHolder {
        public ArrayList<CategoryModel> mItemSet;
        TextView name;
        Activity activity;

        private onCallback callback;

        public DataObjectHolder(final View view,
                                final ViewGroup viewGroup) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onClick(mItemSet.get(getAdapterPosition()));
                }
            });
        }

        public void setItem(ArrayList<CategoryModel> mDataSet, Activity activity, onCallback callback) {
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
        void onClick(CategoryModel CategoryModel);
    }


}
