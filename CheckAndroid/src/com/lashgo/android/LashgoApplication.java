package com.lashgo.android;


import android.os.Build;
import android.os.StrictMode;
import dagger.ObjectGraph;
import org.holoeverywhere.app.Application;

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
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 9) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        }
        graph = ObjectGraph.create(getModules().toArray());
    }

    public ObjectGraph getApplicationGraph() {
        return graph;
    }
}