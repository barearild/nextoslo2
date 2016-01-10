package com.barearild.next.v2.views.departures;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.Transporttype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import v2.next.barearild.com.R;

public class FilterView extends GridView {

    public static final String SWITCH_TEXT = "text";

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private DeparturesAdapter.OnDepartureItemClickListener onDepartureItemClickListener;

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

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnDepartureItemClickListener(DeparturesAdapter.OnDepartureItemClickListener onDepartureItemClickListener) {
        this.onDepartureItemClickListener = onDepartureItemClickListener;
    }

    private void init(Context context) {

        List<HashMap<String, String>> data = new ArrayList<>();

        for (Transporttype transporttype : Transporttype.onlyRealTimeTransporttypes) {
            HashMap<String, String> transportMap = new HashMap<>();
            transportMap.put(SWITCH_TEXT, getResources().getString(transporttype.getTextId()));
            data.add(transportMap);
        }

        String[] from = {SWITCH_TEXT};
        int[] to = {R.id.filter_switch};

        setAdapter(new FilterAdapter(context));
    }

    static class FilterType {

    }

//    private class FilterAdapter extends SimpleAdapter {
//
//        public FilterAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
//            super(context, data, resource, from, to);
//        }
//
//
//    }

        private class FilterAdapter extends BaseAdapter {

        private final Transporttype[] data = Transporttype.onlyRealTimeTransporttypes.toArray(new Transporttype[Transporttype.onlyRealTimeTransporttypes.size()]);
        private final LayoutInflater mInflater;

        public FilterAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Transporttype getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return data[position].ordinal();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Transporttype transporttype = getItem(position);

            FilterViewHolder viewHolder = new FilterViewHolder();

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.departure_filter_switch, parent, false);
                viewHolder.filterSwitch = (Switch) convertView.findViewById(R.id.filter_switch);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (FilterViewHolder) convertView.getTag();
            }

            viewHolder.filterSwitch.setText(transporttype.getTextId());
            viewHolder.filterSwitch.setChecked(NextOsloApp.SHOW_TRANSPORT_TYPE.get(transporttype));

            viewHolder.filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    onDepartureItemClickListener.onFilterUpdate(transporttype, b);
                }
            });

            return convertView;
        }

    }


    static class FilterViewHolder {
        Switch filterSwitch;
    }
}
