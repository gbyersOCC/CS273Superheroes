package com.example.longtruong.cs273.ltruong58.cs273superheroes;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Class loads MusicEvent data from a formatted JSON (JavaScript Object Notation) file.
 * Populates data model (MusicEvent) with data.
 */

public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */
    public static ArrayList<Superhero> loadJSONFromAsset(Context context) throws IOException {
        ArrayList<Superhero> allMusicEvents = new ArrayList<>();
        String json = null;
            InputStream is = context.getAssets().open("cs273superheroes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperheroesJSON = jsonRootObject.getJSONArray("CS273Superheroes");
            int numberOfEvents = allSuperheroesJSON.length();

            for (int i = 0; i < numberOfEvents; i++) {
                JSONObject musicEventJSON = allSuperheroesJSON.getJSONObject(i);

                Superhero event = new Superhero();
                event.setName(musicEventJSON.getString("Name"));

                event.setUsername(musicEventJSON.getString("Username"));
                event.setOneThing(musicEventJSON.getString("OneThing"));
                event.setSuperpower(musicEventJSON.getString("Superpower"));

                event.setImageName(musicEventJSON.getString("ImageName"));

                allMusicEvents.add(event);
             }
        }
        catch (JSONException e)
        {
            Log.e("Superheroes", e.getMessage());
        }

        return allMusicEvents;
    }
}
