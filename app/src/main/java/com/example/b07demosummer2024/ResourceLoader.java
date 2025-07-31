package com.example.b07demosummer2024;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class ResourceLoader {
    public static HashMap<String, List<Resource>> loadResources(Context context) {
        try {
            InputStream is = context.getAssets().open("support_resources.json");
            InputStreamReader reader = new InputStreamReader(is);
            Type type = new TypeToken<HashMap<String, List<Resource>>>(){}.getType();
            return new Gson().fromJson(reader, type);
        } catch (Exception e) {
            return null;
        }
    }
}

