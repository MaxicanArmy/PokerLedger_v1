package com.pokerledger.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import android.Manifest;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.PLCommon;
import com.pokerledger.app.helper.SessionSet;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Break;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Note;
import com.pokerledger.app.model.Session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by max on 8/23/15.
 */
public class ActivitySettings extends ActivityBase {
    private static final int NO_BACKUP = 0;
    private static final int OVERWRITE_BACKUP = 1;
    private static final int MERGE_BACKUP = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 0;

    private int method = NO_BACKUP;
    private String restorePath = "";
    private String backupLocation = "";


    private ArrayList<Session> importedSessions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            TextView versionText = (TextView) this.findViewById(R.id.version_tiny_header);
            versionText.setText("Pokerledger v" + version + " (build " + Integer.toString(pInfo.versionCode) + ")");
        } catch (PackageManager.NameNotFoundException e) {

        }

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        String backupLoc = prefs.getString("backup_location", "");
        if (!backupLoc.equals("")) {
            backupLocation = backupLoc;
            ((Button) findViewById(R.id.backup_loc_select)).setText(backupLoc);
        }

        final Spinner locationSpinner = this.findViewById(R.id.location);
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

        final Spinner gameSpinner = this.findViewById(R.id.game);
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

        final Spinner gameFormatSpinner = this.findViewById(R.id.game_format);
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

        final Spinner blindsSpinner = this.findViewById(R.id.blinds);
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

        if (!checkExternalStoragePermission())
            ActivityCompat.requestPermissions(ActivitySettings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL:
                if (grantResults.length > 0) {
                    boolean externalStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (externalStorageAccepted)
                        Log.d("PERMISSION", "GRANTED");
                    else
                        Log.d("PERMISSION", "DENIED");
                }
                break;
        }
    }

    private boolean checkExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        /*
        if (ContextCompat.checkSelfPermission(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.e("Exception", "shouldShowRequestPermissionRationale");
            } else {
                Log.e("Exception", "ActivityCompat.requestPermissions");
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ActivitySettings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
         */
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

    public void showEditLocationDialog(View v) {
        Spinner locationSpinner = (Spinner) findViewById(R.id.location);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_LOCATION", gson.toJson(locationSpinner.getAdapter().getItem(locationSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentLocation fcl = new FragmentLocation();
        fcl.setArguments(b);
        fcl.show(manager, "EditLocation");
    }

    public void showEditGameDialog(View v) {
        Spinner gameSpinner = (Spinner) findViewById(R.id.game);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_GAME", gson.toJson(gameSpinner.getAdapter().getItem(gameSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentGame fcg = new FragmentGame();
        fcg.setArguments(b);
        fcg.show(manager, "EditGame");
    }

    public void showEditGameFormatDialog(View v) {
        Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_GAME_FORMAT", gson.toJson(gameFormatSpinner.getAdapter().getItem(gameFormatSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentGameFormat fcgf = new FragmentGameFormat();
        fcgf.setArguments(b);
        fcgf.show(manager, "EditGameFormat");
    }

    public void showEditBlindsDialog(View v) {
        Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);

        Gson gson = new Gson();
        Bundle b = new Bundle();

        b.putString("TARGET_BLINDS", gson.toJson(blindsSpinner.getAdapter().getItem(blindsSpinner.getSelectedItemPosition())));

        FragmentManager manager = getFragmentManager();
        FragmentBlinds fcb = new FragmentBlinds();
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

    public void exportCSV(View v) {
        if (!checkExternalStoragePermission())
            ActivityCompat.requestPermissions(ActivitySettings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
        else
            new ExportCSV().execute();
    }

    public void callImportCSV(View v) {
        if (!checkExternalStoragePermission())
            ActivityCompat.requestPermissions(ActivitySettings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to overwrite the data currently in the app, or do you want to merge with the import?")
                    .setCancelable(false)
                    .setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivitySettings.this.method = OVERWRITE_BACKUP;
                            selectCSVFile();
                        }
                    })
                    .setNegativeButton("Merge", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivitySettings.this.method = MERGE_BACKUP;
                            selectCSVFile();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void selectBackupLoc(View v) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(ActivitySettings.this, properties);
        dialog.setTitle("Check the box next to your selection.");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("backup_location", files[0]);
                editor.commit();
                ((Button) findViewById(R.id.backup_loc_select)).setText(files[0]);
                ActivitySettings.this.backupLocation = files[0];
            }
        });
        dialog.show();
    }

    public void selectCSVFile() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        String backupLoc = prefs.getString("backup_location", "");
        if (!backupLoc.equals("")) {
            File dirCheck = new File(backupLoc);
            if (dirCheck.isDirectory()) {
                properties.root = dirCheck;
            }
        }

        FilePickerDialog dialog = new FilePickerDialog(ActivitySettings.this, properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                Boolean validFile = false;
                if (files.length > 0) {
                    File check = new File(files[0]);
                    if (check.isFile()) {
                        validFile = true;
                    }
                }

                if (validFile) {
                    ActivitySettings.this.restorePath = files[0];
                    new AutoExportCSV().execute();
                } else {
                    Toast.makeText(ActivitySettings.this, "You did not select a valid file. Please try again...", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    public class BeginOverwriteCSV extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            Context c = ActivitySettings.this.getApplicationContext();
            c.deleteDatabase(ActivitySettings.this.getDatabasePath("sessionManager").getAbsolutePath());
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new MergeCSV().execute();
        }
    }

    public class MergeCSV extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Parsing CSV...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            this.dialog.setMessage("Reading session data... "+values[0]+".");
        }

        @Override
        protected Void doInBackground(Void... v) {
            Session importS;
            String notesText;
            List<String> notesList;
            ArrayList<Note> notes = new ArrayList<>();
            int progressCounter = 0;

            //parse the CSV in to an ArrayList<Session>
            try {
                Scanner scanner = new Scanner(new File(ActivitySettings.this.restorePath));
                if ( scanner.hasNextLine() )
                    scanner.nextLine(); //this grabs the pokerledger csv version
                if ( scanner.hasNextLine() )
                    scanner.nextLine(); //this grabs the headings

                while (scanner.hasNextLine()) {
                    publishProgress(progressCounter++);
                    importS = new Session();
                    String[] rowValues = scanner.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    importS.setBuyIn(Double.parseDouble(rowValues[0].replace("\"","")));
                    importS.setCashOut(Double.parseDouble(rowValues[1].replace("\"","")));
                    importS.setStart(PLCommon.datetimeToTimestamp(rowValues[2].replace("\"","")));
                    importS.setEnd(PLCommon.datetimeToTimestamp(rowValues[3].replace("\"","")));
                    importS.setLocation(new Location(0, rowValues[4].replace("\"","")));
                    importS.setGame(new Game(0, rowValues[5].replace("\"","")));

                    int baseGameFormatId = 1;
                    String gameFormat = rowValues[6].replaceAll("\"","");
                    String baseGameFormat;

                    Pattern p = Pattern.compile(" Tournament$");   // the pattern to search for
                    Matcher m = p.matcher(rowValues[6].replace("\"",""));

                    // now try to find at least one match
                    if (m.find()) {
                        baseGameFormatId = 2;
                        baseGameFormat = "Tournament";
                        gameFormat = gameFormat.replaceAll(" Tournament$","");
                    } else {
                        baseGameFormat = "Cash Game";
                        gameFormat = gameFormat.replaceAll(" Cash Game$","");
                    }

                    importS.setGameFormat(new GameFormat(0, gameFormat, baseGameFormatId, baseGameFormat));
                    if (!rowValues[7].replace("\"", "").equals("0^0^0^0^0^0")) {
                        String[] blinds = rowValues[7].replace("\"", "").split("\\^");
                        importS.setBlinds(new Blinds(Double.parseDouble(blinds[0]), Double.parseDouble(blinds[1]), Double.parseDouble(blinds[2]),
                                Double.parseDouble(blinds[3]), Double.parseDouble(blinds[4]), Double.parseDouble(blinds[5])));
                    } else {
                        importS.setBlinds(new Blinds());
                    }

                    if (!rowValues[8].replace("\"", "").equals("")) {
                        String[] breaks = rowValues[8].replace("\"", "").split("\\^");
                        ArrayList<Break> breakArrayList = new ArrayList<>();
                        if (breaks.length > 0) {
                            for (int i = 0; i < breaks.length; i++) {
                                breakArrayList.add(new Break(PLCommon.datetimeToTimestamp(breaks[i].split("/")[0]), PLCommon.datetimeToTimestamp(breaks[i].split("/")[1])));
                            }
                        }
                        importS.setBreaks(breakArrayList);
                    }
                    importS.setEntrants(Integer.parseInt(rowValues[9].replace("\"","")));
                    importS.setPlaced(Integer.parseInt(rowValues[10].replace("\"","")));

                    int state = 0;
                    if (!rowValues[11].replace("\"","").equals("Finished"))
                        state = 1;
                    importS.setState(state);
                    notesText = rowValues[12].replace("\"","");
                    notesText = notesText.replaceAll("@linereturn_rn@", "\n").replaceAll("@linereturn_r@", "\n").replaceAll("@linereturn_n@", "\n");
                    notesList = Arrays.asList(notesText.split("@note_div@"));
                    notes.clear();
                    for (String current : notesList) {
                        if (!current.equals("")) {
                            notes.add(new Note(0, 0, current));
                        }
                    }
                    importS.setNotes(notes);

                    int filtered = 0;
                    if (!rowValues[11].replace("\"","").equals("No"))
                        filtered = 1;
                    importS.setFiltered(filtered);
                    ActivitySettings.this.importedSessions.add(importS);
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                //error logging
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            new ImportLocations().execute();
        }
    }

    public class ImportLocations extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Importing locations...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.importLocations(ActivitySettings.this.importedSessions);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            new ImportGames().execute();
            Toast.makeText(ActivitySettings.this, "Locations imported!", Toast.LENGTH_SHORT).show();
        }
    }

    public class ImportGames extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Importing games...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.importGames(ActivitySettings.this.importedSessions);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            new ImportGameFormats().execute();
            Toast.makeText(ActivitySettings.this, "Games imported!", Toast.LENGTH_SHORT).show();
        }
    }

    public class ImportGameFormats extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Importing game formats...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.importGameFormats(ActivitySettings.this.importedSessions);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            new ImportBlinds().execute();
            Toast.makeText(ActivitySettings.this, "Game formats imported!", Toast.LENGTH_SHORT).show();
        }
    }

    public class ImportBlinds extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Importing Blinds...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.importBlinds(ActivitySettings.this.importedSessions);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            new ImportSessions().execute();
        }
    }

    public class ImportSessions extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Importing sessions...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            Boolean success = db.importSessions(ActivitySettings.this.importedSessions);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            new LoadLocations().execute();
            new LoadGames().execute();
            new LoadGameFormats().execute();
            new LoadBlinds().execute();

            Toast.makeText(ActivitySettings.this, getResources().getString(R.string.info_import_complete), Toast.LENGTH_LONG).show();
        }
    }

    public class AutoExportCSV extends AsyncTask<Void, Void, SessionSet> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting CSV restore point...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected SessionSet doInBackground(Void... params) {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
            return new SessionSet(dbHelper.getSessions("0,1", "DESC", null));
        }

        @Override
        protected void onPostExecute(SessionSet allSessions) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String csvName = "pokerledger_auto_" + df.format(c.getTime()) + ".csv";
            File dst = new File(ActivitySettings.this.backupLocation, csvName);

            String data = allSessions.exportCSV();
            if (ContextCompat.checkSelfPermission(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Log.e("Exception", "shouldShowRequestPermissionRationale");
                } else {
                    Log.e("Exception", "ActivityCompat.requestPermissions");
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(ActivitySettings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

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
                Log.e("Exception", "Auto Writing cvs file failed: " + e.toString());
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Toast.makeText(ActivitySettings.this, "In case something goes wrong your current data has been exported to " + ActivitySettings.this.backupLocation + "/" + csvName, Toast.LENGTH_LONG).show();
            if (ActivitySettings.this.method == OVERWRITE_BACKUP) {
                new BeginOverwriteCSV().execute();
            } else if (ActivitySettings.this.method == MERGE_BACKUP) {
                new MergeCSV().execute();
            }
        }
    }

    public class ExportCSV extends AsyncTask<Void, Void, SessionSet> {
        private ProgressDialog dialog = new ProgressDialog(ActivitySettings.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting CSV...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

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
            File dst = new File(ActivitySettings.this.backupLocation, csvName);

            String data = allSessions.exportCSV();
            if (ContextCompat.checkSelfPermission(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Log.e("Exception", "shouldShowRequestPermissionRationale");
                } else {
                    Log.e("Exception", "ActivityCompat.requestPermissions");
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(ActivitySettings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

            try {
                dst.createNewFile();
                FileOutputStream fOut = new FileOutputStream(dst);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(data);

                myOutWriter.close();

                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                Log.e("Exception", "Manual Writing cvs file failed: " + e.toString());
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Toast.makeText(ActivitySettings.this, "Your data has been exported to " + ActivitySettings.this.backupLocation + "/" + csvName, Toast.LENGTH_LONG).show();
        }
    }
}
