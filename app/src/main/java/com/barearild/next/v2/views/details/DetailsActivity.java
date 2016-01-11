package com.barearild.next.v2.views.details;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.views.departures.StopVisitListItem;
import com.squareup.picasso.Picasso;

import v2.next.barearild.com.R;

public class DetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.7f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 400;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private View mRoot;
    private Toolbar mToolbar;
    private TextView mTitle;
    private LinearLayout mTitleContainer;
    private AppBarLayout mAppBarLayout;
    private ImageView mImageparallax;
    private FrameLayout mFrameParallax;
    private FloatingActionButton mFab;

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

        bindActivity();

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppBarLayout.addOnOffsetChangedListener(this);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        ImageView icon = (ImageView) findViewById(R.id.icon);
        TextView title = (TextView) findViewById(R.id.title);
        TextView subtitle = (TextView) findViewById(R.id.subtitle);

//        icon.setImageResource(stopVisitListItem.getTransporttype().getImageResId());

        title.setText(stopVisitListItem.getLineName());
        subtitle.setText(stopVisitListItem.getStop().getName());

        mTitle.setText(stopVisitListItem.getLineName());

        startAlphaAnimation(mToolbar, 0, View.INVISIBLE);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        initParallaxValues();

        downloadMap(stopVisitListItem);
    }

    private void bindActivity() {
        mRoot = findViewById(R.id.coordinatorLayout);
        mToolbar        = (Toolbar) findViewById(R.id.toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.app_bar);
        mImageparallax = (ImageView) findViewById(R.id.map);
        mFrameParallax  = (FrameLayout) findViewById(R.id.mainFramelayoutTitle);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initParallaxValues() {
        CollapsingToolbarLayout.LayoutParams petDetailsLp =
                (CollapsingToolbarLayout.LayoutParams) mImageparallax.getLayoutParams();

        CollapsingToolbarLayout.LayoutParams petBackgroundLp =
                (CollapsingToolbarLayout.LayoutParams) mFrameParallax.getLayoutParams();

        petDetailsLp.setParallaxMultiplier(0.9f);
        petBackgroundLp.setParallaxMultiplier(0.3f);

        mImageparallax.setLayoutParams(petDetailsLp);
        mFrameParallax.setLayoutParams(petBackgroundLp);
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
                .into(mImageparallax);
    }

    private Point calculateImageSize() {
        Point screnSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screnSize);

        screnSize.y = (int) getResources().getDimension(R.dimen.app_bar_details_height);

        return screnSize;

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mToolbar, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }



        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mToolbar, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }


        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
            mFab.hide();

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
            mFab.show();
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
