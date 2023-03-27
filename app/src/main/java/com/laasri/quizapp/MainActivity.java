package com.laasri.quizapp;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder; //<---
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;//<---
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //Step 1: Declaration
    EditText etLogin, etPassword;
    Button bLogin, bGeo, bCam;
    TextView tvRegister;
    //Authentification Firebase
    FirebaseAuth mAuth;
    //Enregistrement Microphone
    private static int MPC = 200;
    MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isMicroPresent()) {
            getMicropermission();
        }
        //Step 2: Recuperation des ids

        etLogin = (EditText) findViewById(R.id.etMail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);

        bGeo = (Button) findViewById(R.id.bGeo);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        mAuth = FirebaseAuth.getInstance();
        bCam = (Button) findViewById(R.id.bCam);

        //Step 3: Association de listeners
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Step 4: Traitement
                String mail = etLogin.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Entrer tous les informations", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(mail, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Bienvenue dans QuizzApp", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, Quiz1.class));


                                    }
                                }
                            });

                }
            }
        });
        //Géolocalisation
        bGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Locate.class));
            }
        });
        //Caméra
        bCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Step 4: Traitement
                startActivity(new Intent(MainActivity.this, Register.class));

            }
        });
    }

    //Microphone
    public void bntRecord(View v) {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordpath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Enregistrement démaré", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void bntStop(View v) {
        mediaRecorder.stop();
        Toast.makeText(this, "Enregistrement arreté", Toast.LENGTH_LONG).show();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private boolean isMicroPresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }

    }

    private void getMicropermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MPC);
        }
    }

    private String getRecordpath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "Recordsession" + ".mp3");
        return file.getPath();
    }
}

