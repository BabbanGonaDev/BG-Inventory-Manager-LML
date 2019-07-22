package com.example.bg10.qrfinal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AllTransAdapter extends RecyclerView.Adapter<AllTransAdapter.VH> {
    final ArrayList<Transaction> transactions; //holds the list of all transactions in database as transaction objects, declared final because it is used in an inner class
    private final OnItemClickListener listener; //this is the listener that is set on view click, declared final because  "                  "                  "
    Context context;    //this is used to store the context where the adapter will be used

    //Constructor initializes all of the above, notice it is the same as the normal constructor except now listener is added
    public AllTransAdapter(Context context, ArrayList<Transaction> transactions, OnItemClickListener listener) {
        this.transactions = transactions;
        this.context = context;
        this.listener = listener;
    }

    //Gets the view holder i.e the layout to hold each transaction view
    @Override
    public AllTransAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.all_trans_row, parent, false);
        return new VH(v);       //returns the view holder class below with the textviews assigned to ids
    }

    @Override
    public void onBindViewHolder(AllTransAdapter.VH holder, int position) {
        Transaction trans = this.transactions.get(position);   //gets current transaction object from array

        holder.tvTransactionid.setText(trans.getId());
        holder.tvIn.setText(trans.getIn());
        holder.tvOut.setText(trans.getOut());
        holder.tvUnit.setText(trans.getUnit());
        holder.tvItemName.setText(trans.getItemName());
        holder.tvDate.setText(trans.getDate());
        if (trans.getSync().equals("yes")) {
            holder.imgSync.setImageResource(R.drawable.ic_done_black_18dp);
        } else {
            holder.imgSync.setImageResource(R.drawable.ic_update_black_18dp);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public interface OnItemClickListener {      //this is the interface that enables the onclick to work
        void onClick(Transaction Transaction);
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView tvTransactionid;
        TextView tvIn;
        TextView tvOut;
        TextView tvUnit;
        TextView tvItemName;
        TextView tvDate;
        ImageView imgSync;

        public VH(View v) {
            super(v);
            tvTransactionid = v.findViewById(R.id.tvTransID);
            tvIn = v.findViewById(R.id.tvIn);
            tvOut = v.findViewById(R.id.tvOut);
            tvUnit = v.findViewById(R.id.tvUnit);
            tvItemName = v.findViewById(R.id.tvItemName);
            tvDate = v.findViewById(R.id.tvDate);
            imgSync = v.findViewById(R.id.imgSync);

            //on click listener for each main transaction
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(transactions.get(getLayoutPosition()));
                }
            });
        }
    }
}
