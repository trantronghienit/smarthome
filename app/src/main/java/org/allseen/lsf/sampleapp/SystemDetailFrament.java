package org.allseen.lsf.sampleapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circularseekbar.CircularSeekBar;


import org.allseen.lsf.sdk.Color;
import org.allseen.lsf.sdk.Lamp;
import org.allseen.lsf.sdk.LampCapabilities;
import org.allseen.lsf.sdk.LampListener;
import org.allseen.lsf.sdk.LightingDirector;
import org.allseen.lsf.sdk.LightingItemErrorEvent;
import org.allseen.lsf.sdk.LightingSystemQueue;
import org.allseen.lsf.sdk.MutableColorItem;

/**
 * Created by admin on 3/21/2017.
 */

// AllLightingItemListener
@SuppressLint("ValidFragment")
public class SystemDetailFrament extends PageFrameParentFragment implements CompoundButton.OnCheckedChangeListener  ,
        CircularSeekBar.OnCircularSeekBarChangeListener , View.OnClickListener {
    private static final long CALLBACK = 500;
    private TextView txtHumidity  , txtTemperature , txtPh;
    private CircularSeekBar seekBar;
    private Switch swFan , swPuml;
    private Lamp lamp;
    private String TAG_LOG = "SystemDetailFrament";
    private final String FAN = "BR40";
    private final String PUML = "BR38";
    public static final String ID_TEMPERATURE_HUMIDITY_DEVICES = "2d4a34658c1266735159b91744d0a39a";
    public static final String ID_PH_DEVI="";
    public static String TAG;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void inIt(View view){
        txtHumidity = (TextView) view.findViewById(R.id.txt_humidity);
        txtTemperature = (TextView) view.findViewById(R.id.txt_temperature);
        txtPh = (TextView) view.findViewById(R.id.txt_ph);
        swFan = (Switch) view.findViewById(R.id.swich_fan);
        swPuml = (Switch) view.findViewById(R.id.swich_puml);
        seekBar = (CircularSeekBar) view.findViewById(R.id.seekBar_corner);
        seekBar.setEnabled(false);
    }

    @Override
    public PageFrameChildFragment createTableChildFragment() {
        return null;
    }

    @Override
    public PageFrameChildFragment createInfoChildFragment() {
        return null;
    }

    @Override
    public PageFrameChildFragment createPresetsChildFragment() {
        return new PageFrameChildFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_detail , container , false);
        inIt(view);
        TAG = getTag();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // setText

        swFan.setOnCheckedChangeListener(this);
        swFan.setOnClickListener(this);
        swPuml.setOnCheckedChangeListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String lampType = null;
        try{
            lampType = getLamp().getDetails().getLampType().name();
            switch (buttonView.getId()){
                case R.id.swich_fan:
                    if (lampType.equalsIgnoreCase(FAN)){
                        if(!swFan.isChecked()){
                            Toast.makeText(getActivity() , "Off Fan" , Toast.LENGTH_SHORT).show();
                            lamp.setPowerOn(false);
                        } else{
                            lamp.setPowerOn(true);
                            Toast.makeText(getActivity() , "On Fan" , Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        showToastMes("Tên thiết bị : Fan , không phát hiện thiết bị trong mạng, kiểu thiết bị " + lampType);
                    }
                    break;
                case R.id.swich_puml:
                    if (lampType.equalsIgnoreCase(PUML)){
                        if(!swPuml.isChecked()){
                            Toast.makeText(getActivity() , "Close water" , Toast.LENGTH_SHORT).show();
                            lamp.setPowerOn(false);
                        } else {
                            Toast.makeText(getActivity() , "Open Water" , Toast.LENGTH_SHORT).show();
                            lamp.setPowerOn(true);
                        }
                    }else {
                        showToastMes("Tên thiết bị : Puml , không phát hiện thiết bị trong mạng, kiểu thiết bị " + lampType);
                    }
                    break;
            }
        }catch (Exception e){
            showToastMes("Có thể thiết bị không hợp lệ");
        }
    }

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (lamp != null){
            showToastMes("phát hiện thiết bị " + lamp.getName());
            String result = lamp.getDetails().getModel().name();
            Toast.makeText(getActivity(), "" + result, Toast.LENGTH_SHORT).show();
        }else {
            showToastMes("không phát hiện ra thiết bị");
        }
    }

    private void showToastMes(String mes){
        Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {

    }

    public void onLampChanged(final Lamp lamp) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setLamp(lamp);
//            }
//        } , CALLBACK);
        setLamp(lamp);
        if (lamp != null){
            swPuml.setEnabled(true);
            swFan.setEnabled(true);
            seekBar.setEnabled(true);
            //todo Working changes
            MutableColorItem colorItem = LightingDirector.get().getLamp(lamp.getId());
            boolean isDevice = lamp.getId().equalsIgnoreCase(SystemDetailFrament.ID_TEMPERATURE_HUMIDITY_DEVICES);
            if (colorItem != null && isDevice) {
                Color color = colorItem.getColor();
                int temperature = color.getBrightness();
                int humidity = color.getSaturation();
                int g = color.getHue();
                int t = color.getColorTemperature() % 1000;
                float pH = cuclulator(g , t);
                txtHumidity.setText(String.valueOf(humidity));
                txtTemperature.setText(String.valueOf(temperature));
                txtPh.setText(String.valueOf(pH));
                Log.i("IdLamp: " , lamp.getId());
                Log.i("Lamp" , "" + lamp.getName() + "\tNhiệt độ: " + temperature + "\tĐộ ẩm: " + humidity + "\tPh: " + pH +
                "\tHue " + g + "\ttemple: "+ t);

            }
        }else {
            swPuml.setEnabled(false);
            swFan.setEnabled(false);
            seekBar.setEnabled(false);
        }
    }

    private void setLamp(Lamp lamp) {
        this.lamp = lamp;
    }
    public Lamp getLamp() {
        return lamp;
    }

    private float cuclulator(int g , int t){
        return g + (t / 10.0f);
    }

    public void onLampRemoved(Lamp lamp) {

    }

//    @Override
//    public void onLampError(LightingItemErrorEvent lightingItemErrorEvent) {
//
//    }

    // click swtich and seekbar
    @Override
    public void onClick(View v) {
        String MesDevices = "có thể do không phát hiện ra thiết bị khả dụng Xin kiểm tra lại";
        switch (v.getId()){
            case R.id.swich_fan:
                if (!swFan.isEnabled()) showToastMes(MesDevices);
                return;
            case R.id.swich_puml:
                if (!swPuml.isEnabled()) showToastMes(MesDevices);
                return;
            case R.id.seekBar_corner:
                if (!swPuml.isEnabled()) showToastMes(MesDevices);
                return;
        }
    }


}
