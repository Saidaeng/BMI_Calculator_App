package com.example.bmi;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private DecimalFormat formatter;  // ประกาศตัวแปร DecimalFormat ที่นี่

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        final TextView output_bmi = findViewById(R.id.TextView_bmi);
        final TextView output_wc = findViewById(R.id.TextView_wc);
        final EditText input_h = findViewById(R.id.editText_h);
        final EditText input_w = findViewById(R.id.editText_w);
        final Button calculate = findViewById(R.id.button_calculate);

        // ตั้งค่าฟิลเตอร์สำหรับ EditText เพื่อจำกัดการรับค่าตัวเลข
        input_h.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        input_w.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        // ดึงรูปแบบการจัดการตัวเลขจาก strings.xml
        String decimalFormatPattern = getResources().getString(R.string.decimal_format);
        formatter = new DecimalFormat(decimalFormatPattern);  // กำหนดรูปแบบ DecimalFormat


        // ตั้งค่าให้ปุ่มคำนวณทำงานเมื่อคลิก
        calculate.setOnClickListener(v -> calculateBMI(input_h, input_w, output_bmi, output_wc));
    }

    // ฟังก์ชันสำหรับคำนวณ BMI
    private void calculateBMI(EditText input_h, EditText input_w, TextView output_bmi, TextView output_wc) {
        String hStr = input_h.getText().toString();
        String wStr = input_w.getText().toString();

        // ตรวจสอบว่าผู้ใช้กรอกข้อมูลครบหรือไม่
        if (!hStr.isEmpty() && !wStr.isEmpty()) {
            try {
                double h = Double.parseDouble(hStr) / 100; // แปลงส่วนสูงเป็นเมตร
                double w = Double.parseDouble(wStr);

                // คำนวณ BMI
                double bmi = w / (h * h);

                // จัดรูปแบบการแสดงผลของ BMI โดยใช้ formatter ที่ดึงจาก strings.xml
                String bmiResult = formatter.format(bmi);
                output_bmi.setText(bmiResult);

                // ดึงค่า BMI thresholds จาก strings.xml
                double severeThinness = Double.parseDouble(getResources().getString(R.string.bmi_severe_thinness));
                double moderateThinness = Double.parseDouble(getResources().getString(R.string.bmi_moderate_thinness));
                double mildThinness = Double.parseDouble(getResources().getString(R.string.bmi_mild_thinness));
                double normal = Double.parseDouble(getResources().getString(R.string.bmi_normal));
                double overweight = Double.parseDouble(getResources().getString(R.string.bmi_overweight));
                double obeseI = Double.parseDouble(getResources().getString(R.string.bmi_obese_i));
                double obeseII = Double.parseDouble(getResources().getString(R.string.bmi_obese_ii));

                // ตรวจสอบและแสดงผลตามเกณฑ์ BMI
                if (bmi < severeThinness) {
                    output_wc.setText(R.string.severe_thinness);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                } else if (bmi >= severeThinness && bmi < moderateThinness) {
                    output_wc.setText(R.string.moderate_thinness);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
                } else if (bmi >= moderateThinness && bmi < mildThinness) {
                    output_wc.setText(R.string.mild_thinness);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                } else if (bmi >= mildThinness && bmi < normal) {
                    output_wc.setText(R.string.normal);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                } else if (bmi >= normal && bmi < overweight) {
                    output_wc.setText(R.string.overweight);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                } else if (bmi >= overweight && bmi < obeseI) {
                    output_wc.setText(R.string.obese_i);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
                } else if (bmi >= obeseI && bmi < obeseII) {
                    output_wc.setText(R.string.obese_ii);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                } else if (bmi >= obeseII) {
                    output_wc.setText(R.string.obese_iii);
                    output_wc.setBackgroundColor(ContextCompat.getColor(this, R.color.r_black));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.input_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            // แสดงข้อความเตือนให้กรอกข้อมูลให้ครบ
            Toast.makeText(this, R.string.input_complete, Toast.LENGTH_SHORT).show();
        }
    }

    // ฟิลเตอร์สำหรับการจำกัดจำนวนตัวเลขใน EditText
    static class DecimalDigitsInputFilter implements InputFilter {
        private final Pattern mPattern;

        DecimalDigitsInputFilter(int digits, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digits - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)|(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return ""; // หากไม่ตรงตามรูปแบบให้ไม่อนุญาตให้กรอก
            return null; // อนุญาตให้กรอก
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setFontScale(newConfig.fontScale);
    }

    private void setFontScale(float fontScale) {
        float scaledSize = fontScale * 16; // ตัวอย่างการคูณเพื่อปรับขนาด
        ((TextView) findViewById(R.id.TextView_bmi)).setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
        ((TextView) findViewById(R.id.TextView_wc)).setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
        ((EditText) findViewById(R.id.editText_h)).setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
        ((EditText) findViewById(R.id.editText_w)).setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
        ((Button) findViewById(R.id.button_calculate)).setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
    }







}