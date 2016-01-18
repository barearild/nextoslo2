package com.barearild.next.v2.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import v2.next.barearild.com.R;

/**
 * Created by Arild on 17.01.2016.
 */
public class SearchSuggestionsAdapter extends RecyclerView.Adapter<SearchSuggestionsAdapter.SearchSuggestionViewHolder> {

    private static final int TYPE_SEARCH_SUGGESTION = 6;

    private final List<Object> data;
    private final Context context;
    private final OnSuggestionClickListener onSuggestionClickListener;

    private LayoutInflater inflater;

    public SearchSuggestionsAdapter(List<Object> data, Context context, OnSuggestionClickListener onSuggestionClickListener) {
        super();
        this.data = data;
        this.context = context;
        this.onSuggestionClickListener = onSuggestionClickListener;

        setHasStableIds(true);

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_SEARCH_SUGGESTION;
    }

    @Override
    public long getItemId(int position) {
        return ((SearchSuggestion)data.get(position)).id;
    }

    @Override
    public SearchSuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchSuggestionViewHolder(inflater.inflate(R.layout.search_suggestion, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchSuggestionViewHolder holder, int position) {
        final SearchSuggestion suggestion = (SearchSuggestion) data.get(position);

        holder.icon.setImageResource(suggestion.iconRes);
        holder.text.setText(suggestion.text + (suggestion.text2 != null ? ("\n" +suggestion.text2) : ""));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSuggestionClickListener.suggestionClicked(suggestion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SearchSuggestionViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView text;

        public SearchSuggestionViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.icon);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    public interface OnSuggestionClickListener {
        void suggestionClicked(SearchSuggestion searchSuggestion);
    }

}
