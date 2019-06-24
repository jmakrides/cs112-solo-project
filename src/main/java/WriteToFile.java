import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WriteToFile {

    public static void createBatchNumberFile() throws IOException {
        File batchnumbers = new File("files/batchnumbers/batchnumbers.json");

        batchnumbers.createNewFile();

        addBatchNumberArrayToFileIfEmpty();
    }

    public static void addBatchNumberArrayToFileIfEmpty() {

        File batchNumberFile = new File("files/batchnumbers/batchnumbers.json");

        if(batchNumberFile.length() == 0) {
            JSONObject batchNumbers = new JSONObject();

            JSONArray batchNumberList = new JSONArray();

            batchNumbers.put("batchNumbers", batchNumberList);

            try (FileWriter file = new FileWriter("files/batchnumbers/batchnumbers.json")) {
                file.write((batchNumbers.toJSONString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeBatchNumberToFile(String batchNo) throws IOException {
        JSONObject batchNumbers = new JSONObject();

        JSONArray batchNumberList = ReadFromFile.readBatchNumbers();

        batchNumberList.add(batchNo);
        batchNumbers.put("batchNumbers", batchNumberList);

        try (FileWriter file = new FileWriter("files/batchnumbers/batchnumbers.json")) {
            file.write((batchNumbers.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBatchToFile(String batchNumber, int noOfComponents, String componentType, String sizeFitmentType, ArrayList<String> componentSerialNumbers) throws IOException {
        File batchnumbers = new File("files/batches/" + batchNumber + ".json");

        batchnumbers.createNewFile();

        JSONObject batch = new JSONObject();
        batch.put("batchNumber", batchNumber);
        batch.put("noOfComponents", noOfComponents);
        batch.put("componentType", componentType);
        batch.put("sizeFitmentType", sizeFitmentType);
        batch.put("componentSerialNumbers", componentSerialNumbers);

        try (FileWriter file = new FileWriter(batchnumbers)) {
            file.write((batch.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
