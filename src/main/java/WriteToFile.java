import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WriteToFile {

    public static void createBatchNumberFile() throws IOException {
        File batchnumbers = new File("files/batchnumbers/batchnumbers.txt");

        batchnumbers.createNewFile();

        addBatchNumberArrayToFileIfEmpty();
    }

    public static void addBatchNumberArrayToFileIfEmpty() {

        File batchNumberFile = new File("files/batchnumbers/batchnumbers.txt");

        if(batchNumberFile.length() == 0) {
            JSONObject batchNumbers = new JSONObject();

            JSONArray batchNumberList = new JSONArray();

            batchNumbers.put("batchNumbers", batchNumberList);

            try (FileWriter file = new FileWriter("files/batchnumbers/batchnumbers.txt")) {
                file.write((batchNumbers.toJSONString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeBatchNumberToFile(String batchNo) throws IOException {
        JSONObject batchNumbers = new JSONObject();

        JSONArray batchNumberList = ReadFromFile.readBatchNumbers();

        JSONArray batchNumberListForGivenDate = new JSONArray();
        batchNumberList.add(batchNo);
        batchNumbers.put("batchNumbers", batchNumberList);

        try (FileWriter file = new FileWriter("files/batchnumbers/batchnumbers.txt")) {
            file.write((batchNumbers.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
