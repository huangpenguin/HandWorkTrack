package com.epson.moverio.app.moveriosdksample2;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

public class MoverioDemoPresentation extends Presentation {

    private TextView mTextView_subtitle = null;
    public MoverioDemoPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_subtitle);
        mTextView_subtitle = (TextView) findViewById(R.id.textView_subtitle_demo);
    }

    public void setTextSize(float size){
        mTextView_subtitle.setTextSize(size);
    }
}
