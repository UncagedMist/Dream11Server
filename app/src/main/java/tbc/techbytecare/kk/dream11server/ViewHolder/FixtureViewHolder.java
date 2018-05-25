package tbc.techbytecare.kk.dream11server.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tbc.techbytecare.kk.dream11server.R;

public class FixtureViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgFirstOpponent,imgSecondOpponent;
    public TextView txtSeriesName,txtTimer;

    public FixtureViewHolder(View itemView) {
        super(itemView);

        imgFirstOpponent = itemView.findViewById(R.id.imgFirstOpponent);
        imgSecondOpponent = itemView.findViewById(R.id.imgSecondOpponent);

        txtSeriesName = itemView.findViewById(R.id.txtSeriesName);
        txtTimer = itemView.findViewById(R.id.txtTimer);
    }
}
