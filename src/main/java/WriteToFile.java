import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//Jack Makrides
//mib18163


public class WriteToFile {

    /*
    Creates a batch number file in the system to store all batch numbers from created batches. Calls a method to add an
    empty array if the file is empty
     */
    public static void createBatchNumberFile() throws IOException {
        File batchnumbers = new File("files/batchnumbers/batchnumbers.json");

        batchnumbers.createNewFile();

        addBatchNumberArrayToFileIfEmpty();
    }

    /*
    Creates a component number file in the system to store all component serial numbers from created components. Calls a method to add an
    empty array if the file is empty
     */
    public static void createComponentNumberFile() throws IOException {
        File componentnumbers = new File("files/componentnumbers/componentnumbers.json");

        componentnumbers.createNewFile();

        addComponentNumberArrayToFileIfEmpty();
    }

    /*
    Adds an empty JSONArray to the batch number file if it is empty
     */
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

    /*
    Adds an empty JSONArray to the component number file if it is empty
     */
    public static void addComponentNumberArrayToFileIfEmpty() {

        File componentNumberFile = new File("files/componentnumbers/componentnumbers.json");

        if(componentNumberFile.length() == 0) {
            JSONObject componentNumbers = new JSONObject();

            JSONArray componentNumberList = new JSONArray();

            componentNumbers.put("componentNumbers", componentNumberList);

            try (FileWriter file = new FileWriter("files/componentnumbers/componentnumbers.json")) {
                file.write((componentNumbers.toJSONString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Appends a new batch number to the list of batch numbers present in the batchnumbers file
     */
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

    /*
    Appends a new component serial number to the list of component serial numbers present in the componentnumbers file
     */
    public static void writeComponentNumberToFile(String batchNo) throws IOException {
        JSONObject componentNumbers = new JSONObject();

        JSONArray componentNumberList = ReadFromFile.readComponentNumbers();

        componentNumberList.add(batchNo);
        componentNumbers.put("componentNumbers", componentNumberList);

        try (FileWriter file = new FileWriter("files/componentnumbers/componentnumbers.json")) {
            file.write((componentNumbers.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Creates a new file with the name being the batch number of the batch to be written to file. Then the details of the
    batch are inserted into this file.
     */
    public static void writeBatchToFile(String batchNumber, String manufactureDate, int noOfComponents,
                                        String componentType, String sizeFitmentType, String location,
                                        ArrayList<String> componentSerialNumbers, ArrayList<String> componentStatus) throws IOException {
        File batches = new File("files/batches/" + batchNumber + ".json");

        batches.createNewFile();

        JSONObject batch = new JSONObject();
        batch.put("batchNumber", batchNumber);
        batch.put("manufactureDate", manufactureDate);
        batch.put("componentType", componentType);
        batch.put("sizeFitmentType", sizeFitmentType);
        batch.put("noOfComponents", noOfComponents);
        batch.put("location", location);
        batch.put("componentSerialNumbers", componentSerialNumbers);
        batch.put("componentStatus", componentStatus);

        try (FileWriter file = new FileWriter(batches)) {
            file.write((batch.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Creates a new file with the name being the component serial number of the component to be written to file. Then the details of the
    component are inserted into this file.
     */
    public static void writeComponentToFile (String serialNumber, String batchNumber, String componentType,
                                             String sizeFitmentType, String manufactureDate, String componentStatus, String finish, String location) throws IOException {
        File components = new File("files/components/" + serialNumber + ".json");

        components.createNewFile();

        JSONObject component = new JSONObject();
        component.put("serialNumber", serialNumber);
        component.put("batchNumber", batchNumber);
        component.put("manufactureDate", manufactureDate);
        component.put("componentType", componentType);
        component.put("sizeFitmentType", sizeFitmentType);
        component.put("componentStatus", componentStatus);
        component.put("finish", finish);
        component.put("location", location);

        try (FileWriter file = new FileWriter(components)) {
            file.write((component.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to write the updated location of a batch to the batch details file
     */
    public static void allocateStock(String batchNumberToAllocate, String chosenLocation) {
        File batches = new File("files/batches/" + batchNumberToAllocate + ".json");

        JSONObject batch = ReadFromFile.readDetailsOfABatch(batchNumberToAllocate);

        batch.replace("location", chosenLocation);

        try (FileWriter file = new FileWriter(batches)) {
            file.write((batch.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Method to assign a finish to a component. It replaces the current finish with the new finish and also updates the
    components status. It then calls another method that updates these values in the relevant batch file.
     */
    public static void assignFinishForComponent(String serialNumber, String finish) {
        File components = new File("files/components/" + serialNumber + ".json");

        JSONObject component = ReadFromFile.readDetailsOfAComponent(serialNumber);

        component.replace("finish", finish);
        component.replace("componentStatus", "Manufactured-finished");

        String batchNumber = (String) component.get("batchNumber");
        assignFinishForComponentInsideBatch(batchNumber, serialNumber);

        try (FileWriter file = new FileWriter(components)) {
            file.write((component.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Method called by assignFinishForComponent(). Used to update the component status stored within a batch details file
    with the new status for that component.
     */
    public static void assignFinishForComponentInsideBatch(String batchNumber, String serialNumber) {
        File batches = new File("files/batches/" + batchNumber + ".json");

        JSONObject batch = ReadFromFile.readDetailsOfABatch(batchNumber);
        JSONArray componentStatusList = (JSONArray) batch.get("componentStatus");
        for (int i = 0; i < componentStatusList.size(); i++) {
            String componentStatus = (String) componentStatusList.get(i);
            String currentSerialNumber = componentStatus.substring(0,15);
            if (currentSerialNumber.equals(serialNumber)) {
                String newComponentStatus = serialNumber + " Manufactured-finished";
                componentStatusList.set(i, newComponentStatus);
                break;
            }
        }
        batch.replace("componentStatus", componentStatusList);

        try (FileWriter file = new FileWriter(batches)) {
            file.write((batch.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
