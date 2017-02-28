package com.skripsi.ekoyulianto.biologipedia.Adapter;

/**
 * Created by ekoyulianto on 12/30/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.skripsi.ekoyulianto.biologipedia.CustomFont.CustomTextView;
import com.skripsi.ekoyulianto.biologipedia.R;
import com.skripsi.ekoyulianto.biologipedia.Result.Result;

import java.util.List;

/**
 * Created by ekoyulianto on 12/11/2016.
 */

public class ResultAdapter extends ArrayAdapter<Result> {

    List<Result> resultList;
    Context context;
    int layout;

    public ResultAdapter(Context context, int layout, List<Result> resultList) {
        super(context, layout, resultList);
        this.resultList = resultList;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ResultHolder holder;

        if (v == null) {
            LayoutInflater vi = ((Activity) context).getLayoutInflater();
            v = vi.inflate(layout, parent, false);

            holder = new ResultHolder();
            holder.istilahView = (CustomTextView) v.findViewById(R.id.textView);
            holder.deskripsiView = (CustomTextView) v.findViewById(R.id.textView2);

            v.setTag(holder);
        } else {
            holder = (ResultHolder) v.getTag();
        }

        Result result = resultList.get(position);
        holder.istilahView.setText(result.getIstilah());
        holder.deskripsiView.setText(result.getDeskripsi());
        return v;
    }

    static class ResultHolder {
        CustomTextView istilahView;
        CustomTextView deskripsiView;
    }
}
