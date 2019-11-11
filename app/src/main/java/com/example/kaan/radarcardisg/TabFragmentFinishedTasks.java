package com.example.kaan.radarcardisg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Kaan on 9.08.2017.
 */

public class TabFragmentFinishedTasks extends Fragment {
    ListView listView ;
    public static TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getTasks();
        View view = setView(inflater,container);
        return view;
    }
    private void getTasks(){
        TaskProviderActivity.finishedTasks.clear();
    }
    private View setView(LayoutInflater inflater, @Nullable ViewGroup container){
        View view = inflater.inflate(R.layout.tab_fragment_2_layout,container,false);

        listView = (ListView) view.findViewById(R.id.listView);
        taskAdapter = new TaskAdapter(getActivity(), TaskProviderActivity.finishedTasks);
        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        return view;
    }
}
