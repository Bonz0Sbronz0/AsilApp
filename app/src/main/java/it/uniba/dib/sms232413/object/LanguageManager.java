package it.uniba.dib.sms232413.object;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class LanguageManager {

    private static final String PREF_LANGUAGE = "language";

    public static void setLanguage(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_LANGUAGE, language);
        editor.apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return preferences.getString(PREF_LANGUAGE, Locale.getDefault().getLanguage());
    }

    public static void applyLanguage(Context context) {
        String language = getLanguage(context);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
