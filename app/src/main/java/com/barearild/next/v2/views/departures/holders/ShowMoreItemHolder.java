package com.barearild.next.v2.views.departures.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import v2.next.barearild.com.R;

/**
 * Created by arild on 12.03.2017.
 */

public class ShowMoreItemHolder extends RecyclerView.ViewHolder {

    public Button showMore;

    public ShowMoreItemHolder(View itemView) {
        super(itemView);
        showMore = (Button) itemView.findViewById(R.id.show_more_button);
    }
}
