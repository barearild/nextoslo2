package com.barearild.next.v2.views.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.StopVisitFilters;
import com.barearild.next.v2.favourites.FavouritesService;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.reisrest.Requests;
import com.barearild.next.v2.reisrest.StopVisit.Deviation;
import com.barearild.next.v2.reisrest.StopVisit.DeviationDetails;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.views.departures.DeparturesSwipeRefreshLayout;
import com.barearild.next.v2.views.departures.StopVisitListItem;
import com.barearild.next.v2.views.map.MapsActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import v2.next.barearild.com.R;

public class DetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.7f;
    private static final int ALPHA_ANIMATIONS_DURATION = 400;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;


    private ViewGroup mRoot;
    private Toolbar mToolbar;
    private TextView mTitle;
    private LinearLayout mTitleContainer;
    private AppBarLayout mAppBarLayout;
    private ImageView mImageparallax;
    private FrameLayout mFrameParallax;
    private FloatingActionButton mFab;
    private GridView mDeparturesGrid;
    private StopVisitListItem mStopVisitListItem;
    private ListView mWarnings;
    private RelativeLayout mDeviationDetails;
    private DeparturesSwipeRefreshLayout mSwipeRefreshLayout;

    private FavouritesService mFavouritesService;
    private LinearLayout mFavouritePanel;
    private int mPrimaryColorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // inside your activity (if you did not enable transitions in your theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
// set an enter transition
            getWindow().setEnterTransition(new Explode());
// set an exit transition
            getWindow().setExitTransition(new Explode());
            getWindow().setSharedElementEnterTransition(new ChangeImageTransform());
            getWindow().setSharedElementExitTransition(new ChangeImageTransform());
            getWindow().setSharedElementsUseOverlay(true);
        }

        mStopVisitListItem = getIntent().getParcelableExtra(StopVisitListItem.class.getSimpleName());
        switch (mStopVisitListItem.getTransporttype()) {
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

        mFavouritesService = new FavouritesService(this);

        mToolbar.setTitle(mStopVisitListItem.getLineName());
        mToolbar.setSubtitle(mStopVisitListItem.getStop().getName());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppBarLayout.addOnOffsetChangedListener(this);

        TextView title = (TextView) findViewById(R.id.title);
        TextView subtitle = (TextView) findViewById(R.id.subtitle);

        title.setText(mStopVisitListItem.getLineName());
        subtitle.setText(mStopVisitListItem.getStop().getName());

//        mTitle.setText(mStopVisitListItem.getLineName());

        startAlphaAnimation(mToolbar, 0, View.INVISIBLE);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        initParallaxValues();

        downloadMap(mStopVisitListItem);
        initColors();
        initFabButton();
        initDeparturesGrid();
        initFavourites();
        initWarnings();
        initSwipeRefreshLayout();

        getAllStops();

        mImageparallax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(DetailsActivity.this, MapsActivity.class);
                double[] latLon = CoordinateConversion.utm2LatLon(mStopVisitListItem.getStop().getX(), mStopVisitListItem.getStop().getY());
                mapIntent.putExtra("latLng", latLon);
                mapIntent.putExtra("stopName", mStopVisitListItem.getStop().getName());

                String transitionName = getString(R.string.transition_map);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        DetailsActivity.this, mImageparallax, transitionName
                );

//                ActivityCompat.startActivity(DetailsActivity.this, mapIntent, options.toBundle());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(mapIntent, options.toBundle());
                } else {
                    startActivity(mapIntent);
                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new UpdateDeparturesTask().execute(mStopVisitListItem);
    }

    private void initColors() {
        mPrimaryColorId = mStopVisitListItem.firstDeparture().getLineColor();
        mImageparallax.setBackgroundColor(mPrimaryColorId);
        mFrameParallax.setBackgroundColor(mPrimaryColorId);
        mToolbar.setBackgroundColor(mPrimaryColorId);
    }

    private void bindActivity() {
        mRoot = (ViewGroup) findViewById(R.id.details_root);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mImageparallax = (ImageView) findViewById(R.id.map);
        mFrameParallax = (FrameLayout) findViewById(R.id.mainFramelayoutTitle);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDeparturesGrid = (GridView) findViewById(R.id.departures);
        mFavouritePanel = (LinearLayout) findViewById(R.id.favouritePanel);
        mWarnings = (ListView) findViewById(R.id.warnings);
        mDeviationDetails = (RelativeLayout) findViewById(R.id.deviation_details);
        mSwipeRefreshLayout = (DeparturesSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    }

    private void getAllStops() {
        new GetAllStopsForLine().execute(mStopVisitListItem.getLineRef());
    }

    private void initFabButton() {
        boolean isFavourite = FavouritesService.isFavourite(mStopVisitListItem);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Resources.Theme theme = getTheme();
            mFab.setImageDrawable(isFavourite ? getResources().getDrawable(R.drawable.ic_favorite_white_48dp, theme) : getResources().getDrawable(R.drawable.ic_favorite_border_white_48dp, theme));
        } else {
            mFab.setImageDrawable(isFavourite ? getResources().getDrawable(R.drawable.ic_favorite_white_48dp) : getResources().getDrawable(R.drawable.ic_favorite_border_white_48dp));
        }


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FavouritesService.isFavourite(mStopVisitListItem)) {
                    mFavouritesService.removeFavourite(mStopVisitListItem);
                    mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_48dp));
                    mFavouritePanel.setVisibility(View.GONE);
                } else {
                    mFavouritesService.addFavourite(mStopVisitListItem);
                    mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_48dp));
                    mFavouritePanel.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initFavourites() {
        boolean isFavourite = FavouritesService.isFavourite(mStopVisitListItem);

        mFavouritePanel.setVisibility(isFavourite ? View.VISIBLE : View.GONE);

        Spinner spinner = (Spinner) mFavouritePanel.findViewById(R.id.favouriteSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.favourites_selector, R.layout.favourite_selector_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mFavouritesService.setAsFavouriteOnStopOnly(mStopVisitListItem);
                        break;
                    case 1:
                        mFavouritesService.addAsFavouriteEverywhere(mStopVisitListItem);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initDeparturesGrid() {
        List<Map<String, String>> departureMap = new ArrayList<>();

        for (StopVisit stopvisit : mStopVisitListItem.getStopVisits()) {
            departureMap.add(departureMap(stopvisit));
        }

        String[] from = {"departure"};

        mDeparturesGrid.setAdapter(new SimpleAdapter(this, departureMap, R.layout.departure_time, from, new int[]{R.id.time}));
    }

    private void initWarnings() {
        mWarnings.setAdapter(new WarningListAdapter(this, mStopVisitListItem.getAllWarnings(this)));
    }

    private Map<String, String> departureMap(StopVisit stopVisit) {
        Map<String, String> map = new HashMap<>();

        map.put("departure", StopVisitListItem.departureTimeString(stopVisit, this));

        return map;
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

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
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
        mSwipeRefreshLayout.setEnabled(verticalOffset == 0);
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
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
            if (mIsTheTitleContainerVisible) {
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

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onRefresh() {
        Log.d(NextOsloApp.LOG_TAG, "Refresh");
        new UpdateDeparturesTask().execute(mStopVisitListItem);
    }

    private class WarningListAdapter extends BaseAdapter {

        private final Deviation[] deviations;
        private LayoutInflater inflater;

        public WarningListAdapter(Context context, List<Deviation> deviations) {
            this.deviations = deviations.toArray(new Deviation[deviations.size()]);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return deviations.length;
        }

        @Override
        public Deviation getItem(int position) {
            return deviations[position];
        }

        @Override
        public long getItemId(int position) {
            return deviations[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Deviation deviation = getItem(position);

            ViewHolder viewHolder = new ViewHolder();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.warning_deviation, parent, false);
                viewHolder.root = (LinearLayout) convertView.findViewById(R.id.deviation_root);
                viewHolder.text = (TextView) convertView.findViewById(R.id.text);
                viewHolder.moreButton = (Button) convertView.findViewById(R.id.moreButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.text.setText(deviation.getHeader());

            viewHolder.moreButton.setTextColor(mPrimaryColorId);
            View.OnClickListener moreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(NextOsloApp.LOG_TAG, "Load more " + deviation.getID());
                    new GetDeviationDetails(deviation.getHeader()).execute(deviation);
                }
            };
            viewHolder.moreButton.setOnClickListener(moreClickListener);

            if (deviation.getID() >= 0) {
                viewHolder.moreButton.setVisibility(View.VISIBLE);
                viewHolder.root.setOnClickListener(moreClickListener);
            } else {
                viewHolder.moreButton.setVisibility(View.GONE);
                viewHolder.root.setOnClickListener(null);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        LinearLayout root;
        TextView text;
        Button moreButton;
    }

    private class UpdateDeparturesTask extends AsyncTask<StopVisitListItem, Void, List<StopVisit>> {

        @Override
        protected List<StopVisit> doInBackground(StopVisitListItem... stopVisitListItems) {
            StopVisitListItem stopVisitListItem = stopVisitListItems[0];
            return StopVisitFilters.filterByLineId(stopVisitListItem.getId(), Requests.getAllDepartures(stopVisitListItem.getStop(), stopVisitListItem.getLinePublishedName()));
        }

        @Override
        protected void onPostExecute(List<StopVisit> stopVisits) {
            super.onPostExecute(stopVisits);
            mSwipeRefreshLayout.setRefreshing(false);
            mStopVisitListItem.clearStopVisits();
            mStopVisitListItem.getStopVisits().addAll(stopVisits);

            initDeparturesGrid();
        }

        @Override
        protected void onCancelled(List<StopVisit> stopVisits) {
            super.onCancelled(stopVisits);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class GetAllStopsForLine extends AsyncTask<String, Void, List<Stop>> {

        @Override
        protected List<Stop> doInBackground(String... lineIds) {
            return Requests.getAllStopsForLine(lineIds[0], null);
        }

        @Override
        protected void onPostExecute(List<Stop> stops) {
            super.onPostExecute(stops);

            Log.d(NextOsloApp.LOG_TAG, stops.toString());
        }
    }

    private class GetDeviationDetails extends AsyncTask<Deviation, Void, DeviationDetails> implements View.OnClickListener {

        private final AlertDialog dialog;
        private final TextView title;
        private final TextView lead;
        private final TextView body;
        private final TextView updated;
        private final TextView fromTo;
        private final ContentLoadingProgressBar progressBar;
        private final Button okButton;

        private final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GetDeviationDetails.this.cancel(true);
                mDeviationDetails.setVisibility(View.GONE);
            }
        };
        private final SimpleDateFormat dateFormatter;


        public GetDeviationDetails(String title) {
//            View deviationView = LayoutInflater.from(DetailsActivity.this).inflate(R.layout.deviation_details, mFrameParallax, false);

            dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

            this.title = (TextView) mDeviationDetails.findViewById(R.id.title);
            this.title.setText(title);

            lead = (TextView) mDeviationDetails.findViewById(R.id.lead);
            body = (TextView) mDeviationDetails.findViewById(R.id.body);
            updated = (TextView) mDeviationDetails.findViewById(R.id.updated);
            fromTo = (TextView) mDeviationDetails.findViewById(R.id.fromTo);
            progressBar = (ContentLoadingProgressBar) mDeviationDetails.findViewById(R.id.progress);
            okButton = (Button) mDeviationDetails.findViewById(R.id.okButton);
            okButton.setTextColor(mPrimaryColorId);
            okButton.setOnClickListener(this);

            dialog = new AlertDialog.Builder(DetailsActivity.this)
                    .setTitle(title)
                    .setView(mDeviationDetails)
                    .setPositiveButton(R.string.ok, onClickListener)
                    .create();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            mDeviationDetails.setVisibility(View.VISIBLE);
//            dialog.show();
        }

        @Override
        protected DeviationDetails doInBackground(Deviation... deviations) {
            DeviationDetails deviationDetails = Requests.getDeviationDetails(deviations[0].getID());

            return deviationDetails;
        }

        @Override
        protected void onPostExecute(DeviationDetails deviationDetails) {
            super.onPostExecute(deviationDetails);
            Log.d(NextOsloApp.LOG_TAG, "Got deviation details");
            progressBar.setVisibility(View.GONE);
            lead.setText(deviationDetails.getLead());

            body.setText(deviationDetails.getBody());

            if (body.getText() == null || body.getText().toString().isEmpty()) {
                body.setVisibility(View.GONE);
            } else {
                body.setVisibility(View.VISIBLE);
            }

            Date dateUpdated = deviationDetails.getLastUpdated().toDate();
            Date dateValidFrom = deviationDetails.getValidFrom().toDate();
            Date dateValidTo = deviationDetails.getValidTo().toDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateValidTo);

            updated.setText(getString(R.string.updated, dateFormatter.format(dateUpdated)));

            fromTo.setText(getString(R.string.valid, dateFormatter.format(dateValidFrom), calendar.get(Calendar.YEAR) == 9999 ? getText(R.string.until_further_notice) : dateFormatter.format(dateValidTo)));
        }

        @Override
        public void onClick(View view) {
            GetDeviationDetails.this.cancel(true);
            mDeviationDetails.setVisibility(View.GONE);
        }
    }
}
