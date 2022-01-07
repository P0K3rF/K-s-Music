package com.example.kmusic;

import static android.content.ContentValues.TAG;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media.AudioManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.Transliterator;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button songname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        songname=findViewById(R.id.button);


        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(getApplicationContext(), "You have given file permission ", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[mySongs.size()];
                        for (int i = 0; i < mySongs.size(); i++) {
                            items[i] = mySongs.get(i).getName().replace("mp3", "").trim();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_1, items) {

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.WHITE);
                                return view;
                            }
                        };

                        listView.setBackgroundColor(Color.BLACK);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                {

                                    String currentSong = listView.getItemAtPosition(position).toString();
                                    Intent intent2 = new Intent(MainActivity.this, Playsong.class);
                                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent2.putExtra("songList", mySongs);
                                    intent2.putExtra("currentSong", currentSong);
                                    intent2.putExtra("position", position);
                                    startActivity(intent2);
                                }
                            }

                        });
                        songname.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), Playsong.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);
                            }
                        });

                    }


                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();

                    }
                })
                .check();
    }
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList=new ArrayList();
        File[] songs=file.listFiles();
        if(songs !=null){
            for(File myfile: songs){
                if(!myfile.isHidden() && myfile.isDirectory()){
                    arrayList.addAll(fetchSongs(myfile));
                }
                else{
                    if(myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }
}