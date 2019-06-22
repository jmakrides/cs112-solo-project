import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class FactorySystem {

    private Scanner reader;
    private boolean finished = false;
    private String batchNumber = "";
    private int noOfComponents = 0;
    private String componentType = "";
    private String sizeFitmentType = "";

    public FactorySystem() {

    }

    public void start() throws IOException {
        WriteToFile.createBatchNumberFile();
        printWelcome();
        printMenu();
    }

    private void printWelcome() {
        System.out.println("Welcome to the GPEC inventory system.");
    }

    private void printMenu() throws IOException {
        System.out.println("Please choose one of the following options:");
        System.out.println("1. Create a new batch");
        System.out.println("2. Quit");
        System.out.println("> ");

        do {
            reader = new Scanner(System.in);
            int input = reader.nextInt();

            switch (input) {
                case 1:
                    getBatchDetails();
                    break;
                case 2:
                    quit();
                    break;
                default:
                    System.out.println("Please choose a valid option");
            }
        }
        while (!finished);
    }

    private void getBatchDetails() throws IOException {
        generateBatchNo();
        System.out.println(batchNumber);

        selectNoComponents();

        selectComponentType();

        if(componentType.equals("Rudder Pin") || componentType.equals("Winglet Strut"))
        {
            selectSizeFitmentType();
        }

        confirmBatchDetails();
    }

    private void retryBatchDetails() throws IOException {
        System.out.println(batchNumber);

        selectNoComponents();

        selectComponentType();

        if(componentType.equals("Rudder Pin")|| componentType.equals("Winglet Strut"))
        {
            selectSizeFitmentType();
        }

        confirmBatchDetails();
    }

    private void generateBatchNo() {
        String dateString = getCurrentDateShortenedYear();

        batchNumber = dateString + "0001";
    }

    private void selectNoComponents() {
        System.out.println("How many components do you want to manufacture (1 to 9999)?");
        System.out.println("> ");

        do {
            reader = new Scanner(System.in);
            int input = reader.nextInt();

            if(input >=1 & input <=9999) {
                noOfComponents = input;
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
            int input = reader.nextInt();

            switch (input) {
                case 1:
                    componentType = "Winglet Strut";
                    break;
                case 2:
                    componentType = "Door Handle";
                    break;
                case 3:
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
                int input = reader.nextInt();

                switch (input) {
                    case 1:
                        sizeFitmentType = "Airbus A320";
                        break;
                    case 2:
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
            System.out.println("Please select your size: 1. '10mm diameter x 75mm length', 2. '12mm diameter x 100mm length', or 3. '16mm diameter x 150mm length'");
            System.out.println("> ");

            do {
                reader = new Scanner(System.in);
                int input = reader.nextInt();

                switch (input) {
                    case 1:
                        sizeFitmentType = "10mm diameter x 75mm length";
                        break;
                    case 2:
                        sizeFitmentType = "12mm diameter x 100mm length";
                        break;
                    case 3:
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
            System.out.println("This batch contains " + noOfComponents + " " + sizeFitmentType + " " + componentType + "s is this correct? Y/N");
        } else {
            System.out.println("This batch contains " + noOfComponents + " " + sizeFitmentType + " " + componentType + " is this correct? Y/N");
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

                    noOfComponents = 0;
                    componentType = "";
                    sizeFitmentType = "";

                    decisionMade = true;
                    printMenu();
                    break;

                case "N":
                    noOfComponents = 0;
                    componentType = "";
                    sizeFitmentType = "";
                    retryBatchDetails();
                    break;

                default:
                    System.out.println("Please enter a valid: 'Y' or 'N'");
                    break;
            }
        } while (!decisionMade);

    }

    private Batch createBatch() throws IOException {
        WriteToFile.writeBatchNumberToFile(batchNumber);
        return new Batch(batchNumber, noOfComponents, componentType, sizeFitmentType);
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
