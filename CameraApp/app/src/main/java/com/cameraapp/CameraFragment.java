package com.cameraapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cameraapp.db.DataFile;
import com.cameraapp.db.ListVariabel;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
//import android.view.Frame;
import com.google.android.gms.vision.Frame.Builder;
import com.otaliastudios.cameraview.controls.Facing;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CAMERA_IMAGE = 101;
    private static final int REQUEST_CAMERA_PERMISSION =100 ;
    FloatingActionButton getPicture ;
    FloatingActionButton back_dashboard;
    Bitmap bitm;
    ProgressDialog progressDialog;
    ConstraintLayout img_page;
    ConstraintLayout page_result;
    TextView txt_result;
    TextView txt_info_proses;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    Intent intent;
    String text_res;
    DataFile dataFile;

    public CameraFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataFile = new DataFile(getActivity(), new ListVariabel().NAMEDB, null, 1);
//        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, REQUEST_CAMERA_IMAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        img_page = (ConstraintLayout) view.findViewById(R.id.img_page);
        page_result = (ConstraintLayout) view.findViewById(R.id.page_result);
        txt_result = (TextView) view.findViewById(R.id.txt_result);
        back_dashboard = (FloatingActionButton) view.findViewById(R.id.back_dashboard);
        txt_info_proses = (TextView) view.findViewById(R.id.txt_info_proses );
        CameraView cameraView = view.findViewById(R.id.camera_view);
        getPicture = view.findViewById(R.id.getPicture);

        // prepare cameraview
        cameraView.setFacing(Facing.BACK);
        cameraView.getTouchscreenBlocksFocus();
        cameraView.setFocusable(true);
        cameraView.setLifecycleOwner(getViewLifecycleOwner());
        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_info_proses.setVisibility(View.VISIBLE);
                cameraView.takePicture();
            }
        });

        // initial listener event
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(@NonNull CameraOptions options) {
                super.onCameraOpened(options);
            }

            @Override
            public void onCameraClosed() {
                super.onCameraClosed();
            }

            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                Bitmap btmp = bytesToBitmap(result.getData());
                try {
                    showProgresDialog();
                    saveFileFromBitmap(btmp,
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().toString()),
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    // comparing bytes bitmap for saving result
    public Bitmap bytesToBitmap(byte[] bytes) {
        BitmapFactory bitmapFactory = new BitmapFactory();
        Bitmap bitmap = bitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    // save to storage when image have capture and initialization for file
    public void saveFileFromBitmap(Bitmap bitmaps, File file, String path) throws IOException {
        // prepare byte data to file and write to storage
        Date date = new Date();
        long timestamp_createat = date.getTime();
        String format = "png";
        String onlyfile = "IMG_"+timestamp_createat;
        String final_name = "IMG_"+timestamp_createat+"."+format;
        File f = new File(file, final_name);
        f.createNewFile();
        Bitmap bitmap = bitmaps;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
        if(f.exists() && file_size > 0){
            txt_info_proses.setVisibility(View.GONE);
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
            String imagePath = f.getAbsolutePath().toString();
            Bitmap bitms = BitmapFactory.decodeFile(imagePath);
            Frame frame = new Frame.Builder().setBitmap(bitms).build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
            if(textBlocks.size() == 0){
                File el_fil = new File(imagePath);
                FileOutputStream fos_del = new FileOutputStream(f);
                el_fil.delete();
                fos_del.flush();
                fos_del.close();
                Toast.makeText(getActivity(), "INFORMATION IS EMPTY!", Toast.LENGTH_SHORT).show();
                FragmentManager parentFragmentManager = getParentFragmentManager();
                parentFragmentManager.popBackStack();
            }else{
                text_res = "";
                for (int i = 0; i < textBlocks.size(); i++) {
                    TextBlock textBlock = textBlocks.valueAt(i);
                    text_res += textBlock.getValue();
                }
                Toast.makeText(getActivity(), "INFORMATION HAVE SAVED!", Toast.LENGTH_SHORT).show();

                // cache text value to concert bitmap
                txt_result.setText(text_res);
                txt_result.buildDrawingCache();
//                // showing result into view
                img_page.setVisibility(View.GONE);
                page_result.setVisibility(View.VISIBLE);

                // back to dashboard
                back_dashboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        File file_result = new File(path+"/"+onlyfile+".txt");
//                        try {
//                            FileOutputStream stream = new FileOutputStream(file_result);
//                            stream.write(text_res.getBytes());
//                            stream.flush();
//                            stream.close();
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        FragmentManager parentFragmentManager = getParentFragmentManager();
                        parentFragmentManager.popBackStack();
                    }
                });
            }
        }else{
            FragmentManager parentFragmentManager = getParentFragmentManager();
            parentFragmentManager.popBackStack();
        }
    }

    public void showProgresDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        // Show the progress dialog.
//        progressDialog.show();
        // Do something that takes time, such as loading data from a network.
        // Dismiss the progress dialog.
    }
}