package com.kaadas.lock.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.kaadas.lock.R;
import com.kaadas.lock.bean.FAQBean;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.DateUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TimePickerDialog extends Dialog {
    private Context context;
    private String startTime;
    private String endTime;

    private int screenWidth;

    private TimePicker timePickerStart;
    private TimePicker timePickerEnd;

    private View submitBtn;

    private ConfirmAction confirmAction;

    private boolean isEndTime = false;


    public TimePickerDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public TimePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected TimePickerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public TimePickerDialog(Context context, String startAndEndTime, ConfirmAction confirmAction) {
        super(context, R.style.dialog);

        this.context = context;

        if(isNull(startAndEndTime) || startAndEndTime.contains("null")) startAndEndTime = "00:00-23:59";

        List<String> strings = getRegEx(startAndEndTime, "\\d+:\\d+");
        if (!isNull(strings) && strings.size() >= 2) {
            this.startTime = getRegEx(startAndEndTime, "\\d+:\\d+").get(0);
            this.endTime = getRegEx(startAndEndTime, "\\d+:\\d+").get(1);
        }

        this.confirmAction = confirmAction;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_time_picker, null);
        setContentView(view);

        getWindow().setWindowAnimations(R.style.Animation_Bottom_Rising);
        getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        initView();
        initData();
        setEvent();
    }

    private void setEvent() {
        submitBtn.setOnClickListener(v -> {
            confirmAction.onClick(startTime + " - " + endTime);
            dismiss();
        });

        this.setCanceledOnTouchOutside(true);
    }

    private void initData() {
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);
        timePickerStart.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePickerEnd.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        setTimePickerDividerColor(timePickerStart);
        setTimePickerDividerColor(timePickerEnd);


        if (!isNull(startTime) && !isNull(endTime)) {
            if(equalsToTime(startTime,endTime)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerStart.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                    timePickerStart.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                    timePickerEnd.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                    timePickerEnd.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                } else {
                    timePickerStart.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                    timePickerStart.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                    timePickerEnd.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                    timePickerEnd.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                }
                endTime = startTime;
            }else{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerStart.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                    timePickerStart.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                    timePickerEnd.setHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                    timePickerEnd.setMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                } else {
                    timePickerStart.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                    timePickerStart.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                    timePickerEnd.setCurrentHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                    timePickerEnd.setCurrentMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                }
            }

        }

        timePickerStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String h = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String m = minute < 10 ? "0" + minute : "" + minute;
                startTime = h + ":" + m;
                if(equalsToTime(startTime,endTime)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePickerEnd.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                        timePickerEnd.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                    } else {
                        timePickerEnd.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                        timePickerEnd.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                    }
                    if(startTime.equals(endTime)){
                        if(startTime.equals("23:59")){
                            startTime = "23" + ":" + "58";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                timePickerStart.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                                timePickerStart.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                            } else {
                                timePickerStart.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                                timePickerStart.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                            }
                        }else{
                            if(m.equals("59")){
                                endTime = ((hourOfDay + 1) < 10 ? "0" + (hourOfDay + 1) : "" + (hourOfDay + 1)) + ":" + "00";
                            }else{
                                endTime = h + ":" + ((minute + 1) < 10 ? "0" + (minute +1) : "" + (minute + 1));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                timePickerEnd.setMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                                timePickerEnd.setHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                            } else {
                                timePickerEnd.setCurrentMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                                timePickerEnd.setCurrentHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                            }
                        }
                    }
                }

            }

        });
        timePickerEnd.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {
            String h = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
            String m = minute < 10 ? "0" + minute : "" + minute;
            endTime = h + ":" + m;
            if(equalsToTime(startTime,endTime)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerStart.setHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                    timePickerStart.setMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                } else {
                    timePickerStart.setCurrentHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                    timePickerStart.setCurrentMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                }
                if(startTime.equals(endTime)){
                    if(endTime.equals("00:00")){
                        endTime = "00" + ":" + "01";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            timePickerEnd.setHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                            timePickerEnd.setMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                        } else {
                            timePickerEnd.setCurrentHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                            timePickerEnd.setCurrentMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
                        }
                    }else{
                            if(m.equals("00")){
                                isEndTime = true;
                                startTime = ((hourOfDay - 1) < 10 ? "0" + (hourOfDay - 1) : "" + (hourOfDay - 1)) + ":" + "59";
                            }else{
                                startTime = h + ":" + ((minute - 1) < 10 ? "0" + (minute - 1) : "" + (minute - 1));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                timePickerStart.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                                timePickerStart.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                            } else {
                                timePickerStart.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));
                                timePickerStart.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                            }
                        }
                }
            }
        });
    }

    private boolean equalsToTime(String t1,String t2){
        if(DateUtils.getSecondTime(t1) >= DateUtils.getSecondTime(t2)){
            return true;
        }else{
            return false;
        }
    }

    private void initView() {
        timePickerStart = (TimePicker) findViewById(R.id.timePickerStart);
        timePickerEnd = (TimePicker) findViewById(R.id.timePickerEnd);
        submitBtn = findViewById(R.id.btn_confirm);
    }

    public interface ConfirmAction {

        void onClick(String startAndEndTime);
    }

    private void setTimePickerDividerColor(TimePicker timePicker) {
        LinearLayout llFirst = (LinearLayout) timePicker.getChildAt(0);
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(1);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            if (mSpinners.getChildAt(i) instanceof NumberPicker) {
                Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                setPickerMargin((NumberPicker) mSpinners.getChildAt(i));
                for (Field pf : pickerFields) {
                    if (pf.getName().equals("mSelectionDivider")) {
                        pf.setAccessible(true);
                        try {
                            pf.set(mSpinners.getChildAt(i), new ColorDrawable());
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    private void setPickerMargin(NumberPicker picker) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) picker.getLayoutParams();
        p.setMargins(-getDensityValue(16, context), 0, -getDensityValue(16, context), 0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            p.setMarginStart(-getDensityValue(16, context));
            p.setMarginEnd(-getDensityValue(16, context));
        }
    }

    public static int getDensityValue(float value, Context activity) {

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

        return (int) Math.ceil(value * displayMetrics.density);
    }

    public static List<String> getRegEx(String input, String regex) {
        List<String> stringList = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        while (m.find())
            stringList.add(m.group());

        return stringList;
    }

    public static boolean isNull(String checkStr) {

        boolean result = false;

        if (null == checkStr){

            result = true;
        } else {
            if (checkStr.length() == 0) {

                result = true;
            }
        }
        return result;
    }

    public static boolean isNull(List<?> list) {

        boolean result = false;

        if (null == list){

            result = true;
        } else {
            if (list.size() == 0) {

                result = true;
            }
        }
        return result;
    }
}
