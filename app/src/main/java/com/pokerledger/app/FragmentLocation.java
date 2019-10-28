package com.pokerledger.app;

import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Location;

/**
 * Created by max on 9/3/15.
 */
public class FragmentLocation extends DialogFragment {
    LocationCompleteListener listener;
    Location location = new Location();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LocationCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LocationCompleteListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, null, false);
        getDialog().setTitle("Edit Location");

        Gson gson = new Gson();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("TARGET_LOCATION")) {
            String json = getArguments().getString("TARGET_LOCATION");
            location = gson.fromJson(json, Location.class);
        }

        ((EditText) view.findViewById(R.id.location_name)).setText(location.getLocation());

        Button createLocation = view.findViewById(R.id.create_location);
        createLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String locationName = ((EditText) getView().findViewById(R.id.location_name)).getText().toString();

                if (locationName.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_no_location_name), Toast.LENGTH_SHORT).show();
                } else {
                    location.setLocation(locationName);
                    new SaveLocation().execute();
                }
            }
        });

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public class SaveLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getActivity());
            if (location.getId() == 0) {
                db.createLocation(location);
            } else {
                db.editLocation(location);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listener.onEditLocationComplete(location);
            dismiss();
        }
    }
}