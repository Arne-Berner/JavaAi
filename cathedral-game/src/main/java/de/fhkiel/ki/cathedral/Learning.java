package de.fhkiel.ki.cathedral;

import java.util.List;

class JSON {

    public static final void main(String args[]) {
        try {
            // Fill data, you know, whatever
            MatchResult stat1 = new MatchResult();
            stat1.setFieldId(1, 5);
            stat1.setNumberOfWins(2);
            stat1.setNumberOfGames(200);
            stat1.setScoreDifference(20, 10);

            MatchResult stat2 = new MatchResult();
            stat2.setFieldId(1, 5);
            stat2.setNumberOfWins(2);
            stat2.setNumberOfGames(200);
            stat2.setScoreDifference(10, 20);

            Serializer.serializeFirstResult(stat1);
            System.out.println("It begins here!");
            List<MatchResult> test = Serializer.deserialize();
            System.out.println(test.size());

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
