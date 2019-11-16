package com.pokerledger.app;

import androidx.appcompat.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import com.pokerledger.app.model.Break;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/27/15.
 */
public class FragmentBreakViewer extends DialogFragment implements AdapterView.OnItemClickListener {
    private ArrayList<Break> breaks = new ArrayList<>();
    private ArrayAdapter<Break> adapter;
    private ListView breakList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_breaks, null, false);
        breakList = (ListView) view.findViewById(R.id.list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getString("SESSION_JSON") != null) {
            Gson gson = new Gson();
            Session current = gson.fromJson(getArguments().getString("SESSION_JSON"),Session.class);
            breaks = current.getBreaks();
        }

        adapter = new ArrayAdapter<Break>(getActivity(), android.R.layout.simple_list_item_1, breaks);
        breakList.setAdapter(adapter);
        breakList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ActivitySession a = (ActivitySession) getActivity();
        final int p = position;

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Confirmation");
        adb.setMessage("Are you sure you want to delete this break?");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                breaks.remove(p);
                a.activeSession.setBreaks(breaks);
                adapter.notifyDataSetChanged();
                if (a.activeSession.getBreaks().size() < 1) {
                    FragmentBreakViewer.this.dismiss();
                }
            }
        });
        adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            }
        });
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.show();
    }
}