package com.codepath.nytimessearch.utils;

import java.io.IOException;

/**
 * Created by santoshag on 7/30/16.
 */
public class Utilities {
    public static String FILTER_PREFERENCES = "filter_search_settings";
    public static String SORT_BY_OLDEST = "oldest";
    public static String SORT_BY_NEWEST = "newest";

    public static boolean checkForInternet() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

}
