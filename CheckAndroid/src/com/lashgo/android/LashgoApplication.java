package com.lashgo.android;


import android.app.Application;
import android.os.StrictMode;
import dagger.ObjectGraph;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public class LashgoApplication extends Application {

    private ObjectGraph graph;

    private static LashgoApplication instance;

    public static LashgoApplication getInstance() {
        return instance;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isWasSent() {
        return wasSent;
    }

    public void setWasSent(boolean wasSent) {
        this.wasSent = wasSent;
    }

    //TODO hack
    private String imgPath;

    private boolean wasSent;

    protected List<Object> getModules() {
        return Arrays.<Object>asList(
                new LashgoModule(this));
    }

    public void inject(Object object) {
        graph.inject(object);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        }
        graph = ObjectGraph.create(getModules().toArray());
    }

    public ObjectGraph getApplicationGraph() {
        return graph;
    }

    public void clearHackData() {
        imgPath = null;
        wasSent = false;
    }
}
