package th.ac.dusit.dbizcom.prachuaptravel.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import th.ac.dusit.dbizcom.prachuaptravel.R;

public class SpinnerWithHintArrayAdapter<T> extends ArrayAdapter<T> {

    private Context mContext;

    public SpinnerWithHintArrayAdapter(Context context, int resource, T[] items) {
        super(context, resource, items);
        mContext = context;
    }

    public SpinnerWithHintArrayAdapter(Context context, int resource, List<T> items) {
        super(context, resource, items);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        TextView textView = v.findViewById(R.id.place_name_text_view);
        //textView.setTextSize(26);

        if (position == getCount()) {
            textView.setText("");

            T item = getItem(getCount());
            if (item != null) {
                textView.setHint(item.toString());
            }
        }
        return v;
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }

    @Nullable
    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }
}