package com.wsec.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
    private static final SharedPrefUtil sharedPrefUtil = new SharedPrefUtil();
    private static final String PREF_TRUSTED_CONTACT = "TRUSTED_CONTACT_PREFERENCE";
    private static final String TRUSTED_CONTACT = "TRUSTED_CONTACT";
    private static final String IS_FIRST_RUN = "isFirstRun";
    public static SharedPrefUtil getInstance() {
        return sharedPrefUtil;
    }

    private SharedPrefUtil() {
    }

    public void setTrustedContactsJSON(Context context,String json)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_TRUSTED_CONTACT, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TRUSTED_CONTACT, json);
        editor.apply();
    }

    public String getTrustedContactsJSON(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_TRUSTED_CONTACT, 0);
        return pref.getString(TRUSTED_CONTACT,"");
    }

    public boolean getIsFirstRun(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(PREF_TRUSTED_CONTACT, 0);

        boolean isFirstRun = pref.getBoolean(IS_FIRST_RUN, true);
        if (isFirstRun) {
            pref.edit().putBoolean(IS_FIRST_RUN, false).apply();
        }

        return isFirstRun;
    }
}
