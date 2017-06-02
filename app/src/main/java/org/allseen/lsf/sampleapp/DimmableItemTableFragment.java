/*
 * Copyright (c) AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.allseen.lsf.sampleapp;

import org.allseen.lsf.sdk.ColorItem;
import org.allseen.lsf.sdk.Lamp;
import org.allseen.lsf.sdk.LampCapabilities;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * thay đổi view picker thay cho seekbar
 */
public abstract class DimmableItemTableFragment
        extends ScrollableTableFragment
        implements
        View.OnClickListener {

    protected abstract int getInfoButtonImageID();

    protected abstract Fragment getInfoFragment();

    public void addItems(ColorItem[] items) {
        for (ColorItem item : items) {
            addItem(item);
        }
    }

    public void addItem(ColorItem item) {
        addItem((Lamp) item, 0);
    }

    public void addItem(Lamp item, int infoBG) {
        if (item != null) {
            insertDimmableItemRow(
                    getActivity(),
                    item.getId(),
                    item.getTag(),
                    item.isOn(),
                    item.getUniformity().power,
                    item.getName(),
                    item.getColor().getBrightness(),
                    item.getUniformity().brightness,
                    infoBG,
                    item.getCapability().dimmable >= LampCapabilities.SOME,
                    item);
            updateLoading();
        }
    }

    public <T> TableRow insertDimmableItemRow(Context context, String itemID, Comparable<T> tag, boolean powerOn, boolean uniformPower, String name, int viewBrightness, boolean uniformBrightness, int infoBG, boolean enabled, Lamp lamp) {
        return insertDimmableItemRow(
                context,
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                itemID,
                tag,
                powerOn,
                uniformPower,
                name,
                viewBrightness,
                uniformBrightness,
                infoBG,
                enabled,
                lamp
        );
    }


    //todo : set for view fragment one
    private List<Integer> listData;
    public <T> TableRow insertDimmableItemRow(Context context, LayoutInflater inflater,
                                              String itemID, Comparable<T> tag, boolean powerOn, boolean uniformPower,
                                              String name, int viewBrightness, boolean uniformBrightness, int infoBG,
                                              boolean enabled, Lamp lamp) {

        Log.d(SampleAppActivity.TAG, "insertDimmableItemRow(): " + itemID + ", " + tag + ", " + name);

        final boolean isEnabled = enabled;
        TableRow tableRow = (TableRow) table.findViewWithTag(itemID);
        // nếu là thiết bị nhiệt độ độ ẩm thì ẩn đi
        String lampType = lamp.getDetails().getLampType().name();
        boolean isDevivesOnOff = lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_PUMP)
                || lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_FAN) || lampType.equalsIgnoreCase("INVALID");
        boolean isDevicesOnlyShow = lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_TEMPERATURE_HUMIDITY)
                || lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_LIGHT_INTENSITY_ONE)
                || lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_LIGHT_INTENSITY_TWO)
                || lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_LIGHT_INTENSITY_THREE);

        int drawable = R.drawable.light_status_icon;
        boolean flagSetBackground;
//        int tagDevice = new SystemDetailFrament().CheckDeviceType(lampType);
//        Toast.makeText(context, ""+tagDevice , Toast.LENGTH_SHORT).show();
        listData = new ArrayList<>();
        for (int i = 0; i <= 100;i++){
            listData.add(i);
        }
        if (tableRow == null) {
            tableRow = new TableRow(context);

            inflater.inflate(R.layout.view_dimmable_item_row, tableRow);

            ImageButton powerButton = (ImageButton) tableRow.findViewById(R.id.dimmableItemButtonPower);
            powerButton.setTag(itemID);
            powerButton.setBackgroundResource(uniformPower ? (powerOn ? R.drawable.power_button_on : R.drawable.power_button_off) : R.drawable.power_button_mix);
            powerButton.setOnClickListener(this);

            ImageView infoButton = (ImageView) tableRow.findViewById(R.id.dimmableItemButtonMore);
            infoButton.setImageResource(getInfoButtonImageID());
            infoButton.setTag(itemID);
            infoButton.setOnClickListener(this);

            TextView txtItemName = (TextView) tableRow.findViewById(R.id.dimmableItemRowText);
            txtItemName.setText(name);

            WheelPicker numberPicker = (WheelPicker) tableRow.findViewById(R.id.dimmableItemRowSlider);
            numberPicker.setData(listData);
            if (isDevivesOnOff) {

                if (lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_PUMP)) {
                    drawable = R.drawable.ic_pump;
                } else if (lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_FAN)) {
                    drawable = R.drawable.ic_fan;
                }
                powerButton.setVisibility(View.VISIBLE);
                infoButton.setEnabled(false);
                numberPicker.setVisibility(View.GONE);
                flagSetBackground = false;
                // device only show value then hide powerButton and infoButton disable click
            } else if (isDevicesOnlyShow) {
                if (lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_TEMPERATURE_HUMIDITY)) {
                    drawable = R.drawable.ic_humidity_temper;
                } else {
                    drawable = R.drawable.ic_lighting;
                }
                numberPicker.setVisibility(View.GONE);
                powerButton.setVisibility(View.INVISIBLE);
                infoButton.setEnabled(false);
                flagSetBackground = false;
            } else {
                // else is lamp then hide powerButton , but show numberPicker and infoButton
                powerButton.setVisibility(View.INVISIBLE);
                numberPicker.setVisibility(View.VISIBLE);
                infoButton.setEnabled(true);
                flagSetBackground = true;
            }

            numberPicker.setSelectedItemPosition(viewBrightness);
            numberPicker.setTag(itemID);
            numberPicker.setSaveEnabled(false);

            PickerListener pickerListener = new PickerListener(numberPicker);
            numberPicker.setOnItemSelectedListener(pickerListener);
            numberPicker.setOnWheelChangeListener(pickerListener);
            numberPicker.setEnabled(isEnabled);

            if (infoBG != 0 && flagSetBackground == true) {
                infoButton.setBackgroundColor(infoBG);
            }

            // set icon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                infoButton.setImageDrawable(getResources().getDrawable(drawable, context.getTheme()));
            } else {
                infoButton.setImageDrawable(getResources().getDrawable(drawable));
            }
            tableRow.setTag(itemID);
            TableSorter.insertSortedTableRow(table, tableRow, tag);
        } else {
            ImageButton powerButton = (ImageButton) tableRow.findViewById(R.id.dimmableItemButtonPower);
            powerButton.setTag(itemID);
            powerButton.setBackgroundResource(uniformPower ? (powerOn ? R.drawable.power_button_on : R.drawable.power_button_off) : R.drawable.power_button_mix);
            powerButton.setOnClickListener(this);

            ImageView infoButton = (ImageView) tableRow.findViewById(R.id.dimmableItemButtonMore);
            infoButton.setImageResource(getInfoButtonImageID());
            infoButton.setTag(itemID);
            infoButton.setOnClickListener(this);

            ((TextView) tableRow.findViewById(R.id.dimmableItemRowText)).setText(name);

            WheelPicker numberPicker = (WheelPicker) tableRow.findViewById(R.id.dimmableItemRowSlider);
            numberPicker.setData(listData);

            if (isDevivesOnOff) {
                if (lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_PUMP)) {
                    drawable = R.drawable.ic_pump;
                } else if (lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_FAN)) {
                    drawable = R.drawable.ic_fan;
                } else if (lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_TEMPERATURE_HUMIDITY)) {
                    drawable = R.drawable.ic_humidity_temper;
                }
                powerButton.setVisibility(View.VISIBLE);
                numberPicker.setVisibility(View.GONE);
                infoButton.setEnabled(false);
                flagSetBackground = false;
                // device only show value then hide powerButton and infoButton disable click
            } else if (isDevicesOnlyShow) {
                if (lampType.equalsIgnoreCase(SystemDetailFrament.DEVICES_TYPE_TEMPERATURE_HUMIDITY)) {
                    drawable = R.drawable.ic_humidity_temper;
                } else {
                    drawable = R.drawable.ic_lighting;
                }
                numberPicker.setVisibility(View.GONE);
                powerButton.setVisibility(View.INVISIBLE);
                infoButton.setEnabled(false);
                flagSetBackground = false;
            } else {
                numberPicker.setVisibility(View.VISIBLE);
                powerButton.setVisibility(View.INVISIBLE);
                flagSetBackground = true;
                infoButton.setEnabled(true);
            }

            numberPicker.setSelectedItemPosition(viewBrightness);
            numberPicker.setEnabled(isEnabled);

            // when click off seekbar set Value 0
//            if (!powerOn){
//                numberPicker.setValue(0);
//                numberPicker.setEnabled(false);
//            }

            if (infoBG != 0 && flagSetBackground == true) {
                infoButton.setBackgroundColor(infoBG);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                infoButton.setImageDrawable(getResources().getDrawable(drawable, context.getTheme()));
            } else {
                infoButton.setImageDrawable(getResources().getDrawable(drawable));
            }
            TableSorter.updateSortedTableRow(table, tableRow, tag);
        }
        ((SampleAppActivity) getActivity()).setTabTitles();
        return tableRow;
    }

    @Override
    public void onClick(View button) {
        Log.d(SampleAppActivity.TAG, "onClick()");

        int buttonID = button.getId();

        if (parent != null) {
            if (buttonID == R.id.dimmableItemButtonPower) {
                ((SampleAppActivity) getActivity()).togglePower(type, button.getTag().toString());
            } else if (buttonID == R.id.dimmableItemButtonMore) {
                ((SampleAppActivity) getActivity()).onItemButtonMore(parent, type, button, button.getTag().toString(), null, true);

            } else if (!((NumberPicker) button.findViewById(R.id.dimmableItemRowSlider)).isEnabled()) {
                ((SampleAppActivity) getActivity()).showToast(R.string.no_support_dimmable);
            }
        }
    }

    private void updateValue(int position, WheelPicker numberPicker) {
        if (parent != null) {
            int value = (int)numberPicker.getData().get(position);
            ((SampleAppActivity) getActivity()).setBrightness(type, numberPicker.getTag().toString(), value);
        }
    }

//    protected class PickerListener implements NumberPicker.OnScrollListener, NumberPicker.OnValueChangeListener {
//        private int scrollState = 0;
//
//        @Override
//        public void onScrollStateChange(NumberPicker view, int scrollState) {
//            this.scrollState = scrollState;
//            if (scrollState == SCROLL_STATE_IDLE) {
//                updateValue(view);
//
//            }
//        }
//
//        @Override
//        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//            if (scrollState == 0) {
//                updateValue(picker);
//            }
//        }
//    }

    protected class PickerListener implements WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {

        private WheelPicker picker;
        private boolean flag = true;
        public PickerListener(WheelPicker picker){
            this.picker = picker;
        }

        @Override
        public void onWheelScrolled(int offset) {
            if(offset ==0 && flag == true){
                updateValue(picker.getCurrentItemPosition() , picker);
            }
        }

        @Override
        public void onWheelSelected(int position) {
            updateValue(position , picker);
        }

        @Override
        public void onWheelScrollStateChanged(int state) {
//            if(state == 0)
//                Toast.makeText(Main2Activity.this, "onWheelScrollStateChanged "+state, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onItemSelected(WheelPicker picker, Object data, int position) {
            this.flag = false;
            updateValue(position , picker);
        }
    }
}
