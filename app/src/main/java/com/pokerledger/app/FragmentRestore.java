package com.pokerledger.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by max on 11/1/15.
 */
public class FragmentRestore extends DialogFragment  implements AdapterView.OnItemClickListener {

    private List<String> item = null;
    private List<String> path = null;
    private String root;
    private TextView myPath;
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restore, null, false);
        myPath = (TextView) view.findViewById(R.id.path);
        list = (ListView) view.findViewById(R.id.list);
        root = Environment.getExternalStorageDirectory().getPath();

        getDir(root);
        return view;
    }

    private void getDir(String dirPath)
    {
        myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if(!dirPath.equals(root))
        {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for(int i=0; i < files.length; i++)
        {
            File file = files[i];

            if(!file.isHidden() && file.canRead()){
                path.add(file.getPath());
                if(file.isDirectory()){
                    item.add(file.getName() + "/");
                }else{
                    item.add(file.getName());
                }
            }
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(getActivity(), R.layout.list_file_row, item);
        list.setAdapter(fileList);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //ListView l, View v, int position, long id
        File file = new File(path.get(position));

        if (file.isDirectory())
        {
            if(file.canRead()){
                getDir(path.get(position));
            }else{
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", null).show();
            }
        }else {
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("[" + file.getName() + "]")
                    .setPositiveButton("OK", null).show();

        }
    }

}
