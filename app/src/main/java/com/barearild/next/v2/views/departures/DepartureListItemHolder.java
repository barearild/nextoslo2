package com.barearild.next.v2.views.departures;

import android.animation.StateListAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.barearild.next.v2.LineColorService;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;

import v2.next.barearild.com.R;

public class DepartureListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView lineRef;
    public final TextView destinationName;
    public final TextView firstDeparture;
    public final TextView secondDeparture;
    public final TextView stopName;
    final ImageView warning;
    final Button menu;

    public DepartureListItemHolder(View view) {
        super(view);
        lineRef = (TextView) view.findViewById(R.id.departure_lineRef);
        destinationName = (TextView) view.findViewById(R.id.departure_destinationName);
        firstDeparture = (TextView) view.findViewById(R.id.departure_first);
        secondDeparture = (TextView) view.findViewById(R.id.departure_second);
        stopName = (TextView) view.findViewById(R.id.departure_stopName);
        warning = (ImageView) view.findViewById(R.id.departure_deviation_warning);
        menu = (Button) view.findViewById(R.id.departure_menu);

        view.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimation(view);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setAnimation(View view) {
        StateListAnimator stateListAnimator = new StateListAnimator();
        view.setStateListAnimator(stateListAnimator);
    }

    private Context getContext() {
        return super.itemView.getContext();
    }

    public void setColor(StopVisit stopVisit) {
        int colorId = stopVisit.getLineColor();
        lineRef.setBackgroundColor(colorId);
        destinationName.setTextColor(colorId);
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
