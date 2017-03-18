package com.barearild.next.v2.views.departures.holders;


import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import v2.next.barearild.com.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView headerText;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        headerText = (TextView) itemView.findViewById(R.id.departure_list_item_header);
        headerText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
