package com.kaadas.lock.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.kaadas.lock.R;

import java.io.UnsupportedEncodingException;


public class EditTextWatcher implements TextWatcher {

    private Context context;

    private EditText editText;

    private int len; //允许输入的字节长度(一个中文占3个字节)

    private int chinaLen;

    private TextWatcher watcher;


    public EditTextWatcher(Context context, TextWatcher watcher,EditText editText, int len) {
        this.context = context;
        this.editText = editText;
        this.watcher = watcher;
        this.len = len > 0 ? len : 0;
        this.chinaLen=len/3;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s != null) {
            String tmp = s.toString();
            try {
                if (count > 0 && len > 0) {

                    int cnt = count;
                    do {
                        tmp = s.toString().substring(0, start + cnt) + s.toString().substring(start + count);
                        byte[] bytes = tmp.getBytes("utf-8");
                        if (bytes.length <=len) {
                            break;
                        }else{

                            String message = String.format(context.getString(R.string.input_name_max), len+"",chinaLen+"");
                            ToastUtil.getInstance().showShort(message);
                        }

                    } while (cnt-- > 0);
                    if (!tmp.equals(s.toString())) {
                        editText.setText(tmp);
                        editText.setSelection(start + cnt);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (watcher != null) {
            watcher.onTextChanged(s, start, before, count);
        }
    }



    @Override
    public void afterTextChanged(Editable editable) {
        if (watcher != null) {
            watcher.afterTextChanged(editable);
        }

        }
    }

