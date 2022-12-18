package de.fhkiel.ki.cathedral;

import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

public class Serializer {

    public static void serializeFirstResult(MatchResult matchResult) {
        try {
            FileWriter writer = new FileWriter("user.json");

            Type listType = new TypeToken<List<MatchResult>>() {
            }.getType();

            List<MatchResult> matchResults = new ArrayList<MatchResult>();
            matchResults.add(matchResult);
            Gson gson = new Gson();
            gson.toJson(matchResults, listType, writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void addResult(MatchResult matchResult) {
        List<MatchResult> matchResults = deserialize();
        matchResults.add(matchResult);

        try {
            FileWriter writer = new FileWriter("user.json");
            Type listType = new TypeToken<List<MatchResult>>() {
            }.getType();
            new Gson().toJson(matchResults, listType, writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<MatchResult> deserialize() {

        List<MatchResult> results = new ArrayList<MatchResult>();

        try {
            Reader reader = Files.newBufferedReader(Paths.get("user.json"));

            Gson gson = new Gson();
            Type listType = new TypeToken<List<MatchResult>>() {
            }.getType();
            results = gson.fromJson(reader, listType);

            // Gson gson = new GsonBuilder().setLenient().create();
        } catch (Exception e) {
            e.getStackTrace();
        }

        return results;
    }

}
