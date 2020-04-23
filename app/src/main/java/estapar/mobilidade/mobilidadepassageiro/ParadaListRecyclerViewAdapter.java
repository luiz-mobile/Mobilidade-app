package estapar.mobilidade.mobilidadepassageiro;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

/**
 * Created by alberto on 03/09/2017.
 */

public class ParadaListRecyclerViewAdapter extends RecyclerView.Adapter<ParadaListRecyclerViewAdapter.ViewHolder> {


    private ArrayList<Parada> paradas;


    public ParadaListRecyclerViewAdapter(ArrayList<Parada> paradas) {
        this.paradas = paradas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_paradaslist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = paradas.get(position);
        holder.mCircularName = holder.mItem.getTitle();
        //holder.mIdView.setText(holder.mItem.getnParada());
        holder.mContentView.setText(holder.mItem.getDescription());

    }

    @Override
    public int getItemCount() {
        return paradas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public final CardView mCardView;
        public Parada mItem;
        public String mCircularName;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.circimg);
            mCardView = (CardView) view.findViewById(R.id.card_view);

        }

    }
}
