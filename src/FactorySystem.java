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

    public void start() {
        printWelcome();
        printMenu();
    }

    private void printWelcome() {
        System.out.println("Welcome to the GPEC inventory system.");
    }

    private void printMenu() {
        System.out.println("Please choose one of the following options:");
        System.out.println("1. Create a new batch");
        System.out.println("2. Quit");
        System.out.println("> ");

        reader = new Scanner(System.in);
        String input = reader.nextLine();

        if(!finished) {
            if(input.equals("1")) {
                getBatchDetails();
            }
            if(input.equals("2")) {
                quit();
            }
            else {
                System.out.println("Please choose a valid option");
            }
        }
    }

    private void getBatchDetails() {

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

    private void retryBatchDetails() {
        System.out.println(batchNumber);

        selectNoComponents();

        selectComponentType();

        if(componentType.equals("Rudder Pin")|| componentType.equals("Winglet Strut"));
        {
            selectSizeFitmentType();
        }

        confirmBatchDetails();
    }

    private void generateBatchNo() {
        String dateString = getCurrentDateShortenedYear();

        String batchNo = dateString + "0001";

        batchNumber = batchNo;
    }

    private void selectNoComponents() {
        System.out.println("How many components do you want to manufacture (1 to 9999)?");
        System.out.println("> ");

        while (noOfComponents == 0) {
            reader = new Scanner(System.in);
            int input = reader.nextInt();

            if(input >=1 & input <=9999) {
                noOfComponents = input;
            } else {
                System.out.println("Please enter a number within the range 1 to 9999");
            }
        }

    }

    private void selectComponentType() {
        System.out.println("Select component type 1. Winglet Strut, 2. Door Handle, 3. Rudder Pin");
        System.out.println("Enter the number corresponding to your choice");
        System.out.println("> ");

        reader = new Scanner(System.in);
        String input = reader.nextLine();

        while (componentType.equals(""))
        {
            if(input.equals("1")) {
                componentType = "Winglet Strut";
            }
            if(input.equals("2")) {
                componentType = "Door Handle";
            }
            if(input.equals("3")) {
                componentType = "Rudder Pin";
            }
            else {
                System.out.println("Please enter a valid choice between 1 and 3");
            }
        }

    }

    private void selectSizeFitmentType() {


        if(componentType.equals("Winglet Strut")) {
            System.out.println("Please select your aircraft type: 1. Airbus A320 or 2. Airbus A380");
            System.out.println("> ");

            while (sizeFitmentType.equals("")) {
                reader = new Scanner(System.in);
                String input = reader.nextLine();

                if (input.equals("1")) {
                    sizeFitmentType = "Airbus A320";
                }
                if (input.equals("2")) {
                    sizeFitmentType = "Airbus A380";
                } else {
                    System.out.println("Please enter a valid choice between 1 and 2");
                }
            }

        }
        if(componentType.equals("Rudder Pin")) {
            System.out.println("Please select your size: 1. '10mm diameter x 75mm length', 2. '12mm diameter x 100mm length', or 3. '16mm diameter x 150mm length'");
            System.out.println("> ");

            while (sizeFitmentType.equals("")) {
                reader = new Scanner(System.in);
                String input = reader.nextLine();

                if (input.equals("1")) {
                    sizeFitmentType = "10mm diameter x 75mm length";
                }
                if (input.equals("2")) {
                    sizeFitmentType = "12mm diameter x 100mm length";
                }
                if (input.equals("3")) {
                    sizeFitmentType = "16mm diameter x 150mm length";
                }
                else {
                    System.out.println("Please enter a valid choice between 1 and 3");
                }
            }

        }
    }

    private void confirmBatchDetails() {
        System.out.println("This batch contains " + noOfComponents + " " + sizeFitmentType + " " + componentType + " is this correct? Y/N");

        reader = new Scanner(System.in);
        String input = reader.nextLine();

        if (input.equals("Y")) {
            String date = getCurrentDateFullYear();
            String time = getCurrentTime();

            System.out.println("Batch and component records created at " + time + " on " + date);

            createBatch();
            printMenu();
        }
        if (input.equals("N")) {
            noOfComponents = 0;
            componentType = "";
            sizeFitmentType = "";
            retryBatchDetails();
        }
        else {
            System.out.println("Please enter a valid: 'Y' or 'N'");
        }
    }

    private Batch createBatch() {
        Batch batch = new Batch(batchNumber, noOfComponents, componentType, sizeFitmentType);

        return batch;
    }

    private String getCurrentDateFullYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate date = LocalDate.now();
        String dateString = dtf.format(date);
        return dateString;
    }

    private String getCurrentDateShortenedYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyy");
        LocalDate date = LocalDate.now();
        String dateString = dtf.format(date);
        return dateString;
    }

    private String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmm");
        LocalDateTime date = LocalDateTime.now();
        String timeString = dtf.format(date);
        return timeString;
    }

    private void quit() {
        finished = true;
        System.out.println("System Exited");
    }
}
