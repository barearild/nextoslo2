package com.barearild.next.v2.views.departures;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.barearild.next.v2.reisrest.Transporttype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import v2.next.barearild.com.R;

public class FilterView extends GridView {

    public static final String SWITCH_TEXT = "text";

    public FilterView(Context context) {
        super(context);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

        List<HashMap<String, String>> data = new ArrayList<>();

        for (Transporttype transporttype : Transporttype.onlyRealTimeTransporttypes) {
            HashMap<String, String> transportMap = new HashMap<>();
            transportMap.put(SWITCH_TEXT, transporttype.name());
            data.add(transportMap);
        }

        String[] from = {SWITCH_TEXT};
        int[] to = {R.id.filter_switch};

        setAdapter(new SimpleAdapter(context, data, R.layout.departure_filter_switch, from, to));
    }

//    private class FilterAdapter extends BaseAdapter {
//
//        private final Transporttype[] data = Transporttype.onlyRealTimeTransporttypes.toArray(new Transporttype[Transporttype.onlyRealTimeTransporttypes.size()]);
//        private final LayoutInflater mInflater;
//
//        public FilterAdapter(Context context) {
//            mInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return data.length;
//        }
//
//        @Override
//        public Transporttype getItem(int position) {
//            return data[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return data[position].ordinal();
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            Transporttype transporttype = getItem(position);
//
//            inflater.inflate()
//            return null;
//        }
//
//        private class FilterViewHolder {
//            Switch filterSwitch;
//        }
//    }
}
