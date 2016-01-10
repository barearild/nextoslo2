package com.barearild.next.v2.views.details;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.views.departures.StopVisitListItem;
import com.squareup.picasso.Picasso;

import v2.next.barearild.com.R;

public class DetailsActivity extends AppCompatActivity {

    private ImageView mMapView;
    private View mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StopVisitListItem stopVisitListItem = getIntent().getParcelableExtra("stopvisit");
        switch (stopVisitListItem.getTransporttype()) {
            case Tram:
                setTheme(R.style.TramTheme);
                break;
            case Bus:
                setTheme(R.style.BusTheme);
                break;
            case Metro:
                setTheme(R.style.MetroTheme);
                break;
            case Train:
            case AirportTrain:
                setTheme(R.style.TrainTheme);
                break;
            case RegionalBus:
                setTheme(R.style.RegionalBusTheme);
                break;
            case Boat:
                setTheme(R.style.BoatTheme);
                break;
            default:
                setTheme(R.style.NextTheme);
                break;
        }

        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRoot = findViewById(R.id.coordinatorLayout);
        mMapView = (ImageView) findViewById(R.id.map);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        TextView title = (TextView) findViewById(R.id.title);
        TextView subtitle = (TextView) findViewById(R.id.subtitle);

//        title.setText(stopVisitListItem.getLineName());
        subtitle.setText(stopVisitListItem.getStop().getName());
        collapsingToolbarLayout.setTitle(stopVisitListItem.getLineName());

        downloadMap(stopVisitListItem);

    }

    private void downloadMap(StopVisitListItem stopVisitListItem) {
        Log.d("nextnext", "Loading map...");
        double[] latLon = CoordinateConversion.utm2LatLon(stopVisitListItem.getStop().getX(), stopVisitListItem.getStop().getY());
        Point imageSize = calculateImageSize();
        String url = "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + latLon[0] + "," + latLon[1] +
                "&zoom=15" +
                "&size=" + imageSize.x + "x" + imageSize.y +
                "&markers=%7C" + latLon[0] + "," + latLon[1] +
                "&key=" + NextOsloApp.GOOGLE_API_KEY;

        Picasso.with(getApplicationContext())
                .load(url)
                .resize(imageSize.x, imageSize.y)
                .into(mMapView);
    }

    private Point calculateImageSize() {
        Point screnSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screnSize);
        Log.d("nextnext", "screnSize = " + screnSize.toString());
        mRoot.measure(screnSize.x, screnSize.y);
        float actualWidth = mMapView.getMeasuredWidth();
        float actualHeight = mMapView.getMeasuredHeight();
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = screnSize.x / screnSize.y;

        if (imgRatio != maxRatio) {
            if (imgRatio < maxRatio) {
                imgRatio = screnSize.y / actualHeight;
                actualWidth = imgRatio * actualWidth;
                actualHeight = screnSize.y;
            } else {
                imgRatio = screnSize.x / actualWidth;
                actualHeight = imgRatio * actualHeight;
                actualWidth = screnSize.x;
            }
        }

        return new Point((int) mRoot.getMeasuredWidth(), (int) getResources().getDimension(R.dimen.app_bar_details_height));

    }
}
