import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

//Jack Makrides
//mib18163

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

    /*
    Method that is run by main, ensures the "batchnumbers" and "componentnumbers" files are present and set up correctly
    before printing the welcome mesage and printing the menu
     */
    public void start() throws IOException {
        WriteToFile.createBatchNumberFile();
        WriteToFile.createComponentNumberFile();
        printWelcome();
        printMenu();
    }


    // Prints welcome message to user
    private void printWelcome() {
        System.out.println("Welcome to the GPEC inventory system.");
    }

    /*
    Method for printing out the systems user menu and then taking the users input and directing the program to the correct
    method
     */
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
                    System.out.println("Please choose a valid menu option, 1 - 8");
            }
        }
        while (!finished);
    }

    /*
    Method for adding a finish to a component. The method first checks to see if the component serial number entered
    by the user matches any component serial number in the componentnumbers file. If so it then prints out the details
    of this component and asks the user to confirm whether this is correct or not. Next the system asks the user if they
    want a polished or painted finish. If they want a polished finish this is applied, if they want a painted finish they
    are then prompted to enter a 4 character paint code to be applied to the component.
     */
    private void finishComponent() throws IOException {
        JSONArray componentNumbers = ReadFromFile.readComponentNumbers();

        System.out.println("Enter component serial number:");
        System.out.println("> ");

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
            System.out.println("> ");

            do {
                reader = new Scanner(System.in);
                String input = reader.nextLine();

                switch (input) {
                    case "Y":
                        String currentStatus = (String) component.get("componentStatus");
                        if(currentStatus.equals("Manufactured-unfinished")) {
                            boolean finishChosen = false;
                            System.out.println("Select finish (1. Polished, 2. Painted)");
                            System.out.println("> ");

                            do {
                                reader = new Scanner(System.in);
                                String finishInput = reader.nextLine();

                                switch(finishInput) {
                                    case "1":
                                        WriteToFile.assignFinishForComponent(componentNumber, "Polished");
                                        System.out.println("Component number " + componentNumber + " will have a polished finish.");
                                        System.out.println();
                                        System.out.println();
                                        finishChosen = true;
                                        break;
                                    case "2":
                                        System.out.println("Enter paint code (4 characters)");
                                        System.out.println("> ");

                                        boolean paintCodeCorrect = false;
                                        do {
                                            reader = new Scanner(System.in);
                                            String paintCode = reader.nextLine();
                                            if(paintCode.length() == 4) {
                                                String upperCasePaintCode = paintCode.toUpperCase();
                                                WriteToFile.assignFinishForComponent(componentNumber, upperCasePaintCode);
                                                paintCodeCorrect = true;
                                                System.out.println("Component number " + componentNumber + " will be finished in Paint Code " + upperCasePaintCode);
                                                System.out.println();
                                                System.out.println();
                                            } else {
                                                System.out.println("Please enter a 4 character paint code.");
                                            }
                                        } while(!paintCodeCorrect);
                                        finishChosen = true;
                                        break;
                                    default:
                                        System.out.println("Please enter a valid input: '1' or '2'");
                                        break;
                                }
                            } while(!finishChosen);

                        } else {
                            System.out.println("Component already has a finish.");
                            System.out.println();
                            System.out.println();
                            printMenu();
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

            printMenu();
        }
        else {
            System.out.println("Invalid component number.");
            System.out.println();
            System.out.println();
            printMenu();
        }
    }

    /*
    This method allows the user to search for available components based on a component type and component size/model.
    The method reuses the selectComponentType() and selectSizeFitmentType() methods. It then asks the user to confirm if
    this is what they wish to search for. If there are components matching the users input then the method will print out
    the details of these components. If not it will notify the user that there is no stock available.
     */
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

    /*
    Method that allows a user to allocate a batch to a chosen location. The method checks that the batch has not already
    been allocated to a location. If it hasn't, it gives the user the option to allocate the batch to either Glasgow or
    Dubai.
     */
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
                System.out.println();
                System.out.println();
                printMenu();
            }
        }
        else {
            System.out.println("Invalid batch number.");
            System.out.println();
            System.out.println();
            printMenu();

        }
    }

    /*
    Method used to call sub methods for getting the details of a batch to be created. The selectSizeFitmentType() method
    is skipped if the component type is "Door Handle", due to the fact that this is a universal fit component.
     */
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

    /*
    This method is nearly identical to the getBatchDetails() method. It differs by not generating a new batch number and
    not reassigning the default location. This method is used when the user answers no to the question of if the batch
    details are correct. It allows the user to re-enter the details without generating a new batch number.
     */
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

    /*
    Method for generating a new unique batch number. This is done by first getting a list of batch numbers from the
    batchnumbers file. It then check to see if the list is empty therefore it will create a batch number with todays date
    and "0001". If the list is not empty, it gets the latest batch number from the list and gets the date from it. If this
    date matches todays date, it will add 1 to the batch number. If it does not match todays date it will create a new batch number
    with todays date and "0001".
     */
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

    /*
    Method for allowing a user to input the number of components they want to produce. This must be between 1 and 9999.
     */
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

    /*
    Method that allows the user to choose which type of component they want to create in the batch.
     */
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

    /*
    Method for letting the user choose the size/fitment type of the component based on which component they chose previously.
     */
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

    /*
    This method allows the user to confirm whether they want to create a batch with the details they inputted or if
    they would like to change these details. If the user wants to change the details, the retryBatchDetails() method is
    called. If not, the user is told at what time and date the batch and components have been recorded at. It then calls
    the createBatch() method followed by the printDetails() method before returning the user to the main menu.
     */
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

    /*
    Method for printing out the details of the batch that has just been created. The user has the choice to print the
    details or not print them.
     */
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


    /*
    Method for creating a list of components. It is also responsible for assinging each component a unique serial number.
    It creates a list of serial numbers and component statuses to be used when writing a batch to file. It also writes the
    component serial numbers to a file along with writing each component and it's details to an individual file.
     */
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

    /*
    Method that writes the batch number to the batchnumbers file and writes the batch details to a file.
     */
    private void createBatch() throws IOException {
        ArrayList<Component> componentList = createComponentList();
        String manufactureDate = getCurrentDateFullYear();

        WriteToFile.writeBatchNumberToFile(batchNumber);
        //TODO get batch number using getBatchNumber
        WriteToFile.writeBatchToFile(batchNumber, manufactureDate, noOfComponents, componentType, sizeFitmentType, location,
                componentSerialNumbers, componentStatusList);
    }

    /*
    This method will print out the details of a component by taking in a serial number from the user. The inputted
    component number is compared against the list of component serial numbers in the componetnumbers file to check that
    it exists in the system.
     */
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
                String finish = (String) component.get("finish");
                String batchForComponent = (String) component.get("batchNumber");

                System.out.println("Component Details for " + componentNumber);
                System.out.println("Type: " + componentType);
                System.out.println("Size/Fit: " + componentSizeType);
                System.out.println("Date of Manufacture: " + manufactureDate);
                System.out.println("Current Status: " + currentStatus);
                System.out.println("Finish: " + finish);
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

    /*
    This method will print out the details of a batch by taking in a batch number from the user. The inputted
    batch number is compared against the list of batch numbers in the batchnumbers file to check that
    it exists in the system.
     */
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

    /*
    This method prints out some details of all batches that are stored in the system.
     */
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

    /*
    Gets the current date in full year format
     */
    private String getCurrentDateFullYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate date = LocalDate.now();
        return dtf.format(date);
    }

    /*
    Gets the current date in shortened year format
     */
    private String getCurrentDateShortenedYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyy");
        LocalDate date = LocalDate.now();
        return dtf.format(date);
    }

    /*
    Get the current time
     */
    private String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmm");
        LocalDateTime date = LocalDateTime.now();
        return dtf.format(date);
    }

    /*
    Used by the main menu to exit the system
     */
    private void quit() {
        finished = true;
        System.out.println("System Exited");
        System.exit(0);
    }
}
