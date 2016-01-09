package com.barearild.next.v2.views.departures;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import v2.next.barearild.com.R;

public class DeparturesSwipeRefreshLayout extends SwipeRefreshLayout {

    public DeparturesSwipeRefreshLayout(Context context) {
        super(context);
        setColorSchemeResources(R.color.tramColorPrimary, R.color.regionalBusColorPrimary, R.color.busColorPrimary, R.color.metroColorPrimary, R.color.boatColorPrimary);
    }

    public DeparturesSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeResources(R.color.tramColorPrimary, R.color.regionalBusColorPrimary, R.color.busColorPrimary, R.color.metroColorPrimary, R.color.boatColorPrimary);
    }
}
