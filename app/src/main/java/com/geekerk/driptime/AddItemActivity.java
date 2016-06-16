package com.geekerk.driptime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/6/16.
 */
public class AddItemActivity extends AppCompatActivity {
    private ImageView priorityCommon, prioritySecond, priorityFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);
        final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.content_inputlayout);
        EditText editText = textInputLayout.getEditText();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    textInputLayout.setError("Content can not be empty");
                    textInputLayout.setErrorEnabled(true);
                } else {
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_action :   //取消
                break;
            case R.id.done_action :     //完成
                break;
            case R.id.priority_level_0 :    //设置普通优先级
                //设置优先级
                //更新页面
                break;
            case R.id.priority_level_2 :    //设置2级优先级
                break;
            case R.id.priority_level_1 :    //设置1级优先级
                break;
            case R.id.startTime_bt :    //设置开始时间
                break;
            case R.id.deadline_bt :     //设置截止时间
                break;
        }
    }
}
