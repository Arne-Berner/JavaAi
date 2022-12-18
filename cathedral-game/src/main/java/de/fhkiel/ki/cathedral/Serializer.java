package de.fhkiel.ki.cathedral;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {

    public static void serializeFirstResult(MatchResult matchResult) {
        try {
            FileWriter writer = new FileWriter("user.json");

            Type listType = new TypeToken<Learning>() {
            }.getType();

            Learning matchResults = new Learning();
            matchResults.addResult(matchResult);
            Gson gson = new Gson();
            gson.toJson(matchResults, listType, writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void addResult(MatchResult matchResult) {
        Learning matchResults = deserialize();
        matchResults.addResult(matchResult);

        try {
            FileWriter writer = new FileWriter("user.json");
            new Gson().toJson(matchResults, writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Learning deserializePretty() {
        // maybe use JsonObjects, with jsonelements

        char[] array = new char[10000];
        try {
            FileReader input = new FileReader("/home/arne/Vscode/KI/user.json");
            input.read(array);

            input.close();
        } catch (Exception e) {
            e.getStackTrace();
        }

        String resultsString = new String(array);

        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        // Type listType = new TypeToken<Learning>() {
        // }.getType();
        Learning results = gson.fromJson(resultsString, Learning.class);

        return results;
    }

    public static Learning deserialize() {

        Learning results = new Learning();

        try {
            Reader reader = Files.newBufferedReader(Paths.get("user.json"));

            Gson gson = new Gson();
            results = gson.fromJson(reader, Learning.class);

            // Gson gson = new GsonBuilder().setLenient().create();
        } catch (Exception e) {
            e.getStackTrace();
        }

        return results;
    }

}
