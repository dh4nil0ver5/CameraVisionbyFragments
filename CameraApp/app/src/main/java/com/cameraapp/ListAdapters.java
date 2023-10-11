package com.cameraapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListAdapters extends RecyclerView.Adapter<ListAdapters.MyViewHolder> {

    private List<String> data;

    public ListAdapters() {
        data = new ArrayList<>();
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ListAdapters.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    public void alertDeleteFile(View holder, File file){
        AlertDialog alertDialog = new AlertDialog.Builder(holder.getContext())
                .setTitle("Confrimation")
                .setMessage("this file will be delete permanetly, are you sure ?")
                .setPositiveButton("Yes, delete please !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(file.exists()){
                            file.delete();
                            Toast.makeText(holder.getContext(), "Delete success !", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(holder.getContext(), "File not found !", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("No, still needed !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String item = data.get(position);
        holder.textView.setText(item);
        int pst = position;
        String location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().toString();
        holder.delFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(holder.itemView.getContext(), location+"/"+data.get(pst), Toast.LENGTH_LONG).show();
                File file = new File(location+"/"+data.get(pst));
                alertDeleteFile(view, file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        FloatingActionButton delFile;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.courseName);
            delFile = itemView.findViewById(R.id.delFile);
        }
    }
}
