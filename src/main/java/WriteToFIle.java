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

public class WriteToFIle {

    public static void createBatchNumberFile() throws IOException {
        File batchnumbers = new File("files/batchnumbers/batchnumbers.txt");

        batchnumbers.createNewFile();


    }

    public static void writeBatchNumberToFile(String batchNo) throws IOException {
        JSONObject batch = new JSONObject();
        batch.put("Batch Number", batchNo);

        try (FileWriter file = new FileWriter("files/batchnumbers/batchnumbers.txt")) {
            file.write(batch.toJSONString());
        }

        try {
            final Path path = Paths.get("files/batchnumbers/batchnumbers.txt");
            Files.write(path, batch.toJSONString().getBytes(), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe) {
            // Add your own exception handling...
        }
    }
}
