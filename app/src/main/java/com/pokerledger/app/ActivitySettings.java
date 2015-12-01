package com.pokerledger.app;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by max on 8/23/15.
 */
public class ActivitySettings extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FlurryAgent.logEvent("Activity_Settings");

        final Spinner locationSpinner = (Spinner) this.findViewById(R.id.location);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Location selected = (Location) locationSpinner.getSelectedItem();
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int defaultLocation = prefs.getInt("default_location", 0);
                if (defaultLocation == selected.getId()) {
                    ActivitySettings.this.findViewById(R.id.set_default_location).setBackgroundColor(0xfff6da00);
                } else {
                    ActivitySettings.this.findViewById(R.id.set_default_location).setBackgroundColor(0xffb4b6b3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        final Spinner gameSpinner = (Spinner) this.findViewById(R.id.game);
        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Game selected = (Game) gameSpinner.getSelectedItem();
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int defaultGame = prefs.getInt("default_game", 0);
                if (defaultGame == selected.getId()) {
                    ActivitySettings.this.findViewById(R.id.set_default_game).setBackgroundColor(0xfff6da00);
                } else {
                    ActivitySettings.this.findViewById(R.id.set_default_game).setBackgroundColor(0xffb4b6b3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        final Spinner gameFormatSpinner = (Spinner) this.findViewById(R.id.game_format);
        gameFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                GameFormat selected = (GameFormat) gameFormatSpinner.getSelectedItem();
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int defaultGameFormat = prefs.getInt("default_game_format", 0);
                if (defaultGameFormat == selected.getId()) {
                    ActivitySettings.this.findViewById(R.id.set_default_game_format).setBackgroundColor(0xfff6da00);
                } else {
                    ActivitySettings.this.findViewById(R.id.set_default_game_format).setBackgroundColor(0xffb4b6b3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        final Spinner blindsSpinner = (Spinner) this.findViewById(R.id.blinds);
        blindsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Blinds selected = (Blinds) blindsSpinner.getSelectedItem();
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int defaultBlinds = prefs.getInt("default_blinds", 0);
                if (defaultBlinds == selected.getId()) {
                    ActivitySettings.this.findViewById(R.id.set_default_blinds).setBackgroundColor(0xfff6da00);
                } else {
                    ActivitySettings.this.findViewById(R.id.set_default_blinds).setBackgroundColor(0xffb4b6b3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //load locations, games, gameformats and blinds from their database tables in to the spinners
        //these async tasks also call set functions to select the correct item in spinners for the activeSession
        new LoadLocations().execute();
        new LoadGames().execute();
        new LoadGameFormats().execute();
        new LoadBlinds().execute();
    }

    protected void notifyEditLocation(Location l) {
        //this method is necessary because i cant get fragments to call async tasks
        new EditLocation().execute(l);
    }

    protected void notifyEditGame(Game g) {
        //this method is necessary because i cant get fragments to call async tasks
        new EditGame().execute(g);
    }

    protected void notifyEditGameFormat(GameFormat gf) {
        //this method is necessary because i cant get fragments to call async tasks
        new EditGameFormat().execute(gf);
    }

    protected void notifyEditBlinds(Blinds b) {
        //this method is necessary because i cant get fragments to call async tasks
        new EditBlinds().execute(b);
    }

    public void showEditLocationDialog(View v) {
        Spinner locationSpinner = (Spinner) findViewById(R.id.location);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_LOCATION", gson.toJson(locationSpinner.getAdapter().getItem(locationSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentEditLocation fcl = new FragmentEditLocation();
        fcl.setArguments(b);
        fcl.show(manager, "EditLocation");
    }

    public void showEditGameDialog(View v) {
        Spinner gameSpinner = (Spinner) findViewById(R.id.game);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_GAME", gson.toJson(gameSpinner.getAdapter().getItem(gameSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentEditGame fcg = new FragmentEditGame();
        fcg.setArguments(b);
        fcg.show(manager, "EditGame");
    }

    public void showEditGameFormatDialog(View v) {
        Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_GAME_FORMAT", gson.toJson(gameFormatSpinner.getAdapter().getItem(gameFormatSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentEditGameFormat fcgf = new FragmentEditGameFormat();
        fcgf.setArguments(b);
        fcgf.show(manager, "EditGameFormat");
    }

    public void showEditBlindsDialog(View v) {
        Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_BLINDS", gson.toJson(blindsSpinner.getAdapter().getItem(blindsSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentEditBlinds fcb = new FragmentEditBlinds();
        fcb.setArguments(b);
        fcb.show(manager, "EditBlinds");
    }

    public void showDeleteLocationDialog(View v) {
        final Spinner locationSpinner = (Spinner) findViewById(R.id.location);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle("Confirm Delete Location");
        adb.setMessage("Deleting a location will also delete all sessions associated with that location. Are you certain that you want to continue?");

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new DeleteLocation().execute((Location) locationSpinner.getAdapter().getItem(locationSpinner.getSelectedItemPosition()));
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int dfL = prefs.getInt("default_location", 0);
                if (dfL == ((Location) locationSpinner.getSelectedItem()).getId()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("default_location", 0);
                    editor.commit();
                }
                Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_delete_location), Toast.LENGTH_LONG).show();
            }
        });

        adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.show();
    }

    public void showDeleteGameDialog(View v) {
        final Spinner gameSpinner = (Spinner) findViewById(R.id.game);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle("Confirm Delete Game");
        adb.setMessage("Deleting a game will also delete all sessions associated with that game. Are you certain that you want to continue?");

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new DeleteGame().execute((Game) gameSpinner.getAdapter().getItem(gameSpinner.getSelectedItemPosition()));
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int dfG = prefs.getInt("default_game", 0);
                if (dfG == ((Game) gameSpinner.getSelectedItem()).getId()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("default_game", 0);
                    editor.commit();
                }
                Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_delete_game), Toast.LENGTH_LONG).show();
            }
        });

        adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.show();
    }

    public void showDeleteGameFormatDialog(View v) {
        final Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle("Confirm Delete Game Format");
        adb.setMessage("Deleting a game format will also delete all sessions associated with that game format. Are you certain that you want to continue?");

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new DeleteGameFormat().execute((GameFormat) gameFormatSpinner.getAdapter().getItem(gameFormatSpinner.getSelectedItemPosition()));
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int dfGF = prefs.getInt("default_game_format", 0);
                if (dfGF == ((GameFormat) gameFormatSpinner.getSelectedItem()).getId()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("default_game_format", 0);
                    editor.commit();
                }
                Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_delete_game_format), Toast.LENGTH_LONG).show();
            }
        });

        adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.show();
    }

    public void showDeleteBlindsDialog(View v) {
        final Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle("Confirm Delete Blinds");
        adb.setMessage("Deleting a blind set will also delete all sessions associated with that blind set. Are you certain that you want to continue?");

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new DeleteBlinds().execute((Blinds) blindsSpinner.getAdapter().getItem(blindsSpinner.getSelectedItemPosition()));
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                int dfB = prefs.getInt("default_blinds", 0);
                if (dfB == ((Blinds) blindsSpinner.getSelectedItem()).getId()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("default_blinds", 0);
                    editor.commit();
                }
                Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_delete_blinds), Toast.LENGTH_LONG).show();
            }
        });

        adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.show();
    }

    public void setDefaultLocation(View v) {
        final Spinner locationSpinner = (Spinner) findViewById(R.id.location);
        Location selected = ((Location) locationSpinner.getAdapter().getItem(locationSpinner.getSelectedItemPosition()));
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        int defaultLocation = prefs.getInt("default_location", 0);
        SharedPreferences.Editor editor = prefs.edit();
        if (defaultLocation == selected.getId()) {
            defaultLocation = 0;
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_remove_default_location), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_location).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultLocation = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_set_default_location), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_location).setBackgroundColor(0xfff6da00);
        }
        editor.putInt("default_location", defaultLocation);
        editor.commit();
    }

    public void setDefaultGame(View v) {
        final Spinner gameSpinner = (Spinner) findViewById(R.id.game);
        Game selected = ((Game) gameSpinner.getAdapter().getItem(gameSpinner.getSelectedItemPosition()));
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        int defaultGame = prefs.getInt("default_game", 0);
        SharedPreferences.Editor editor = prefs.edit();
        if (defaultGame == selected.getId()) {
            defaultGame = 0;
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_remove_default_game), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_game).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultGame = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_set_default_game), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_game).setBackgroundColor(0xfff6da00);
        }
        editor.putInt("default_game", defaultGame);
        editor.commit();
    }

    public void setDefaultGameFormat(View v) {
        final Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);
        GameFormat selected = ((GameFormat) gameFormatSpinner.getAdapter().getItem(gameFormatSpinner.getSelectedItemPosition()));
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        int defaultGameFormat = prefs.getInt("default_game_format", 0);
        SharedPreferences.Editor editor = prefs.edit();
        if (defaultGameFormat == selected.getId()) {
            defaultGameFormat = 0;
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_remove_default_game_format), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_game_format).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultGameFormat = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_set_default_game_format), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_game_format).setBackgroundColor(0xfff6da00);
        }
        editor.putInt("default_game_format", defaultGameFormat);
        editor.commit();
    }

    public void setDefaultBlinds(View v) {
        final Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);
        Blinds selected = ((Blinds) blindsSpinner.getAdapter().getItem(blindsSpinner.getSelectedItemPosition()));
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        int defaultBlinds = prefs.getInt("default_blinds", 0);
        SharedPreferences.Editor editor = prefs.edit();
        if (defaultBlinds == selected.getId()) {
            defaultBlinds = 0;
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_remove_default_blinds), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_blinds).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultBlinds = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + getResources().getString(R.string.info_set_default_blinds), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_blinds).setBackgroundColor(0xfff6da00);
        }
        editor.putInt("default_blinds", defaultBlinds);
        editor.commit();
    }

    public void backupDatabase(View v) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String backupName = "pokerledger_" + df.format(c.getTime()) + ".pldb";
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
            //blah blah, thank you java
        }
        Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_db_backup) + backupName, Toast.LENGTH_LONG).show();
    }

    public void restoreDatabase(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivity(intent);
        /*
        File dst = new File(this.getDatabasePath("sessionManager").getAbsolutePath());
        File src = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "pokerledger_backup");
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
            //blah blah, thank you java
        }
        */
    }

    public class EditLocation extends AsyncTask<Location, Void, Void> {
        @Override
        protected Void doInBackground(Location... loc) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.editLocation(loc[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadLocations().execute();
        }
    }

    public class DeleteLocation extends AsyncTask<Location, Void, Void> {
        @Override
        protected Void doInBackground(Location... loc) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.deleteLocation(loc[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadLocations().execute();
        }
    }

    public class EditGame extends AsyncTask<Game, Void, Void> {
        @Override
        protected Void doInBackground(Game... game) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.editGame(game[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadGames().execute();
        }
    }

    public class DeleteGame extends AsyncTask<Game, Void, Void> {
        @Override
        protected Void doInBackground(Game... game) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.deleteGame(game[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadGames().execute();
        }
    }

    public class EditGameFormat extends AsyncTask<GameFormat, Void, Void> {
        @Override
        protected Void doInBackground(GameFormat... g) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.editGameFormat(g[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadGameFormats().execute();
        }
    }

    public class DeleteGameFormat extends AsyncTask<GameFormat, Void, Void> {
        @Override
        protected Void doInBackground(GameFormat... g) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.deleteGameFormat(g[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadGameFormats().execute();
        }
    }

    public class EditBlinds extends AsyncTask<Blinds, Void, Void> {
        @Override
        protected Void doInBackground(Blinds... set) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.editBlinds(set[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadBlinds().execute();
        }
    }

    public class DeleteBlinds extends AsyncTask<Blinds, Void, Void> {
        @Override
        protected Void doInBackground(Blinds... set) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.deleteBlinds(set[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadBlinds().execute();
        }
    }
}
