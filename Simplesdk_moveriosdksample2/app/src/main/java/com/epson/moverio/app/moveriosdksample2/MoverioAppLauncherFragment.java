package com.epson.moverio.app.moveriosdksample2;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MoverioAppLauncherFragment extends androidx.fragment.app.Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;
    private final String[] DEPENDENT_FEATURE_LIST = {
            PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContext = getContext();

        return inflater.inflate(R.layout.fragment_app_launcher, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AppListAdapter adapter = getAppListAdapter();

        ListView listView = (ListView)view.findViewById(R.id.listview_app);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(!checkDependentFeatures()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Launch Failed...")
                            .setMessage("It cannot be executed on this host device.")
                            .setPositiveButton("OK", null)
                            .show();
                    return ;
                }
                else {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItemId(i) == id) {
                            adapter.getItem(i).setChecked(true);
                            setupThing(adapter.getItem(i));
                        } else {
                            adapter.getItem(i).setChecked(false);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }

        });
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

    @TargetApi(Build.VERSION_CODES.O)
    private void setupThing(AppData appData) {
        MediaRouter mediaRouter = (MediaRouter) mContext.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        if (mediaRouter != null) {
            MediaRouter.RouteInfo routeInfo = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
            if (routeInfo != null) {
                Display presentationDisplay = routeInfo.getPresentationDisplay();
                Bundle bundle = null;
                bundle = ActivityOptions.makeBasic().setLaunchDisplayId(presentationDisplay.getDisplayId()).toBundle();
                Intent intent = new Intent();
                intent.setClassName(appData.getPackageName(), appData.getClassName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent, bundle);
            }
        }
    }
    private List<ResolveInfo> getAppInfoList(){
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return packageManager.queryIntentActivities(intent, 0);
    }
    private AppListAdapter getAppListAdapter(){
        List<ResolveInfo> appInfoList = getAppInfoList();
        final AppListAdapter adapter = new AppListAdapter(mContext, 0, new ArrayList<AppData>());
        for(ResolveInfo app : appInfoList) {
            AppData appData = new AppData();
            appData.setIcon(GraphicUtil.getBitmap(app.loadIcon(mContext.getPackageManager())));
            appData.setLabel(app.loadLabel(mContext.getPackageManager()).toString());
            appData.setChecked(false);
            appData.setPackageName(app.activityInfo.packageName);
            appData.setClassName(app.activityInfo.name);
            adapter.add(appData);
        }
        AppData appData = new AppData();
        appData.setIcon(null);
        appData.setLabel("none");
        appData.setChecked(true);
        appData.setPackageName("");
        appData.setClassName("");
        adapter.add(appData);

        return adapter;
    }
    private boolean checkDependentFeatures(){
        boolean ret = true;
        PackageManager packageManager = mContext.getPackageManager();
        for(String feature : DEPENDENT_FEATURE_LIST){
            if(!packageManager.hasSystemFeature(feature)) {
                ret = false;
                break;
            }
            else ;
        }
        return ret;
    }
}
