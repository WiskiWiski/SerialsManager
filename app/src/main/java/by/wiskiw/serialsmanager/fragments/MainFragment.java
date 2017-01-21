package by.wiskiw.serialsmanager.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.Utils;
import by.wiskiw.serialsmanager.adapters.SerialListAdapter;
import by.wiskiw.serialsmanager.edit.dialog.AddDialogListener;
import by.wiskiw.serialsmanager.edit.dialog.EditDialog;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

public class MainFragment extends Fragment {

    private SerialListAdapter serialListAdapter;
    private RecyclerView recyclerView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Context context = rootView.getContext();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fillRecyclerView(context);

        return rootView;
    }



    private void fillRecyclerView(Context context){
        List<Serial> serialList = JsonDatabase.getSerials(context);
        if (serialList.size() == 0 && Utils.firstStart){
            EditDialog.addingFirst(context, new AddDialogListener() {
                @Override
                public void onSave(Context context, Serial serial) {
                    fillRecyclerView(context);
                }
            });
        } else {
            serialListAdapter = new SerialListAdapter(serialList);
            recyclerView.setAdapter(serialListAdapter);
        }
    }

    public void addSerial() {
        Context context = getContext();
        EditDialog.adding(context, new AddDialogListener() {
            @Override
            public void onSave(Context context, Serial serial) {
                if (serialListAdapter != null){
                    serialListAdapter.onAdd(context, serial);
                }
            }
        });

    }
}
