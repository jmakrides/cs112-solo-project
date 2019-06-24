import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class ReadFromFile {

    public static JSONArray readBatchNumbers() {
        JSONArray batchNumberList = new JSONArray();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("files/batchnumbers/batchnumbers.json")) {

            JSONObject batchNumbers = (JSONObject) parser.parse(reader);

            batchNumberList = (JSONArray) batchNumbers.get("batchNumbers");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return batchNumberList;
    }
}
