package com.pokerledger.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityBase extends AppCompatActivity implements GameCompleteListener<Game>, LocationCompleteListener<Location>, GameFormatCompleteListener<GameFormat>, BlindsCompleteListener<Blinds> {
    Session activeSession = new Session();

    ArrayList<Location> locations = new ArrayList<>();
    Spinner locationSpinner;
    ArrayAdapter locationAdapter;

    ArrayList<Game> games = new ArrayList<>();
    Spinner gameSpinner;
    ArrayAdapter gameAdapter;

    ArrayList<GameFormat> gameFormats = new ArrayList<>();
    Spinner gameFormatSpinner;
    ArrayAdapter gameFormatAdapter;

    ArrayList<Blinds> blinds = new ArrayList<>();
    Spinner blindsSpinner;
    ArrayAdapter blindsAdapter;

    public void onEditGameComplete(Game g) {
        activeSession.setGame(g);
        new LoadGames().execute();
    }

    public void onEditLocationComplete(Location l) {
        activeSession.setLocation(l);
        new LoadLocations().execute();
    }

    public void onEditGameFormatComplete(GameFormat gf) {
        activeSession.setGameFormat(gf);
        new LoadGameFormats().execute();
    }

    public void onEditBlindsComplete(Blinds b) {
        activeSession.setBlinds(b);
        new LoadBlinds().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LIFECYCLE", "Activity Base onCreate");
        locationSpinner = findViewById(R.id.location);
        if (locationSpinner != null) { //locationSpinner view will not be on several views
            locationAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, locations);
            locationSpinner.setAdapter(locationAdapter);
        }

        gameSpinner = findViewById(R.id.game);
        if (gameSpinner != null) { //gameSpinner view will not be on several views
            gameAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, games);
            gameSpinner.setAdapter(gameAdapter);
        }

        gameFormatSpinner = findViewById(R.id.game_format);
        if (gameFormatSpinner != null) { //gameFormatSpinner view will not be on several views
            gameFormatAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, gameFormats);
            gameFormatSpinner.setAdapter(gameFormatAdapter);
        }

        blindsSpinner = findViewById(R.id.blinds);
        if (blindsSpinner != null) { //blinds view will not be on several views
            blindsAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, blinds);
            blindsSpinner.setAdapter(blindsAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("LIFECYCLE", "Activity Base onStart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Gson gson = new Gson();
        savedInstanceState.putString("ACTIVE_SESSION_JSON", gson.toJson(activeSession));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Gson gson = new Gson();
            activeSession = gson.fromJson(savedInstanceState.getString("ACTIVE_SESSION_JSON"), Session.class);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.add_session :
                FragmentManager manager = getFragmentManager();

                FragmentAddSession dialog = new FragmentAddSession();
                dialog.show(manager, "AddSession");
                break;
            case R.id.history :
                Intent history = new Intent(this, ActivityHistory.class);
                this.startActivity(history);
                break;
            case R.id.settings :
                Intent settings = new Intent(this, ActivitySettings.class);
                this.startActivity(settings);
                break;
            case R.id.graphs :
                Intent statistics = new Intent(this, ActivityGraphs.class);
                this.startActivity(statistics);
                break;
            case R.id.filters :
                Intent filters = new Intent(this, ActivityFilters.class);
                this.startActivity(filters);
                break;
            case R.id.notes :
                Intent notes = new Intent(this, ActivityNotes.class);
                this.startActivity(notes);
                break;
            /*
            case R.id.statistics :
                Intent statistics = new Intent(this, StatisticsActivity.class);
                this.startActivity(statistics);
                break;
                */
        }

        return super.onOptionsItemSelected(item);
    }

    public void showLocationDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentLocation fl = new FragmentLocation();
        fl.show(manager, "CreateLocation");
    }

    public void showGameDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentGame fg = new FragmentGame();
        fg.show(manager, "Edit Game");
    }

    public void showGameFormatDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentGameFormat fgf = new FragmentGameFormat();
        fgf.show(manager, "Edit Game Format");
    }

    public void showBlindsDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentBlinds fb = new FragmentBlinds();
        fb.show(manager, "CreateBlinds");
    }

    public class LoadLocations extends AsyncTask<Void, Void, ArrayList<Location>> {
        @Override
        protected ArrayList<Location> doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.getAllLocations("frequency");
        }

        @Override
        protected void onPostExecute(ArrayList<Location> result) {
            locations.clear();
            locations.addAll(result);
            locationAdapter.notifyDataSetChanged();

            int target;
            if (ActivityBase.this.activeSession.getLocation() != null && ActivityBase.this.activeSession.getLocation().getId() > 0) {
                target = ActivityBase.this.activeSession.getLocation().getId();
            }
            else {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                target = prefs.getInt("default_location", 0);
            }
            setLocation(target);
        }
    }

    public void setLocation(int target) {
        Spinner locationSpinner = findViewById(R.id.location);

        if (target > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < locationSpinner.getCount()) {
                Location currentLocation = (Location) locationSpinner.getItemAtPosition(count);
                if (target == currentLocation.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            locationSpinner.setSelection(spinnerPos);
        }
    }

    public class LoadGames extends AsyncTask<Void, Void, ArrayList<Game>> {
        @Override
        protected ArrayList<Game> doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.getAllGames(null);
        }

        @Override
        protected void onPostExecute(ArrayList<Game> result) {
            games.clear();
            games.addAll(result);
            gameAdapter.notifyDataSetChanged();

            int target;
            if (ActivityBase.this.activeSession.getGame() != null && ActivityBase.this.activeSession.getGame().getId() > 0) {
                target = ActivityBase.this.activeSession.getGame().getId();
            }
            else {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                target = prefs.getInt("default_game", 0);
            }
            setGame(target);
        }
    }

    public void setGame(int target) {
        Spinner gameSpinner = findViewById(R.id.game);

        if (target > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < gameSpinner.getCount()) {
                Game currentGame = (Game) gameSpinner.getItemAtPosition(count);
                if (target == currentGame.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            gameSpinner.setSelection(spinnerPos);
        }
    }

    public class LoadGameFormats extends AsyncTask<Void, Void, ArrayList<GameFormat>> {
        @Override
        protected ArrayList<GameFormat> doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.getAllGameFormats(null);
        }

        @Override
        protected void onPostExecute(ArrayList<GameFormat> result) {
            gameFormats.clear();
            gameFormats.addAll(result);
            gameFormatAdapter.notifyDataSetChanged();

            int target;
            if (ActivityBase.this.activeSession.getGameFormat() != null && ActivityBase.this.activeSession.getGameFormat().getId() > 0) {
                target = ActivityBase.this.activeSession.getGameFormat().getId();
            }
            else {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                target = prefs.getInt("default_game_format", 0);
            }
            setGameFormat(target);
        }
    }

    public void setGameFormat(int target) {
        Spinner gameFormatSpinner = findViewById(R.id.game_format);

        if (target > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < gameFormatSpinner.getCount()) {
                GameFormat currentGameFormat = (GameFormat) gameFormatSpinner.getItemAtPosition(count);
                if (target == currentGameFormat.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            gameFormatSpinner.setSelection(spinnerPos);
        }
    }

    public class LoadBlinds extends AsyncTask<Void, Void, ArrayList<Blinds>> {
        @Override
        protected ArrayList<Blinds> doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.getAllBlinds(null);
        }

        @Override
        protected void onPostExecute(ArrayList<Blinds> result) {
            blinds.clear();
            blinds.addAll(result);
            blindsAdapter.notifyDataSetChanged();

            int target;
            if (ActivityBase.this.activeSession.getBlinds() != null && ActivityBase.this.activeSession.getBlinds().getId() > 0) {
                target = ActivityBase.this.activeSession.getBlinds().getId();
            }
            else {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                target = prefs.getInt("default_blinds", 0);
            }
            setBlinds(target);
        }
    }

    public void setBlinds(int target) {
        Spinner blindsSpinner = findViewById(R.id.blinds);

        if (target > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < blindsSpinner.getCount()) {
                Blinds currentBlinds = (Blinds) blindsSpinner.getItemAtPosition(count);
                if (target == currentBlinds.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            blindsSpinner.setSelection(spinnerPos);
        }
    }

    public void autoBackup() {
        String backupName = "pokerledger_restore_point.pldb";
        File src = new File(this.getDatabasePath("sessionManager").getAbsolutePath());
        File dst = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), backupName);
        FileChannel inChannel;
        FileChannel outChannel;

        try
        {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);

            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        } catch (IOException e) {

        }
    }
}