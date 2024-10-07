package com.epson.moverio.app.moveriosdksample2;

import android.graphics.Bitmap;

public class AppData {
    private Bitmap icon = null;
    private String label = null;
    private boolean isChecked = false;
    private String mPackageName = "";
    private String mClassName = "";

    public Bitmap getIcon(){
        return icon;
    }
    public String getLabel(){
        return label;
    }
    public boolean isChecked(){
        return isChecked;
    }
    public void setIcon(Bitmap bitmap){
        this.icon = bitmap;
    }
    public void setLabel(String label){
        this.label = label;
    }
    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }
    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }
    public String getPackageName() {
        return mPackageName;
    }
    public void setClassName(String className) {
        mClassName = className;
    }
    public String getClassName() {
        return mClassName;
    }
}