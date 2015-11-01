package com.pokerledger.app;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class FragmentAddSession extends DialogFragment implements AdapterView.OnItemClickListener {
    private static final int ACTIVE_RESULT = 1;
    private static final int FINISHED_RESULT = 2;

    private ArrayList<String> options = new ArrayList<String>();
    private ListView myList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_session, null, false);
        myList = (ListView) view.findViewById(R.id.add_session_list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        options.add("Active Session");
        options.add("Finished Session");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();

        if (position == 0) {
            Intent intent = new Intent(getActivity(), ActivityActiveSession.class);
            getActivity().startActivityForResult(intent, ACTIVE_RESULT);
        }
        else if (position == 1) {
            Intent intent = new Intent(getActivity(), ActivityFinishedSession.class);
            getActivity().startActivityForResult(intent, FINISHED_RESULT);
        }
    }
}