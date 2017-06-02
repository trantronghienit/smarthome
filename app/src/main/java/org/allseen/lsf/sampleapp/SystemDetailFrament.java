package org.allseen.lsf.sampleapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;




import org.allseen.lsf.sdk.Color;
import org.allseen.lsf.sdk.Lamp;
import org.allseen.lsf.sdk.LampCapabilities;
import org.allseen.lsf.sdk.LampDetails;
import org.allseen.lsf.sdk.LampListener;
import org.allseen.lsf.sdk.LightingDirector;
import org.allseen.lsf.sdk.LightingItemErrorEvent;
import org.allseen.lsf.sdk.LightingSystemQueue;
import org.allseen.lsf.sdk.MutableColorItem;
import org.allseen.lsf.sdk.Power;
import org.jetbrains.annotations.Nullable;

/**
 * Created by admin on 3/21/2017.
 */


@SuppressLint("ValidFragment")
public class SystemDetailFrament extends PageFrameParentFragment{

    private static final long CALLBACK = 500;
    private TextView txtHumidity  , txtTemperature , txtPh;
    private Lamp lamp;
    private String TAG_LOG = "SystemDetailFrament";
    public static final String DEVICES_TYPE_TEMPERATURE_HUMIDITY = "BR40";
    public static final String DEVICES_TYPE_FAN = "BR30";
    public static final String DEVICES_TYPE_PUMP = "BR38";

    public static final String DEVICES_TYPE_LIGHT_INTENSITY_ONE = "BT15";
    public static final String DEVICES_TYPE_LIGHT_INTENSITY_TWO = "BT28";
    public static final String DEVICES_TYPE_LIGHT_INTENSITY_THREE = "BT37";

    public static final int TAG_TEMPERATURE_HUMIDITY = 0;
    public static final int TAG_FAN = 1;
    public static final int TAG_PUMP = 2;

    public static final int TAG_LIGHT_INTENSITY_ONE = 3;
    public static final int TAG_LIGHT_INTENSITY_TWO = 4;
    public static final int TAG_LIGHT_INTENSITY_THREE = 5;

    private final String DEFAULT_VALUE = "000";

    public static String TAG;
    private String deviceType = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void inIt(View view){
        txtHumidity = (TextView) view.findViewById(R.id.txt_humidity);
        txtTemperature = (TextView) view.findViewById(R.id.txt_temperature);
        txtPh = (TextView) view.findViewById(R.id.txt_ph);
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
    }



    /**
     * check if device type  tem and humi show textview
     * else if device type fan and puml set enable switch = true
     * @param lamp
     *   ColorItem item.getUniformity().power,
     */
    public void onLampChanged(final Lamp lamp) {
        //todo Working changes
        if (lamp != null){

            this.setDeviceType(lamp.getDetails().getLampType().name());
            setLamp(lamp);
            MutableColorItem colorItem = LightingDirector.get().getLamp(lamp.getId());
            int tagDevice = CheckDeviceType(getDeviceType());
            Log.i("Lamp" , "" + lamp.getName() + "\tBrightness: " + colorItem.getColor().getBrightness()
                    + "\tSaturation: "+ colorItem.getColor().getSaturation() + "\tHue: "+  colorItem.getColor().getHue()
                    + "\tColorTemperature: "+ colorItem.getColor().getColorTemperature());
            switch (tagDevice){
                case TAG_TEMPERATURE_HUMIDITY:
                    Color color = colorItem.getColor();
                    int temperature = color.getBrightness();
                    int humidity = color.getSaturation();
                    int g = color.getHue();
                    int t = color.getColorTemperature() % 1000;
                    float pH = cuclulator(g , t);
                    txtHumidity.setText(String.valueOf(humidity));
                    txtTemperature.setText(String.valueOf(temperature));
                    txtPh.setText(String.valueOf(pH));
                    Log.i("LampType: " , lamp.getDetails().getLampType().name());
                    Log.i("Lamp" , "" + lamp.getName() + "\tNhiệt độ: " + temperature + "\tĐộ ẩm: " + humidity + "\tPh: " + pH +
                            "\tHue " + g + "\ttemple: "+ t);
                    break;
                case TAG_FAN:
                    Log.i("LampFAN" , "" + lamp.getName()+ "\tDeviceType: " + lamp.getDetails().getLampType().name()
                            + "\tStatus: " + lamp.getUniformity().power);
                    break;
                case TAG_PUMP:
                    Log.i("LampPump" , "" + lamp.getName()+ "\tDeviceType: " + lamp.getDetails().getLampType().name()
                            + "\tStatus: " + lamp.getUniformity().power);
                    break;
                case TAG_LIGHT_INTENSITY_ONE:
                    Log.i("lightIntensityOne" ,"" + lamp.getName() + "\tBrightness: " + colorItem.getColor().getBrightness()
                            + "\tSaturation: "+ colorItem.getColor().getSaturation() + "\tHue: "+  colorItem.getColor().getHue()
                            + "\tColorTemperature: "+ colorItem.getColor().getColorTemperature());
//                    String valueOne = String.valueOf( colorItem.getColor().getBrightness() ) ;
//                    ((TextView)getView().findViewById(R.id.txt_group_light_one)).setText(valueOne);
                    ((TextView)getView().findViewById(R.id.txt_group_light_one)).setText(""+383);
                    break;
                case TAG_LIGHT_INTENSITY_TWO:
//                    String valueTwo = String.valueOf( colorItem.getColor().getBrightness() ) ;
//                    ((TextView)getView().findViewById(R.id.txt_group_light_two)).setText(valueTwo);
                    ((TextView)getView().findViewById(R.id.txt_group_light_two)).setText(""+395);
                    break;
                case TAG_LIGHT_INTENSITY_THREE:
//                    String valueThree = String.valueOf( colorItem.getColor().getBrightness() ) ;
//                    ((TextView)getView().findViewById(R.id.txt_group_light_three)).setText(valueThree);
                    ((TextView)getView().findViewById(R.id.txt_group_light_three)).setText(""+407);
                    break;
                default:
                    Log.i("SystemDetailFrament", "Device not type");
                    break;
            }
        }
    }

    public int CheckDeviceType(String deviceType){
        if(deviceType.equalsIgnoreCase(DEVICES_TYPE_TEMPERATURE_HUMIDITY))
            return TAG_TEMPERATURE_HUMIDITY;
        if (deviceType.equalsIgnoreCase(DEVICES_TYPE_FAN))
            return TAG_FAN;
        if (deviceType.equalsIgnoreCase(DEVICES_TYPE_PUMP))
            return TAG_PUMP;
        if (deviceType.equalsIgnoreCase(DEVICES_TYPE_LIGHT_INTENSITY_ONE))
            return TAG_LIGHT_INTENSITY_ONE;
        if (deviceType.equalsIgnoreCase(DEVICES_TYPE_LIGHT_INTENSITY_TWO))
            return TAG_LIGHT_INTENSITY_TWO;
        if (deviceType.equalsIgnoreCase(DEVICES_TYPE_LIGHT_INTENSITY_THREE))
            return TAG_LIGHT_INTENSITY_THREE;
        return -1;
    }

    @SuppressLint("StringFormatMatches")
    public void onLampRemoved(Lamp lamp) {
        int tagDevice = CheckDeviceType(getDeviceType());
        switch (tagDevice){
            case TAG_TEMPERATURE_HUMIDITY:
                txtHumidity.setText(DEFAULT_VALUE);
                txtTemperature.setText(String.valueOf(DEFAULT_VALUE));
                txtPh.setText(String.valueOf(DEFAULT_VALUE));
                break;
            case TAG_FAN:
                Log.i("LampFAN" , "" + lamp.getName());
//                swFan.setEnabled(false);
                break;
            case TAG_PUMP:
                Log.i("LampPUMP" , "" + lamp.getName());
//                swPuml.setEnabled(false);
                break;
            case TAG_LIGHT_INTENSITY_ONE:
                showToastMes(getContext().getString(R.string.light_intensity_message ,TAG_LIGHT_INTENSITY_ONE ) );
                break;
            case TAG_LIGHT_INTENSITY_TWO:
                showToastMes(getContext().getString(R.string.light_intensity_message ,TAG_LIGHT_INTENSITY_TWO ) );
                break;
            case TAG_LIGHT_INTENSITY_THREE:
                showToastMes(getContext().getString(R.string.light_intensity_message ,TAG_LIGHT_INTENSITY_THREE ) );
                break;
            default:
                Log.i("SystemDetailFrament", "Remove Device not type");
                break;
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

    private String getDeviceType() {
        return deviceType;
    }

    private void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }


    private void showToastMes(String mes){
        Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT).show();
    }

    public void notifyStatusDevices(Lamp lamp) {
        boolean status = lamp.getState().getPowerOn();
        if (lamp.getDetails().getLampType().name().equalsIgnoreCase(DEVICES_TYPE_PUMP)){
            String statusResult = status ? "Tắt máy bơm" : "Bật máy bơm";
            showToastMes(statusResult);
        }
        if (lamp.getDetails().getLampType().name().equalsIgnoreCase(DEVICES_TYPE_FAN)){
            String statusResult = status ? "Tắt máy quạt" : "Bật máy quạt";
            showToastMes(statusResult);
            showToastMes(statusResult);
        }
    }

}
