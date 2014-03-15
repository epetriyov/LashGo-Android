package com.check.android;


import android.os.Build;
import android.os.StrictMode;
import com.check.android.service.CheckErrorHandler;
import com.check.android.service.CheckInterceptor;
import com.check.android.service.JacksonConverter;
import com.check.android.service.RestService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.ObjectGraph;
import org.holoeverywhere.app.Application;
import retrofit.RestAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public class CheckApplication extends Application {

    private ObjectGraph graph;

    private static CheckApplication instance;

    public static CheckApplication getInstance() {
        return instance;
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(
                new CheckModule(this));
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
