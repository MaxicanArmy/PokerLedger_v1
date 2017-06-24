package com.pokerledger.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.SessionSet;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by max on 8/23/15.
 */
public class ActivitySettings extends ActivityBase {
    private static final int OVERWRITE_BACKUP = 1;
    private static final int MERGE_BACKUP = 2;

    private Intent restoreIntent = new Intent();

    private ArrayList<Session> mergeSessions = new ArrayList<>();
    private int method = 0;
    private String restorePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FlurryAgent.logEvent("Activity_Settings");

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            TextView versionText = (TextView) this.findViewById(R.id.version_tiny_header);
            versionText.setText("Pokerledger v" + version + " (build " + Integer.toString(pInfo.versionCode) + ")");
        } catch (PackageManager.NameNotFoundException e) {
            //this should probably be a flurry call
        }

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
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_remove_default_location), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_location).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultLocation = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_set_default_location), Toast.LENGTH_LONG).show();
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
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_remove_default_game), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_game).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultGame = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_set_default_game), Toast.LENGTH_LONG).show();
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
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_remove_default_game_format), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_game_format).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultGameFormat = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_set_default_game_format), Toast.LENGTH_LONG).show();
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
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_remove_default_blinds), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_blinds).setBackgroundColor(0xffb4b6b3);
        } else {
            defaultBlinds = selected.getId();
            Toast.makeText(ActivitySettings.this, selected.toString() + " " + getResources().getString(R.string.info_set_default_blinds), Toast.LENGTH_LONG).show();
            ActivitySettings.this.findViewById(R.id.set_default_blinds).setBackgroundColor(0xfff6da00);
        }
        editor.putInt("default_blinds", defaultBlinds);
        editor.commit();
    }

    public class EditLocation extends AsyncTask<Location, Void, Void> {
        @Override
        protected Void doInBackground(Location... loc) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());
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
            db = DatabaseHelper.getInstance(getApplicationContext());
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
            db = DatabaseHelper.getInstance(getApplicationContext());
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
            db = DatabaseHelper.getInstance(getApplicationContext());
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
            db = DatabaseHelper.getInstance(getApplicationContext());
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
            db = DatabaseHelper.getInstance(getApplicationContext());
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
            db = DatabaseHelper.getInstance(getApplicationContext());
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
            db = DatabaseHelper.getInstance(getApplicationContext());
            db.deleteBlinds(set[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadBlinds().execute();
        }
    }

    public void callExportCSV(View v) {
        new ExportCSV().execute();
    }

    public class ExportCSV extends AsyncTask<Void, Void, SessionSet> {

        @Override
        protected SessionSet doInBackground(Void... params) {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
            return new SessionSet(dbHelper.getSessions("0,1", "DESC", null));
        }

        @Override
        protected void onPostExecute(SessionSet allSessions) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String csvName = "pokerledger_" + df.format(c.getTime()) + ".csv";
            File dst = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), csvName);

            String data = allSessions.exportCSV();
            try
            {
                dst.createNewFile();
                FileOutputStream fOut = new FileOutputStream(dst);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(data);

                myOutWriter.close();

                fOut.flush();
                fOut.close();
            }
            catch (IOException e)
            {
                Log.e("Exception", "Writing cvs file failed: " + e.toString());
            }

            Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_export_cvs) + " " + csvName, Toast.LENGTH_LONG).show();
        }
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
            FlurryAgent.logEvent("Error_BackupDatabase");
        }
        Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_db_backup) + " " + backupName, Toast.LENGTH_LONG).show();
    }

    public void restoreDatabase(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to overwrite the data currently in the app, or do you want to merge with the backup?")
                .setCancelable(false)
                .setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivitySettings.this.method = OVERWRITE_BACKUP;
                        restoreDatabaseMethod();
                    }
                })
                .setNegativeButton("Merge", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivitySettings.this.method = MERGE_BACKUP;
                        restoreDatabaseMethod();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void restoreDatabaseMethod() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(ActivitySettings.this, properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                ActivitySettings.this.restorePath = files[0];
                if (ActivitySettings.this.method == OVERWRITE_BACKUP) {
                    new OverWriteDB().execute();
                } else if (ActivitySettings.this.method == MERGE_BACKUP) {
                    new BeginMerge().execute();
                }
            }
        });
        dialog.show();
    }

    public class OverWriteDB extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            String backupPath = ActivitySettings.this.restorePath;

            File dst = new File(ActivitySettings.this.getDatabasePath("sessionManager").getAbsolutePath());
            File src = new File(backupPath);
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
                FlurryAgent.logEvent("Error_RestoreDatabase");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            DatabaseHelper.resetDatabaseHelper();
            new LoadLocations().execute();
            new LoadGames().execute();
            new LoadGameFormats().execute();
            new LoadBlinds().execute();

            Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_db_overwritten), Toast.LENGTH_LONG).show();
        }
    }

    public class BeginMerge extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            ActivitySettings.this.autoBackup();
            ActivitySettings.this.mergeSessions = new ArrayList<Session>();
        }

        @Override
        protected Void doInBackground(Void... v) {
            String backupName = "pokerledger_temp.pldb";
            File src = new File(ActivitySettings.this.getDatabasePath("sessionManager").getAbsolutePath());
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
                FlurryAgent.logEvent("Error_BeginMerge");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            new MergeOverwrite().execute();
        }
    }

    public class MergeOverwrite extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            String backupPath = ActivitySettings.this.restorePath;

            File dst = new File(ActivitySettings.this.getDatabasePath("sessionManager").getAbsolutePath());
            File src = new File(backupPath);
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
                FlurryAgent.logEvent("Error_RestoreDatabase");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            DatabaseHelper.resetDatabaseHelper();

            new GetMergeSessions().execute();
        }
    }

    public class GetMergeSessions extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            ActivitySettings.this.autoBackup();
            ActivitySettings.this.mergeSessions = new ArrayList<Session>();
        }

        @Override
        protected Void doInBackground(Void... v) {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
            ActivitySettings.this.mergeSessions = dbHelper.getSessions(null, "DESC", null);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            new RestoreMergeInitial().execute();
        }
    }

    public class RestoreMergeInitial extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            String backupName = "pokerledger_temp.pldb";
            File src = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), backupName);
            File dst = new File(ActivitySettings.this.getDatabasePath("sessionManager").getAbsolutePath());
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
                FlurryAgent.logEvent("Error_BeginMerge");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            new MergeSessions().execute(ActivitySettings.this.mergeSessions);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "pokerledger_temp.pldb");
            boolean deleted = file.delete();
        }
    }

    public class MergeSessions extends AsyncTask<ArrayList<Session>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<Session>... s) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.addSessions(s[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new LoadLocations().execute();
            new LoadGames().execute();
            new LoadGameFormats().execute();
            new LoadBlinds().execute();
            Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_db_merge), Toast.LENGTH_LONG).show();
        }
    }
}
