package com.cameraapp.ui.dashboard;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cameraapp.CameraFragment;
import com.cameraapp.ListAdapters;
import com.cameraapp.R;
import com.cameraapp.db.DataFile;
import com.cameraapp.db.ListVariabel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final int RESULT_CANCELED = 0;
    private RecyclerView recyclerView;
    TextView count_file;
    ListAdapters adapter;
    List<String> tempListFile;
    int RESULT_OK = 100;

    private static final int REQUEST_READ_STORAGE_PERMISSION = 100;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 100;
    private static final int READ_MEDIA_VIDEO_PERMISSION = 100;
    private static final int RECORD_AUDIO_PERMISSION = 100;
    private static final int REQUEST_CAMERA_PERMISSION =100 ;
    private DashboardViewModel mViewModel;
    ExtendedFloatingActionButton mAddFab;
    FloatingActionButton mAddAlarmFab;
    FloatingActionButton mAddPersonFab;
    boolean isAllFabsVisible;
    FragmentManager fragmentManager;
    DataFile dataFile;
    ContentLoadingProgressBar prog;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    public List appendData(){
        List<String> lisfile = new ArrayList<>();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().toString());
        File lf = new File(file.toString());
        File[]  lf_ = lf.listFiles();
        int i = 0;
        for (File fl : lf_) {
            String TAG = "CHECK";
            boolean isDirectory = file.isDirectory(); // true for directory and false for file
            if (!isDirectory) {
//                System.out.println(fl.getAbsolutePath().toString());
//                lisfile.add(fl.getAbsolutePath().toString());
//                Log.d(TAG, "appendData: "+fl.getAbsolutePath().toString());
//                Log.d(TAG, "appendData: file");
            } else {
                if(fl.getAbsolutePath().toString().split("/")[5].contains(".")){
                    lisfile.add(fl.getAbsolutePath().toString().split("/")[5].toString());
                    Log.d(TAG, "appendData: FOLDER");
                }
            }
        }
        Toast.makeText(getActivity(), "Update list sucessfully !\n", Toast.LENGTH_LONG).show();
        return lisfile;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
            return;
        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
            return;
        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, READ_MEDIA_VIDEO_PERMISSION);
            return;
        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION);
            return;
        }
        dataFile = new DataFile(getActivity(), new ListVariabel().NAMEDB, null, 1);
        appendData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
//
        prog = (ContentLoadingProgressBar) view.findViewById(R.id.prog);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        count_file = (TextView) view.findViewById(R.id.count_file);
        adapter = new ListAdapters();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Bind some data to the adapter
        tempListFile = appendData();
        if(tempListFile.size() == 0){
            recyclerView.setVisibility(View.GONE);
            count_file.setVisibility(View.VISIBLE);
        }else{
            count_file.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(tempListFile);
        }

        fragmentManager = getFragmentManager();
        mAddFab = view.findViewById(R.id.add_fab);
        // FAB button
        mAddAlarmFab = view.findViewById(R.id.add_alarm_fab);
        mAddPersonFab = view.findViewById(R.id.add_person_fab);
        //
        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        //
        isAllFabsVisible = false;
        //
        mAddFab.shrink();
        //
        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllFabsVisible) {
                    mAddAlarmFab.show();
                    mAddPersonFab.show();
                    mAddFab.extend();
                    isAllFabsVisible = true;
                }else {
                    mAddAlarmFab.hide();
                    mAddPersonFab.hide();
                    mAddFab.shrink();
                    isAllFabsVisible = false;
                }
            }
        });
//
        mAddAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_CAMERA_IMAGE);

                // Create a fragment transaction.
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Replace the current fragment with the new fragment.
                Fragment newFragment = new CameraFragment();
                fragmentTransaction.replace(R.id.dashboard, newFragment);
                // Add the transaction to the back stack.
                fragmentTransaction.addToBackStack(null);
                // Commit the transaction.
                fragmentTransaction.commit();

                mAddAlarmFab.hide();
                mAddPersonFab.hide();
                mAddFab.shrink();
                isAllFabsVisible = false;
            }
        });

        mAddPersonFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempListFile = appendData();
                adapter.setData(tempListFile);
                mAddAlarmFab.hide();
                mAddPersonFab.hide();
                mAddFab.shrink();
                isAllFabsVisible = false;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        // TODO: Use the ViewModel
    }
}