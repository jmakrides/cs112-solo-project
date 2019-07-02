import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class FactorySystem {

    private Scanner reader;
    private boolean finished = false;
    private String batchNumber = "";
    private int noOfComponents = 0;
    private String componentType = "";
    private String sizeFitmentType = "";
    private String location = "";
    private ArrayList<String> componentSerialNumbers = new ArrayList<>();
    private ArrayList<String> componentStatusList = new ArrayList<>();

    public FactorySystem() {

    }

    public void start() throws IOException {
        WriteToFile.createBatchNumberFile();
        WriteToFile.createComponentNumberFile();
        printWelcome();
        printMenu();
    }

    private void printWelcome() {
        System.out.println("Welcome to the GPEC inventory system.");
    }

    private void printMenu() throws IOException {
        System.out.println("Please choose one of the following options:");
        System.out.println("1. Create a new batch");
        System.out.println("2. List all batches");
        System.out.println("3. View details of a batch");
        System.out.println("4. View details of a component");
        System.out.println("5. Allocate manufactured stock");
        System.out.println("6. Search by product type");
        System.out.println("7. Finish a component");
        System.out.println("8. Quit");

        System.out.println("> ");

        do {
            reader = new Scanner(System.in);
            String input = reader.nextLine();

            switch (input) {
                case "1":
                    getBatchDetails();
                    break;
                case "2":
                    listAllBatches();
                    break;
                case "3":
                    viewDetailsOfABatch();
                    break;
                case "4":
                    viewDetailsOfAComponent();
                    break;
                case "5":
                    allocateStock();
                    break;
                case "6":
                    searchByProduct();
                    break;
                case "7":
                    finishComponent();
                case "8":
                    quit();
                    break;
                default:
                    System.out.println("Please choose a valid option, 1 - 6");
            }
        }
        while (!finished);
    }

    private void finishComponent() throws IOException {
        boolean decisionMade = false;
        JSONArray componentNumbers = ReadFromFile.readComponentNumbers();

        System.out.println("Enter component number:");
        System.out.println("> ");

        do {
            reader = new Scanner(System.in);
            String componentNumber = reader.nextLine();

            if(componentNumbers.contains(componentNumber)) {

                boolean confirmation = false;
                JSONObject component = ReadFromFile.readDetailsOfAComponent(componentNumber);

                String currentComponentType = (String) component.get("componentType");
                String currentSizeFitmentType = (String) component.get("sizeFitmentType");
                String componentBatchNumber = (String) component.get("batchNumber");
                JSONObject batch = ReadFromFile.readDetailsOfABatch(componentBatchNumber);
                String location = (String) batch.get("location");

                System.out.println("You selected " + currentSizeFitmentType + " " + currentComponentType + " " + location + " is this correct? (Y/N)");

                do {
                    reader = new Scanner(System.in);
                    String input = reader.nextLine();

                    switch (input) {
                        case "Y":
                            String currentStatus = (String) component.get("componentStatus");
                            if(currentStatus.equals("Manufactured-unfinished")) {

                                //TODO ask if polish or paint 

                            } else {
                                System.out.println("Component already has a finish.");
                            }
                            confirmation = true;
                            printMenu();
                            break;

                        case "N":
                            confirmation = true;
                            finishComponent();
                            break;

                        default:
                            System.out.println("Please enter a valid: 'Y' or 'N'");
                            break;
                    }
                } while (!confirmation);




                decisionMade = true;
                printMenu();
            }
            else {
                System.out.println("Invalid component number.");
                printMenu();
                decisionMade = true;
            }

        } while (!decisionMade);
    }

    private void searchByProduct() throws IOException {
        componentType = null;
        sizeFitmentType = null;
        boolean decisionMade = false;
        selectComponentType();

        if(componentType.equals("Rudder Pin") || componentType.equals("Winglet Strut"))
        {
            selectSizeFitmentType();
        }

        System.out.println("You selected " + sizeFitmentType + " " + componentType + "s. Is this correct? (Y/N)");

        do {
            reader = new Scanner(System.in);
            String input = reader.nextLine();

            switch (input) {
                case "Y":
                    boolean stock = false;
                    ArrayList<JSONObject> components = ReadFromFile.readAllComponents();

                    if (!components.isEmpty()) {
                        for (JSONObject component : components) {
                            String currentComponentType = (String) component.get("componentType");
                            String currentSizeFitmentType = (String) component.get("sizeFitmentType");
                            String componentSerialNumber = (String) component.get("serialNumber");
                            String finish = (String) component.get("finish");
                            String manufactureDate = (String) component.get("manufactureDate");
                            String componentBatchNumber = (String) component.get("batchNumber");

                            JSONObject batch = ReadFromFile.readDetailsOfABatch(componentBatchNumber);
                            String location = (String) batch.get("location");

                            if(currentComponentType.equals(componentType) && currentSizeFitmentType.equals(sizeFitmentType)) {
                                stock = true;
                                System.out.println("Component Serial Number: " + componentSerialNumber);
                                System.out.println("Location: " + location);
                                System.out.println("Finish: " + finish);
                                System.out.println("Manufacture Date: " + manufactureDate);
                                System.out.println();
                                System.out.println();
                            }
                        }

                        if(!stock) {
                            System.out.println("No stock available");
                            System.out.println();
                        }
                    }
                    else {
                        System.out.println("No stock available");
                        System.out.println();
                    }

                    decisionMade = true;
                    componentType = "";
                    sizeFitmentType = "";
                    printMenu();
                    break;

                case "N":
                    decisionMade = true;
                    searchByProduct();
                    break;

                default:
                    System.out.println("Please enter a valid: 'Y' or 'N'");
                    break;
            }
        } while (!decisionMade);

    }

    private void allocateStock() throws IOException {

        String chosenLocation = "";
        JSONArray batchNumbers = ReadFromFile.readBatchNumbers();

        System.out.println("Enter batch number:");
        System.out.println("> ");

        reader = new Scanner(System.in);
        String batchNumberToAllocate = reader.nextLine();

        if(batchNumbers.contains(batchNumberToAllocate)) {
            JSONObject batch = ReadFromFile.readDetailsOfABatch(batchNumberToAllocate);
            String location = (String) batch.get("location");

            if(location.equals("Factory Floor - Warehouse Not Allocated")) {
                System.out.println("Select Warehouse (1. Glasgow, 2. Dubai)");
                System.out.println("Enter the number corresponding to your choice");
                System.out.println("> ");

                do {
                    reader = new Scanner(System.in);
                    String input = reader.nextLine();

                    switch (input) {
                        case "1":
                            chosenLocation = "Glasgow";
                            WriteToFile.allocateStock(batchNumberToAllocate, chosenLocation);
                            System.out.println("This batch is now allocated and will be shipped to the Glasgow location.");
                            System.out.println();
                            System.out.println();
                            printMenu();
                            break;
                        case "2":
                            chosenLocation = "Dubai";
                            WriteToFile.allocateStock(batchNumberToAllocate, chosenLocation);
                            System.out.println("This batch is now allocated and will be shipped to the Dubai location.");
                            System.out.println();
                            System.out.println();
                            printMenu();
                            break;
                        default:
                            System.out.println("Please enter a valid choice, 1 or 2");
                            break;
                    }
                }
                while (chosenLocation.equals(""));

            } else {
                System.out.println("Batch already allocated.");
                printMenu();
            }
        }
        else {
            System.out.println("Invalid batch number.");
            printMenu();

        }
    }


    private void getBatchDetails() throws IOException {
        generateBatchNo();
        location = "Factory Floor - Warehouse Not Allocated";
        System.out.println("Batch Number: " + batchNumber);

        selectNoComponents();

        selectComponentType();

        if(componentType.equals("Rudder Pin") || componentType.equals("Winglet Strut"))
        {
            selectSizeFitmentType();
        }

        confirmBatchDetails();

        printDetails();
    }


    private void retryBatchDetails() throws IOException {
        System.out.println("Batch Number: " + batchNumber);

        selectNoComponents();

        selectComponentType();

        if(componentType.equals("Rudder Pin")|| componentType.equals("Winglet Strut"))
        {
            selectSizeFitmentType();
        }

        confirmBatchDetails();

        printDetails();
    }

    private void generateBatchNo() {

        String dateString = getCurrentDateShortenedYear();

        JSONArray batchNumbers = ReadFromFile.readBatchNumbers();

        if (!batchNumbers.isEmpty()) {
            String latestBatchNumber = batchNumbers.get(batchNumbers.size()-1).toString();
            String latestBatchNumberDate = latestBatchNumber.substring(0,6);
            if(latestBatchNumberDate.equals(dateString)) {
                long latestBatchNumberAsLong = Long.parseLong(latestBatchNumber);
                long newBatchNumber = latestBatchNumberAsLong + 1;
                batchNumber = String.format("%010d", newBatchNumber);
            } else {
                batchNumber = dateString + "0001";
            }
        } else {
            batchNumber = dateString + "0001";
        }
    }

    private void selectNoComponents() {
        System.out.println("How many components do you want to manufacture (1 to 9999)?");
        System.out.println("> ");

        do {
            int input = 0;
            reader = new Scanner(System.in);
            if(reader.hasNextInt()) {
                input = reader.nextInt();
                if(input >=1 & input <=9999) {
                    noOfComponents = input;
                } else {
                    System.out.println("Please enter a number within the range 1 to 9999");
                }
            } else {
                System.out.println("Please enter a number within the range 1 to 9999");
            }
        } while (noOfComponents == 0);

    }

    private void selectComponentType() {
        System.out.println("Select component type 1. Winglet Strut, 2. Door Handle, 3. Rudder Pin");
        System.out.println("Enter the number corresponding to your choice");
        System.out.println("> ");

        do {
            reader = new Scanner(System.in);
            String input = reader.nextLine();

            switch (input) {
                case "1":
                    componentType = "Winglet Strut";
                    break;
                case "2":
                    componentType = "Door Handle";
                    break;
                case "3":
                    componentType = "Rudder Pin";
                    break;
                default:
                    System.out.println("Please enter a valid choice between 1 and 3");
                    break;
            }
        }
        while (componentType.equals(""));

    }

    private void selectSizeFitmentType() {
        if(componentType.equals("Winglet Strut")) {
            System.out.println("Please select your aircraft type: 1. Airbus A320 or 2. Airbus A380");
            System.out.println("> ");

            do {
                reader = new Scanner(System.in);
                String input = reader.nextLine();

                switch (input) {
                    case "1":
                        sizeFitmentType = "Airbus A320";
                        break;
                    case "2":
                        sizeFitmentType = "Airbus A380";
                        break;
                    default:
                        System.out.println("Please enter a valid choice between 1 and 2");
                        break;
                }
            }
            while (sizeFitmentType.equals(""));
        }
        else {
            System.out.println("Please select your size: 1. '10mm diameter x 75mm length',"
                    + "2. '12mm diameter x 100mm length', or 3. '16mm diameter x 150mm length'");
            System.out.println("> ");

            do {
                reader = new Scanner(System.in);
                String input = reader.nextLine();

                switch (input) {
                    case "1":
                        sizeFitmentType = "10mm diameter x 75mm length";
                        break;
                    case "2":
                        sizeFitmentType = "12mm diameter x 100mm length";
                        break;
                    case "3":
                        sizeFitmentType = "16mm diameter x 150mm length";
                        break;
                    default:
                        System.out.println("Please enter a valid choice between 1 and 3");
                        break;
                }
            }
            while (sizeFitmentType.equals(""));
        }
    }

    private void confirmBatchDetails() throws IOException {
        boolean decisionMade = false;
        if (noOfComponents > 1) {
            System.out.println("This batch contains " + noOfComponents + " " + sizeFitmentType + " " + componentType +
                    "s is this correct? Y/N");
        } else {
            System.out.println("This batch contains " + noOfComponents + " " + sizeFitmentType + " " + componentType +
                    " is this correct? Y/N");
        }


        do {
            reader = new Scanner(System.in);
            String input = reader.nextLine();

            switch (input) {
                case "Y":
                    String date = getCurrentDateFullYear();
                    String time = getCurrentTime();

                    System.out.println("Batch and component records created at " + time + " on " + date);

                    createBatch();
                    printDetails();

                    noOfComponents = 0;
                    componentType = "";
                    sizeFitmentType = "";
                    location = "";
                    componentSerialNumbers.clear();
                    componentStatusList.clear();

                    decisionMade = true;
                    printMenu();
                    break;

                case "N":
                    noOfComponents = 0;
                    componentType = "";
                    sizeFitmentType = "";
                    componentSerialNumbers.clear();
                    componentStatusList.clear();
                    decisionMade = true;
                    retryBatchDetails();
                    break;

                default:
                    System.out.println("Please enter a valid: 'Y' or 'N'");
                    break;
            }
        } while (!decisionMade);

    }

    private void printDetails() {
        System.out.println("Print batch details? (Y/N)");
        System.out.println("> ");

        boolean done = false;

        do {
            reader = new Scanner(System.in);
            String input = reader.nextLine();
            switch (input) {
                case "Y":
                    System.out.println("Batch Number: " + batchNumber);
                    String manufactureDate = getCurrentDateFullYear();
                    System.out.println("Manufacture Date: " + manufactureDate);
                    System.out.println("Component Type: " + componentType);
                    System.out.println("Component Size/Fitment Type: " + sizeFitmentType);
                    System.out.println("Number of Components in Batch: " + noOfComponents);
                    System.out.println("Location: " + location);
                    System.out.println("Serial Numbers: " + componentSerialNumbers);
                    System.out.println("Component Status: " + componentStatusList);
                    done = true;
                    break;

                case "N":
                    done = true;
                    break;

                default:
                    System.out.println("Please enter a valid: 'Y' or 'N'");
                    break;
            }
        } while (!done) ;
    }


    private ArrayList<Component> createComponentList() throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("0000");
        ArrayList<Component> componentList = new ArrayList<>();
        int count = noOfComponents;
        for (int i = 0; i < count; i++) {
            String componentSerialNumberEnding = decimalFormat.format(0001 + i);
            String componentSerialNumber = batchNumber + "-" + componentSerialNumberEnding;
            Component component = new Component(batchNumber, componentSerialNumber, componentType, sizeFitmentType);
            componentList.add(component);
            componentSerialNumbers.add(componentSerialNumber);
            String componentStatus = "Manufactured-unfinished";
            String finish = "Unfinished";
            componentStatusList.add(componentSerialNumber + " Manufactured-unfinished");
            WriteToFile.writeComponentNumberToFile(componentSerialNumber);
            WriteToFile.writeComponentToFile(componentSerialNumber, batchNumber, componentType, sizeFitmentType,
                    getCurrentDateShortenedYear(), componentStatus, finish, location);
        }
        return componentList;
    }

    private void createBatch() throws IOException {
        ArrayList<Component> componentList = createComponentList();
        String manufactureDate = getCurrentDateFullYear();

        WriteToFile.writeBatchNumberToFile(batchNumber);
        //TODO get batch number using getBatchNumber
        WriteToFile.writeBatchToFile(batchNumber, manufactureDate, noOfComponents, componentType, sizeFitmentType, location,
                componentSerialNumbers, componentStatusList);
    }

    private void viewDetailsOfAComponent() throws IOException {
        boolean decisionMade = false;
        JSONArray componentNumbers = ReadFromFile.readComponentNumbers();

        System.out.println("Enter component number:");
        System.out.println("> ");

        do {
            reader = new Scanner(System.in);
            String componentNumber = reader.nextLine();

            if(componentNumbers.contains(componentNumber)) {
                JSONObject component = ReadFromFile.readDetailsOfAComponent(componentNumber);

                String componentType = (String) component.get("componentType");
                String componentSizeType = (String) component.get("sizeFitmentType");
                String manufactureDate = (String) component.get("manufactureDate");
                String currentStatus = (String) component.get("componentStatus");
                String batchForComponent = (String) component.get("batchNumber");

                System.out.println("Component Details for " + componentNumber);
                System.out.println("Type: " + componentType);
                System.out.println("Size/Fit: " + componentSizeType);
                System.out.println("Date of Manufacture: " + manufactureDate);
                System.out.println("Current Status: " + currentStatus);
                System.out.println("Part of Batch: " + batchForComponent);
                System.out.println();
                decisionMade = true;

                printMenu();
            }
            else {
                System.out.println("Invalid component number.");
                printMenu();
                decisionMade = true;
            }

        } while (!decisionMade);
    }

    private void viewDetailsOfABatch() throws IOException {
        boolean decisionMade = false;
        JSONArray batchNumbers = ReadFromFile.readBatchNumbers();

        System.out.println("Enter batch number:");
        System.out.println("> ");

        do {
            reader = new Scanner(System.in);
            String batchNumber = reader.nextLine();

            if(batchNumbers.contains(batchNumber)) {
                JSONObject batch = ReadFromFile.readDetailsOfABatch(batchNumber);

                String location = (String) batch.get("location");
                String manufactureDate = (String) batch.get("manufactureDate");
                String componentType = (String) batch.get("componentType");
                String componentSizeType = (String) batch.get("sizeFitmentType");
                Long numberOfComponents = (Long) batch.get("noOfComponents");
                ArrayList<String> serialNumbers = (ArrayList<String>) batch.get("componentSerialNumbers");
                ArrayList<String> componentStatus = (ArrayList<String>) batch.get("componentStatus");

                System.out.println("Batch Number: " + batchNumber);
                System.out.println("Location: " + location);
                System.out.println("Manufacture Date: " + manufactureDate);
                System.out.println("Component Type: " + componentType);
                System.out.println("Component size/fitment type: " + componentSizeType);
                System.out.println("Nuumber of Components in batch: " + numberOfComponents);
                System.out.println("Serial Numbers " + serialNumbers);
                System.out.println("Component Status: ");
                for (String element : componentStatus) {
                    System.out.println(element);
                }
                System.out.println();
                decisionMade = true;

                printMenu();
            }
            else {
                System.out.println("Invalid batch number.");
                printMenu();
                decisionMade = true;
            }

        } while (!decisionMade);
    }

    private void listAllBatches() throws IOException {
        ArrayList<JSONObject> batches = ReadFromFile.readAllBatches();

        if (!batches.isEmpty()) {
            for (JSONObject batch : batches) {
                String batchNumber = (String) batch.get("batchNumber");
                String type = (String) batch.get("componentType");
                String sizeFitmentType = (String) batch.get("sizeFitmentType");
                Long numberOfComponents = (Long) batch.get("noOfComponents");
                String location = (String) batch.get("location");

                System.out.println("Batch Number: " + batchNumber);
                System.out.println("Type: " + type);
                System.out.println("Size/Fit: " + sizeFitmentType);
                System.out.println("Quantity Made: " + numberOfComponents);
                System.out.println("Location: " + location);
                System.out.println();
                System.out.println();
            }
        }
        else {
            System.out.println("No batches to display");
        }
        printMenu();
    }

    private String getCurrentDateFullYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate date = LocalDate.now();
        return dtf.format(date);
    }

    private String getCurrentDateShortenedYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyy");
        LocalDate date = LocalDate.now();
        return dtf.format(date);
    }

    private String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmm");
        LocalDateTime date = LocalDateTime.now();
        return dtf.format(date);
    }

    private void quit() {
        finished = true;
        System.out.println("System Exited");
        System.exit(0);
    }
}
