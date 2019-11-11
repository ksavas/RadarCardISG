package com.example.kaan.radarcardisg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Kaan on 9.08.2017.
 */

public class TabFragmentPendingTasks extends Fragment  {

    ListView listView ;
    TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // getTasks();
        View view = setView(inflater,container);
        return view;
    }
    private void getTasks(){/*
        TaskProviderActivity.pendingTasks.clear();
        TaskProviderActivity.pendingTasks.add(new TaskProviderElement("test1","LCW","Start","Finish",""));
        TaskProviderActivity.pendingTasks.add(new TaskProviderElement("test2","HSG","Start","Finish",""));
        TaskProviderActivity.pendingTasks.add(new TaskProviderElement("test3","LCW","Start","Finish",""));
        TaskProviderActivity.pendingTasks.add(new TaskProviderElement("test5","LCW","Start","Finish",""));*/
    }
    private View setView(LayoutInflater inflater, @Nullable ViewGroup container){
        View view = inflater.inflate(R.layout.tab_fragment1_layout,container,false);

        listView = (ListView) view.findViewById(R.id.listView);
        taskAdapter = new TaskAdapter(getActivity(), TaskProviderActivity.pendingTasks);
        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),TaskActivity.class);
                intent.putExtra("taskNumber",position);
                intent.putExtra("listId",TaskProviderActivity.pendingTasks.get(position).listId);
                intent.putExtra("tkId",TaskProviderActivity.pendingTasks.get(position).tkId);
                startActivityForResult(intent,1);
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode ==1){
                int position = data.getIntExtra("task",-1);
                TaskProviderActivity.finishedTasks.add(TaskProviderActivity.pendingTasks.get(position));
                TaskProviderActivity.pendingTasks.remove(position);
                taskAdapter.notifyDataSetChanged();
                TabFragmentFinishedTasks.taskAdapter.notifyDataSetChanged();
            }
        }

    }
}
