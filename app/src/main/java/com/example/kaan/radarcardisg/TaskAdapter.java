package com.example.kaan.radarcardisg;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class TaskAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<TaskProviderElement> mKisiListesi;

    public TaskAdapter(Activity activity, List<TaskProviderElement> kisiler) {
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mKisiListesi = kisiler;
    }

    @Override
    public int getCount() {
        return mKisiListesi.size();
    }

    @Override
    public TaskProviderElement getItem(int position) {
        return mKisiListesi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View satirView;

        satirView = mInflater.inflate(R.layout.list_view_layout, null);
        TextView departmentName = (TextView) satirView.findViewById(R.id.txtDept);
        TextView companyName = (TextView) satirView.findViewById(R.id.txtCompany);
        TextView startDate = (TextView) satirView.findViewById(R.id.txtStartDate);
        TextView finishDate = (TextView) satirView.findViewById(R.id.txtFinishDate);

        TaskProviderElement taskProviderElement = mKisiListesi.get(position);

        departmentName.setText(taskProviderElement.getDepartmentName());
        companyName.setText(taskProviderElement.getCompanyName());
        startDate.setText(taskProviderElement.getStartDate());
        finishDate.setText(taskProviderElement.getFinishDate());

        return satirView;
    }
}