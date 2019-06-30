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

    public static JSONArray readAllBatches() {
        JSONArray batches = new JSONArray();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("files/batches")){


            JSONObject batch = (JSONObject) parser.parse(reader);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  batches;
    }

    public static JSONObject readDetailsOfABatch(String batchNumber) {
        JSONObject batch = new JSONObject();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("files/batches/" + batchNumber + ".json")) {
            batch = (JSONObject) parser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return batch;
    }

    public static JSONArray readComponentNumbers() {
        JSONArray componentNumberList = new JSONArray();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("files/componentnumbers/componentnumbers.json")) {

            JSONObject componentNumbers = (JSONObject) parser.parse(reader);

            componentNumberList = (JSONArray) componentNumbers.get("componentNumbers");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return componentNumberList;
    }

    public static JSONObject readDetailsOfAComponent(String componentNumber) {
        JSONObject component = new JSONObject();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("files/components/" + componentNumber + ".json")) {
            component = (JSONObject) parser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return component;
    }
}
