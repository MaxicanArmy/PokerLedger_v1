package com.pokerledger.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.flurry.android.FlurryAgent;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityBase extends AppCompatActivity {
    Session activeSession = new Session();

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

    protected void notifyCreateLocation(String value) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateLocation().execute(new Location(0, value, 0));
    }

    protected void notifyCreateGame(String value) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateGame().execute(new Game(0, value, 0));
    }

    protected void notifyCreateGameFormat(String gameFormat, int bfId, String baseFormat) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateGameFormat().execute(new GameFormat(0, gameFormat, bfId, baseFormat));
    }

    protected void notifyCreateBlinds(double sb, double bb, double straddle, double bringIn, double ante, double perPoint) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateBlinds().execute(new Blinds(sb, bb, straddle, bringIn, ante, perPoint, 0));
    }

    public void showCreateLocationDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentCreateLocation fcl = new FragmentCreateLocation();
        fcl.show(manager, "CreateLocation");
    }

    public void showCreateGameDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentCreateGame fcg = new FragmentCreateGame();
        fcg.show(manager, "CreateGame");
    }

    public void showCreateGameFormatDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentCreateGameFormat fcgf = new FragmentCreateGameFormat();
        fcgf.show(manager, "CreateGameFormat");
    }

    public void showCreateBlindsDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentCreateBlinds fcb = new FragmentCreateBlinds();
        fcb.show(manager, "CreateBlinds");
    }

    public class CreateLocation extends AsyncTask<Location, Void, Location> {
        @Override
        protected Location doInBackground(Location... loc) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.createLocation(loc[0]);
        }

        @Override
        protected void onPostExecute(Location result) {
            activeSession.setLocation(result);
            new LoadLocations().execute();
        }
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
            Spinner locationSpinner = (Spinner) findViewById(R.id.location);
            ArrayAdapter locationAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, result);
            locationAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            locationSpinner.setAdapter(locationAdapter);

            int target;
            if (ActivityBase.this.activeSession.getLocation().getId() > 0) {
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
        Spinner locationSpinner = (Spinner) findViewById(R.id.location);

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

    public class CreateGame extends AsyncTask<Game, Void, Game> {
        @Override
        protected Game doInBackground(Game... loc) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.createGame(loc[0]);
        }

        @Override
        protected void onPostExecute(Game result) {
            activeSession.setGame(result);
            new LoadGames().execute();
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
            Spinner gameSpinner = (Spinner) findViewById(R.id.game);
            ArrayAdapter gameAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, result);
            gameAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            gameSpinner.setAdapter(gameAdapter);

            int target;
            if (ActivityBase.this.activeSession.getGame().getId() > 0) {
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
        Spinner gameSpinner = (Spinner) findViewById(R.id.game);

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

    public class CreateGameFormat extends AsyncTask<GameFormat, Void, GameFormat> {
        @Override
        protected GameFormat doInBackground(GameFormat... g) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.createGameFormat(g[0]);
        }

        @Override
        protected void onPostExecute(GameFormat result) {
            activeSession.setGameFormat(result);
            new LoadGameFormats().execute();
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
            Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);
            ArrayAdapter gameFormatAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, result);
            gameFormatAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            gameFormatSpinner.setAdapter(gameFormatAdapter);

            int target;
            if (ActivityBase.this.activeSession.getGameFormat().getId() > 0) {
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
        Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);

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

    public class CreateBlinds extends AsyncTask<Blinds, Void, Blinds> {
        @Override
        protected Blinds doInBackground(Blinds... set) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
            return db.createBlinds(set[0]);
        }

        @Override
        protected void onPostExecute(Blinds result) {
            activeSession.setBlinds(result);
            new LoadBlinds().execute();
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
            Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);
            ArrayAdapter blindsAdapter = new ArrayAdapter(ActivityBase.this, R.layout.spinner_item_view, result);
            blindsAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            blindsSpinner.setAdapter(blindsAdapter);

            int target;
            if (ActivityBase.this.activeSession.getBlinds().getId() > 0) {
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
        Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);

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
            FlurryAgent.logEvent("Error_BackupDatabase");
        }
    }
}