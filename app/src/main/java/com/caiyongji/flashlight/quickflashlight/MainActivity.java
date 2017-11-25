package com.caiyongji.flashlight.quickflashlight;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ToggleButton toggleButton;
    private CameraManager cameraManager;
    public String cameraId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        flashOn();
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    flashOn();
                } else {
                    flashOff();
                }
            }
        });
    }

    private void init() {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            List<String> ids = Arrays.asList(cameraManager.getCameraIdList());
            //Only get the first available flash.
            for (String cameraId : ids) {
                if (cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                    this.cameraId = cameraId;
                    cameraManager.registerTorchCallback(new CameraManager.TorchCallback() {
                        @Override
                        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                            super.onTorchModeChanged(cameraId, enabled);
                            //NO need to check cameraId
                            if (enabled){
                                toggleButton.setChecked(true);
                                toggleButton.setBackground(getDrawable(R.drawable.ic_highlight_yellow_24dp));
                            }else {
                                toggleButton.setChecked(false);
                                toggleButton.setBackground(getDrawable(R.drawable.ic_highlight_black_24dp));
                            }
                        }
                    },null);
                    return;
                }
            }
        } catch (CameraAccessException e) {
            Log.e("caiyongji", e.getMessage());
            Toast.makeText(this, R.string.flash_error, Toast.LENGTH_LONG).show();
        }
    }

    public void flashOn(){
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void flashOff(){
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        flashOn();
    }
}
