package com.barearild.next.v2.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.barearild.next.v2.LineColorService;
import com.barearild.next.v2.reisrest.Transporttype;

import v2.next.barearild.com.R;

public class DepartureListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    final TextView lineRef;
    final TextView destinationName;
    final TextView firstDeparture;
    final TextView secondDeparture;
    final TextView stopName;
    final ImageView deviationWarning;
    final Button menu;

    public DepartureListItemHolder(View view) {
        super(view);
        lineRef = (TextView) view.findViewById(R.id.departure_lineRef);
        destinationName = (TextView) view.findViewById(R.id.departure_destinationName);
        firstDeparture = (TextView) view.findViewById(R.id.departure_first);
        secondDeparture = (TextView) view.findViewById(R.id.departure_second);
        stopName = (TextView) view.findViewById(R.id.departure_stopName);
        deviationWarning = (ImageView) view.findViewById(R.id.departure_deviation_warning);
        menu = (Button) view.findViewById(R.id.departure_menu);

        view.setOnClickListener(this);
    }

    private Context getContext() {
        return super.itemView.getContext();
    }

    public void setColor(Transporttype transporttype) {
        int lineColor = LineColorService.lineColor(getContext(), transporttype);
        lineRef.setBackgroundColor(lineColor);
        destinationName.setTextColor(lineColor);
    }


    @Override
    public void onClick(View v) {
    }
}
