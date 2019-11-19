package com.freak.marqueeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.freak.marquee.MarqueeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private MarqueeView marquee;
private List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marquee=findViewById(R.id.marquee);
        list=new ArrayList<>();
        list.add("测试1");
        list.add("测试2");
        list.add("测试3");
        list.add("测试4");
        list.add("测试5");
        list.add("测试6");
        marquee.startWithList(list);
    }
}
