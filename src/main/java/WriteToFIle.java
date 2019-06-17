import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WriteToFIle {

    public static void writeBatchNumberToFile(String batchNo) throws IOException {
        JSONObject batch = new JSONObject();
        batch.put("Batch Number", batchNo);

        try (FileWriter file = new FileWriter("files/batchnumbers/batchnumbers.txt")) {
            file.write(batch.toJSONString());
        }
    }
}
