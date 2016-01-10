package com.barearild.next.v2.views.departures;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class DeparturesRecyclerView extends RecyclerView {

    final LinearLayoutManager layoutManager;

    public DeparturesRecyclerView(Context context) {
        super(context);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        init(context);
    }

    public DeparturesRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        init(context);
    }

    public DeparturesRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        init(context);
    }

    private void init(Context context) {
        setHasFixedSize(true);
        setLayoutManager(layoutManager);
        setItemAnimator(new DefaultItemAnimator());
        setAdapter(new DeparturesAdapter(context));
    }
}
