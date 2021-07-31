package com.github.skiiyis.hotfix;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.skiiyis.study.hotfix.R;

public class MainActivity extends Activity {

    // public static HotFixProxy __change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPatch();
        ((TextView) findViewById(R.id.text)).setText(problemMethod());
        ((TextView) findViewById(R.id.text)).setOnClickListener(v -> ((TextView) findViewById(R.id.text)).setText(problemMethod()));
    }

    private void checkPatch() {
        HotFixCheckUtil.checkAndHotFix(this);
    }

    public String noProblemMethod() {
        return "I'm no problem method!!";
    }

    private String problemMethod() {
        return "I'm problem method!!";
    }
}
