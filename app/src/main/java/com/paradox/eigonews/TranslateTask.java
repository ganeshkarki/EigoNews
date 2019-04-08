package com.paradox.eigonews;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.ArrayList;

public abstract class TranslateTask extends AsyncTask<String, Integer, ArrayList<String>>
        implements CallbackReceiver {
    Activity activity;
    Runnable callback;


    public TranslateTask(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected ArrayList<String> doInBackground(String... text) {
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        ArrayList<String> translatedText = new ArrayList<String>();

        for (String t: text) {
            Translation translation =
                    translate.translate(
                            t,
                            Translate.TranslateOption.sourceLanguage("jp"),
                            Translate.TranslateOption.targetLanguage("en"));
            translatedText.add(translation.getTranslatedText());
        }

        return translatedText;
    }

    public abstract void receiveData(Object object);

    // Todo: can use preexecute to show progress bar
}
