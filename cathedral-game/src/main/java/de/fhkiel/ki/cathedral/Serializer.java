package de.fhkiel.ki.cathedral;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Serializer {

    public void serializeFirstResult(Learning matchResult) {
        try {
            FileWriter writer = new FileWriter("user.json");
            new Gson().toJson(matchResult, writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addResult(Learning matchResult) {
        List<Learning> matchResults = deserialize();
        matchResults.add(matchResult);

        try {
            FileWriter writer = new FileWriter("user.json");
            new Gson().toJson(matchResults, writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Learning> deserialize() {

        char[] array = new char[10000];
        try {
            FileReader input = new FileReader("user.json");
            input.read(array);

            input.close();
        } catch (Exception e) {
            e.getStackTrace();
        }

        String resultsString = new String(array);

        Gson gson = new Gson();
        List<Learning> results = gson.fromJson(resultsString, ArrayList.class);

        return results;
    }

}
