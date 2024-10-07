package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epson.moverio.util.Property;

public class MoverioSdkInfoSampleFragment extends androidx.fragment.app.Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;
    private Property.BasicFunctionSdk mSdkInfo = null;

    private TextView mTextView_sdkName = null;
    private TextView mTextView_sdkVersion = null;
    private TextView mTextView_supportProduct = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContext = getContext();
        mSdkInfo = new Property.BasicFunctionSdk(mContext);

        return inflater.inflate(R.layout.fragment_sdk_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView_sdkName = (TextView) view.findViewById(R.id.textView_sdkName);
        mTextView_sdkName.setText(mSdkInfo.getSdkName());
        mTextView_sdkVersion = (TextView) view.findViewById(R.id.textView_sdkVersion);
        mTextView_sdkVersion.setText(mSdkInfo.getSdkVersion());
        mTextView_supportProduct = (TextView) view.findViewById(R.id.textView_supportProduct);
        String str = "";
        for (String product : mSdkInfo.getSupportedProductList()) str += product + "\n";
        mTextView_supportProduct.setText(str);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}