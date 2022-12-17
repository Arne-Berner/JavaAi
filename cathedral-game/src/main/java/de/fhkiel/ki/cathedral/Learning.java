package de.fhkiel.ki.cathedral;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Learning {
    private int[] fieldId = new int[2];
    private int numberOfWins;
    private int numberOfGames;
    private int scoreDifference;

    /**
     * @return the fieldId
     */
    public int[] getFieldId() {
        return fieldId;
    }

    /**
     * @param fieldId the fieldId to set
     */
    public void setFieldId(int x, int y) {
        this.fieldId[0] = x;
        this.fieldId[1] = y;
    }

    /**
     * @return the value
     */
    public int getNumberOfWins() {
        return numberOfWins;
    }

    /**
     * @param value the value to set
     */
    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }

    /**
     * @return the value
     */
    public int getNumberOfGames() {
        return numberOfGames;
    }

    /**
     * @param value the value to set
     */
    public void setNumberOfGames(int numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    /**
     * @return the value
     *         higher score is better for white
     */
    public int getScoreDifference() {
        return scoreDifference;
    }

    /**
     * @param value the value to set
     */
    public void setScoreDifference(int blackScore, int whiteScore) {
        this.scoreDifference = blackScore - whiteScore;
    }

}

class JSON {

    public static final void main(String args[]) {
        try {
            List<Learning> stats = new ArrayList<Learning>();
            // Fill data, you know, whatever
            Learning stat1 = new Learning();
            stat1.setFieldId(1, 5);
            stat1.setNumberOfWins(2);
            stat1.setNumberOfGames(200);
            stat1.setScoreDifference(20, 10);

            Learning stat2 = new Learning();
            stat2.setFieldId(1, 5);
            stat2.setNumberOfWins(2);
            stat2.setNumberOfGames(200);
            stat2.setScoreDifference(10, 20);

            stats.add(stat1);
            stats.add(stat2);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(stats));
            // create a writer
            FileWriter writer = new FileWriter("user.json");

            // convert map to JSON File
            new Gson().toJson(stats, writer);

            // close the writer
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//     public static void appendToJson(Learning learning){
//                 String json = "{\"results\":[{\"lat\":\"value\",\"lon\":\"value\" }, { \"lat\":\"value\", \"lon\":\"value\"}]}";
//         Gson gson = new Gson();
//         JsonObject inputObj  = gson.fromJson(json, JsonObject.class);
//         JsonObject newObject = new JsonObject() ;
//         newObject.addProperty("lat", "newValue");
//         newObject.addProperty("lon", "newValue");
//         inputObj.get("results").getAsJsonArray().add(newObject);
//         System.out.println(inputObj);

// JSONParser parser = new JSONParser();
//       try {
//          Object obj = parser.parse(new FileReader("/Users/User/Desktop/course.json"));
//          JSONObject jsonObject = (JSONObject)obj;
//          String name = (String)jsonObject.get("Name");
//          String course = (String)jsonObject.get("Course");
//          JSONArray subjects = (JSONArray)jsonObject.get("Subjects");
//          System.out.println("Name: " + name);
//          System.out.println("Course: " + course);
//          System.out.println("Subjects:");
//          Iterator iterator = subjects.iterator();
//          while (iterator.hasNext()) {
//             System.out.println(iterator.next());
//          }
//       } catch(Exception e) {
//          e.printStackTrace();
//       }
//     }

}
