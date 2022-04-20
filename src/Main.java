import java.util.*;

public class Main {
    public static void main(String[] args) {
        mainLoop();
    }

    public static void mainLoop() {

        try (Scanner scanner = new Scanner(System.in)) {
            boolean isWorking = true;

            DataService[] datasetArrayReference = new DataService[20];
            initialProcess(datasetArrayReference);

            System.out.println("----------------------Welcome to the forecasting program----------------------");
            while (isWorking) {
                String message = """
                        \nChoose process you want to do:
                        1 \t|  List datasets
                        2 \t|  Insert a new dataset
                        3 \t|  Delete a dataset\s
                        4 \t|  Forecast utilizing dataset (choose the number of the dataset)
                        5 \t|  Find maximum forecasted sales numbers
                        6 \t|  Find minimum forecasted sales numbers
                        7 \t|  Sort forecasted sales in descending order
                        8 \t|  Find maximum sales count on dataset (choose the number of the dataset)
                        9 \t|  Find minimum sales count on dataset (choose the number of the dataset)
                        10\t|  Search a value on a dataset (choose the number of the dataset)
                        11\t|  Replace a value on the dataset (choose the number of the dataset)
                        12\t|  Print all values on a dataset in reverse order
                        13\t|  Close the program""";
                System.out.println(message);
                int choice;
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                } else {
                    System.out.println("Please type a proper process! (As integer)");
                    scanner.next();
                    continue;
                }

                switch (choice) {
                    case 1:
                        int listIndex;
                        int dataSetLength = datasetManagerSizeCheck(datasetArrayReference);

                        boolean notCorrect = true;
                        System.out.println("\tType which dataset you want to print? ( There are " + dataSetLength
                                + " datasets. (including initial datasets))");
                        while (notCorrect) {
                            if (scanner.hasNextInt()) {
                                listIndex = scanner.nextInt();
                            } else {
                                System.out.println("Please type an integer!");
                                scanner.next();
                                continue;
                            }
                            if (datasetArrayReference[0] == null && datasetArrayReference[1] != null) {
                                if (listIndex == 2)
                                    datasetArrayReference[listIndex - 1].print();
                                break;
                            }
                            if (listIndex > dataSetLength || listIndex < 1) {
                                System.out.println("\tPlease type a correct dataset number (1-" + dataSetLength + ") : ");
                            } else if (listIndex == 1 && datasetArrayReference[listIndex - 1] == null
                                    || listIndex == 2 && datasetArrayReference[listIndex - 1] == null) {
                                System.out.println("This dataset was deleted.");
                            } else {
                                System.out.println("---- Dataset " + listIndex + "----");
                                datasetArrayReference[listIndex - 1].print();
                                notCorrect = false;
                            }
                        }
                        break;

                    case 2:
                        int totalDay = 720;
                        System.out.println("Type time gap of the each data on the dataset as days: ");
                        int timeGap = scanner.nextInt();

                        while (timeGap != 10 && timeGap != 15 && timeGap != 30 && timeGap != 90) {
                            System.out.println("Choose 10/15/30 days: ");
                            timeGap = scanner.nextInt();
                        }
                        int size = totalDay / timeGap;
                        boolean isEmpty = true;
                        int datasetArrayLength = 0;
                        while (isEmpty) {
                            int i = 0;
                            while (i < datasetArrayReference.length) {
                                if (datasetArrayReference[i] != null)
                                    datasetArrayLength++;
                                i++;
                            }
                            isEmpty = false;
                        }

                        datasetArrayReference[datasetArrayLength] = new DataService();
                        for (int i = 0; i < size; i++) {
                            System.out.println("Type" + (i + 1) + ". element: ");
                            if (scanner.hasNextInt()) {
                                datasetArrayReference[datasetArrayLength].insert(scanner.nextInt());
                            } else {
                                System.out.println("Please type an integer)");
                                i--;
                                scanner.next();
                            }
                        }

                        System.out.println("\n\tThe new dataset's number is " + (datasetArrayLength + 1));
                        break;

                    case 3:

                        notCorrect = true;
                        dataSetLength = datasetManagerSizeCheck(datasetArrayReference);
                        System.out.println("\tWhich dataset you want to delete? (1-" + dataSetLength + ") : ");
                        while (notCorrect) {
                            listIndex = scanner.nextInt();
                            if (listIndex > dataSetLength || listIndex < 1) {
                                System.out.println("\tPlease type a correct dataset number (1-" + dataSetLength + ") : ");
                            } else {
                                datasetArrayReference[listIndex - 1] = null;
                                System.out.println("Dataset " + (listIndex) + " deleted.");
                                notCorrect = false;
                            }
                        }
                        break;

                    case 4:
                        dataSetLength = datasetManagerSizeCheck(datasetArrayReference);
                        System.out.println("Which dataset you want to forecast?( choose between 1 and " + dataSetLength
                                + " including initial datasets)");

                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }

                        while (listIndex > dataSetLength || listIndex < 1) {
                            System.out.println("\tPlease type a correct dataset number (1-" + dataSetLength + ") : ");
                            listIndex = scanner.nextInt();
                        }

                        System.out.println("Predictions for Dataset" + (listIndex));

                        List<List<Integer>> list;
                        list = addIntoList(datasetArrayReference[listIndex - 1]);

                        List<Double> expMSEList = exponentialSmoothing(datasetArrayReference[listIndex - 1]);
                        double expoMSE = expMSEList.get(expMSEList.size() - 1);
                        List<Double> listDouble = doubleExponentialSmoothing(datasetArrayReference[listIndex - 1]);
                        double doubleMSE = listDouble.get(listDouble.size() - 1);

                        List<Double> regressionMseFinderList = regressionPrediction(list);
                        double regressionMSE = regressionMseFinderList.get(regressionMseFinderList.size() - 1);

                        List<List<Integer>> deseasonalizedRegMSEList = addIntoList(datasetArrayReference[listIndex - 1]);
                        List<Double> deseasonalizedMseList = deseasonalizedRegression(list);
                        double deseasonalizedMSE = deseasonalizedMseList.get(deseasonalizedRegMSEList.get(1).size());
                        System.out.println("\n---------------------------------------------------");
                        System.out.println(
                                "Exponential Smoothing \t\t MSE: " + expoMSE + "\nDouble exponential Smoothing MSE: "
                                        + doubleMSE + "\nRegression \t\t\t\t\t MSE: " + regressionMSE
                                        + "\nDeseasonalized Regression \t MSE: " + deseasonalizedMSE
                                        + "\n*MSE stands for Mean Squared Error.");
                        List<Double> mseList = new ArrayList<>();
                        mseList.add(regressionMSE);
                        mseList.add(doubleMSE);
                        mseList.add(expoMSE);
                        mseList.add(deseasonalizedMSE);

                        double minMSE = findBestForecastAndMinSales(mseList);
                        System.out.println("---------------------------------------------------");

                        if (minMSE == expoMSE)
                            System.out.println("The best method for forecast is Exponential Smoothing :" + minMSE);
                        else if (minMSE == doubleMSE)
                            System.out.println("The best method for forecast is Double Exponential Smoothing :" + minMSE);
                        else if (minMSE == regressionMSE)
                            System.out.println("The best method for forecast is Regression Analysis :" + minMSE);
                        else
                            System.out.println("The best method for forecast is Deseasonalized Regression :" + minMSE);
                        break;

                    case 5:
                        dataSetLength = datasetManagerSizeCheck(datasetArrayReference);
                        System.out.println("Which dataset you want to see maximum sale forecast?( choose between 1 and "
                                + dataSetLength + " including initial datasets)");

                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }

                        while (listIndex > dataSetLength || listIndex < 1) {
                            System.out.println("\tPlease type a correct dataset number (1-" + dataSetLength + ") : ");
                            listIndex = scanner.nextInt();
                        }
                        double maxExpSmoothing = findMaxSaleForecast(
                                exponentialSmoothing(datasetArrayReference[listIndex - 1]));
                        double maxDoubleExpSmoothing = findMaxSaleForecast(
                                doubleExponentialSmoothing(datasetArrayReference[listIndex - 1]));
                        list = addIntoList(datasetArrayReference[listIndex - 1]);
                        double maxRegressionPrediction = findMaxSaleForecast(regressionPrediction(list));
                        double maxDeseasonalizedPrediction = findMaxSaleForecast(deseasonalizedRegression(list));
                        System.out.println("---------------------- Max sale prediction numbers for Dataset " + listIndex
                                + " ----------------------");
                        System.out.println("| Max sale in Exponential Smoothing Forecast\t\t\t | " + maxExpSmoothing + " |"
                                +
                                "\n| Max sale in Double Exponential Smoothing Forecast\t\t | " + maxDoubleExpSmoothing + "|"
                                +
                                "\n| Max sale in Regression Analysis Forecast\t\t\t\t | " + maxRegressionPrediction + " |" +
                                "\n| Max sale in Deseasonalized Regression Analysis Forecast| "
                                + maxDeseasonalizedPrediction + " |");
                        System.out.println("-----------------------------------------------------------------------------");
                        break;

                    case 6:
                        dataSetLength = datasetManagerSizeCheck(datasetArrayReference);
                        System.out.println("Which dataset you want to see maximum sale forecast?( choose between 1 and "
                                + dataSetLength + " including initial datasets)");

                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }

                        while (listIndex > dataSetLength || listIndex < 1) {
                            System.out.println("\tPlease type a correct dataset number (1-" + dataSetLength + ") : ");
                            listIndex = scanner.nextInt();
                        }
                        double minExpSmoothing = findBestForecastAndMinSales(
                                exponentialSmoothing(datasetArrayReference[listIndex - 1]));
                        double minDoubleExpSmoothing = findBestForecastAndMinSales(
                                doubleExponentialSmoothing(datasetArrayReference[listIndex - 1]));
                        list = addIntoList(datasetArrayReference[listIndex - 1]);
                        double minRegressionPrediction = findBestForecastAndMinSales(regressionPrediction(list));
                        double minDeseasonalizedPrediction = findBestForecastAndMinSales(deseasonalizedRegression(list));
                        System.out.println("-------------------- Min sale prediction numbers for Dataset " + listIndex
                                + " --------------------");
                        System.out.println("Min sale in Exponential Smoothing Forecast\t\t\t\t: " + minExpSmoothing +
                                "\nMin sale in Double Exponential Smoothing Forecast\t\t: " + minDoubleExpSmoothing +
                                "\nMin sale in Regression Analysis Forecast\t\t\t\t: " + minRegressionPrediction +
                                "\nMin sale in Deseasonalized Regression Analysis Forecast\s: "
                                + minDeseasonalizedPrediction);
                        System.out.println("---------------------------------------------------------------------------");
                        break;

                    case 7:
                        dataSetLength = datasetManagerSizeCheck(datasetArrayReference);
                        System.out.println("Which dataset you want to sort?( choose between 1 and " + dataSetLength
                                + " including initial datasets)");

                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }

                        while (listIndex > dataSetLength || listIndex < 1) {
                            System.out.println("\tPlease type a correct dataset number (1-" + dataSetLength + ") : ");
                            listIndex = scanner.nextInt();
                        }
                        addAndSort(datasetArrayReference[listIndex - 1]);
                        break;

                    case 8:
                        System.out.println("\tType which list you want to see it's maximum value: ");
                        dataSetLength = datasetManagerSizeCheck(datasetArrayReference);

                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                            while (listIndex < 1 || listIndex > dataSetLength) {
                                System.out.println("Invalid input. Please type an integer!");
                                listIndex = scanner.nextInt();
                                if (scanner.hasNextInt()) {
                                    listIndex = scanner.nextInt();
                                } else {
                                    System.out.println("Invalid input. Please type an integer!");
                                    scanner.next();
                                }
                            }
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }
                        findMax(datasetArrayReference[listIndex - 1]);
                        break;

                    case 9:
                        System.out.println("Type which list you want to see it's minimum value: ");
                        dataSetLength = datasetManagerSizeCheck(datasetArrayReference);

                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                            while (listIndex > dataSetLength || listIndex < 1) {
                                System.out.println("Invalid input! Please type an integer!");
                                listIndex = scanner.nextInt();
                                if (scanner.hasNextInt()) {
                                    listIndex = scanner.nextInt();
                                } else {
                                    System.out.println("Invalid input! Please type an integer!");
                                    scanner.next();
                                }
                            }
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }
                        findMin(datasetArrayReference[listIndex - 1]);
                        break;

                    case 10:
                        System.out.println("Which dataset you want to search a value?");
                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }
                        listIndex--;
                        search(datasetArrayReference[listIndex]);
                        break;

                    case 11:
                        System.out.println("In which dataset you want to change a value?");
                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }
                        replace(datasetArrayReference[listIndex - 1]);
                        break;

                    case 12:
                        System.out.println("Type which list you want to see reverse: ");
                        if (scanner.hasNextInt()) {
                            listIndex = scanner.nextInt();
                        } else {
                            System.out.println("Invalid input! Please type an integer!");
                            scanner.next();
                            continue;
                        }
                        reverseDataset(datasetArrayReference[listIndex - 1]);
                        break;

                    case 13:
                        isWorking = false;
                        System.out.println("Closing the program...");
                        break;

                    default:
                        System.out.println("Unexpected value! Please type a correct process: ");
                }
            }
        }
    }

    public static int sizeCheck(DataService dataService) {
        boolean sizeNotFound = true;
        int dataSetSize = 0;
        while (sizeNotFound) {
            for (int i = 0; i < dataService.size(); i++) {
                if (dataService.getByPosition(i) != null)
                    dataSetSize++;
            }
            sizeNotFound = false;
        }
        return dataSetSize;
    }

    public static int datasetManagerSizeCheck(DataService[] dataService) {
        int dataSetLength = 0;

        boolean isEmpty = true;
        while (isEmpty) {
            for (int i = 0; i < 20; i++) {
                if (dataService[i] != null)
                    dataSetLength++;
            }
            isEmpty = false;
        }
        return dataSetLength;
    }

    public static List<Double> exponentialSmoothing(DataService dataService) {
        int dataSetSize = sizeCheck(dataService);
        String timeGapName;

        if (dataSetSize == 24)
            timeGapName = " month ";
        else if (dataSetSize == 48)
            timeGapName = " 15 days ";
        else
            timeGapName = " 10 days ";

        double prediction;
        final double a = 0.2;

        int ref = 0;
        prediction = a * (dataService.getByPosition(ref).getData())
                + (1 - a) * (dataService.getByPosition(ref).getData());
        System.out.println("\n-----Exponential Smoothing-----");
        System.out.println("________________________________");
        System.out.println("| 1. " + timeGapName + "Prediction |  " + prediction + " |");

        double error;
        double errorSquare;
        double errorSum = 0;

        List<Double> predictionList = new ArrayList<>();
        System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
        for (int i = 1; i < dataSetSize; i++) {
            prediction = a * (dataService.getByPosition(i - 1).getData()) + (1 - a) * (prediction);
            error = dataService.getByPosition(i).getData() - prediction;
            errorSquare = error * error;
            errorSum += errorSquare;
            System.out.println("| " +
                    (i + 1) + ". " + timeGapName + "Prediction |  " + prediction + " |");
            System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
            predictionList.add(prediction);
        }

        double mse = errorSum / dataSetSize;
        System.out.println("\nMSE for Exponential Smoothing: " + mse);
        predictionList.add(mse);
        return predictionList;
    }

    public static List<Double> doubleExponentialSmoothing(DataService dataService) {// Calculating
        String timeGapName;
        int dataSetSize = sizeCheck(dataService);

        if (dataSetSize == 24)
            timeGapName = " month ";
        else if (dataSetSize == 48)
            timeGapName = " 15 days ";
        else
            timeGapName = " 10 days ";

        final double alpha = 0.2;
        final double beta = 0.2;
        final double initialS = 200;
        final double initialG = 50;

        double st = alpha * (dataService.getByPosition(0).getData()) + (1 - alpha) * (initialS + initialG);
        System.out.println("ST : " + st);
        double gt = beta * (st - initialS) + (1 - beta);
        System.out.println("GT : " + gt);
        int i = 1;
        double predictionDouble = st + (i * gt);
        double error = dataService.getByPosition(0).getData() - predictionDouble;
        double errorSquare = error * error;
        double errorSum = 0;
        errorSum += errorSquare;
        System.out.println("\n--Double Exponential Smoothing--");
        System.out.println("_______________________________");
        System.out.println("| 1." + timeGapName + "Prediction | " + predictionDouble + " |");
        i++;

        List<Double> predictionList = new ArrayList<>();
        System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
        for (int j = 1; j < dataSetSize; j++) {

            double stRef = st;
            double gtRef = gt;

            st = alpha * (dataService.getByPosition(j).getData()) + (1 - alpha) * (st + gt);
            gt = beta * (st - stRef) + (1 - beta) * gtRef;

            predictionDouble = st + (i * gt);
            i++;

            error = dataService.getByPosition(j).getData() - predictionDouble;
            errorSquare = error * error;
            errorSum += errorSquare;

            System.out.println("| " + (j + 1) + "." + timeGapName + "Prediction | " + predictionDouble + " |");
            System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
            predictionList.add(predictionDouble);
        }

        double mse = errorSum / dataSetSize;

        System.out.println("\nMSE for Double Exponential Smoothing: " + mse);
        predictionList.add(mse);
        return predictionList;
    }

    public static void reverseDataset(DataService dataService) {// Reverses dataset
        Node prev = null;
        Node currentReverse = dataService.head;
        Node next;
        while (currentReverse != null) {
            next = currentReverse.next;
            currentReverse.next = prev;
            prev = currentReverse;
            currentReverse = next;
        }
        dataService.head = prev;
        dataService.print();

        prev = null;
        currentReverse = dataService.head;
        while (currentReverse != null) {
            next = currentReverse.next;
            currentReverse.next = prev;
            prev = currentReverse;
            currentReverse = next;
        }
        dataService.head = prev;
    }

    public static void findMax(DataService dataService) {
        Node temp = dataService.head;
        int max;

        if (dataService.head == null) {
            System.out.println("List is empty");
        } else {
            // Initializing max with head node data
            max = dataService.head.getData();

            while (temp != null) {
                // If current node's data is greater than max, then replace value of max with
                // current node's data
                if (max < temp.getData()) {
                    max = temp.getData();
                }
                temp = temp.next;
            }
            System.out.println("Maximum sale in the dataset: " + max);
        }
    }

    public static void findMin(DataService dataService) {
        Node current = dataService.head;
        int min;
        if (dataService.head == null) {
            System.out.println("List is empty");
        } else {
            // Initializing min with head node data
            min = dataService.head.getData();

            while (current != null) {
                // If current node's data is smaller than min, then replace value of min with
                // current node's data
                if (min > current.getData()) {
                    min = current.getData();
                }
                current = current.next;
            }
            System.out.println("Minimum sale in the dataset: " + min);
        }
    }

    public static void search(DataService dataService) {// searching in linkedlist method
        try (Scanner scanner = new Scanner(System.in)) {
            // Stores head Node
            Node tempSearch = dataService.head;
            int x = 0;
            System.out.println("Type the value you want to search: ");
            if (scanner.hasNextInt()) {
                x += scanner.nextInt();
            } else {
                System.out.println("Invalid input! Please type an integer!");
                scanner.next();

            }

            // Stores position of the integer in the doubly linked list
            int pos = 0;
            // Traverse the doubly linked list
            while (tempSearch.getData() != x
                    && tempSearch.next != null) {
                // Update pos
                pos++;
                // Update temp
                tempSearch = tempSearch.next;
            }
            // If the integer not present in the doubly linked list
            if (tempSearch.getData() != x) {
                System.out.println("No such value in this dataset!");
                return;
            }
            // If the integer present in the doubly linked list

            System.out.println("Value that you typed is: " + x + "\nThe index of value is: " + (pos + 1));
        }
    }

    public static void replace(DataService dataService) {// Value replacing method
        try (Scanner scanner = new Scanner(System.in)) {
            Node tempChange = dataService.head;
            System.out.println("Type the value you want to change: ");
            int y = 0;
            boolean notInteger = true;
            while (notInteger) {
                if (scanner.hasNextInt()) {
                    y = scanner.nextInt();
                    notInteger = false;
                } else {
                    System.out.println("Invalid input! Please type an integer!");
                    scanner.next();
                }
            }
            // Stores position of the integer
            // in the doubly linked list
            int replacePos = 0;

            // Traverse the doubly linked list
            while (tempChange.getData() != y
                    && tempChange.next != null) {
                // Update pos
                replacePos++;
                // Update temp
                tempChange = tempChange.next;
            }

            // If the integer not present in the doubly linked list
            if (tempChange.getData() != y) {
                System.out.println("No such value in this dataset!");
                return;
            }
            // If the integer present in the doubly linked list
            System.out
                    .println("The index of such value is =>" + (replacePos + 1) + "\nValue is => " + tempChange.getData());

            if (tempChange.getData() == y
                    && tempChange.next != null) {
                System.out.println("Now type the new value");
                int replace = scanner.nextInt();
                // Update the value
                tempChange.setData(replace);
            }
        }
        System.out.println("The new dataset:\n");
        dataService.print();
    }

    public static void initialProcess(DataService[] datasetArrayReference) {

        datasetArrayReference[0] = new DataService();
        datasetArrayReference[0].insert(300);
        datasetArrayReference[0].insert(350);
        datasetArrayReference[0].insert(330);
        datasetArrayReference[0].insert(340);
        datasetArrayReference[0].insert(390);
        datasetArrayReference[0].insert(430);
        datasetArrayReference[0].insert(480);
        datasetArrayReference[0].insert(460);
        datasetArrayReference[0].insert(490);
        datasetArrayReference[0].insert(510);
        datasetArrayReference[0].insert(550);
        datasetArrayReference[0].insert(560);
        datasetArrayReference[0].insert(550);
        datasetArrayReference[0].insert(590);
        datasetArrayReference[0].insert(600);
        datasetArrayReference[0].insert(610);
        datasetArrayReference[0].insert(630);
        datasetArrayReference[0].insert(620);
        datasetArrayReference[0].insert(680);
        datasetArrayReference[0].insert(690);
        datasetArrayReference[0].insert(710);
        datasetArrayReference[0].insert(730);
        datasetArrayReference[0].insert(740);
        datasetArrayReference[0].insert(770);

        datasetArrayReference[1] = new DataService();
        datasetArrayReference[1].insert(200);
        datasetArrayReference[1].insert(300);
        datasetArrayReference[1].insert(250);
        datasetArrayReference[1].insert(600);
        datasetArrayReference[1].insert(650);
        datasetArrayReference[1].insert(670);
        datasetArrayReference[1].insert(400);
        datasetArrayReference[1].insert(440);
        datasetArrayReference[1].insert(430);
        datasetArrayReference[1].insert(900);
        datasetArrayReference[1].insert(980);
        datasetArrayReference[1].insert(990);
        datasetArrayReference[1].insert(300);
        datasetArrayReference[1].insert(370);
        datasetArrayReference[1].insert(380);
        datasetArrayReference[1].insert(710);
        datasetArrayReference[1].insert(730);
        datasetArrayReference[1].insert(790);
        datasetArrayReference[1].insert(450);
        datasetArrayReference[1].insert(480);
        datasetArrayReference[1].insert(490);
        datasetArrayReference[1].insert(930);
        datasetArrayReference[1].insert(960);
        datasetArrayReference[1].insert(980);
    }

    public static void addAndSort(DataService dataService) {
        if (dataService.isEmpty())
            return;
        Node node = dataService.head;

        List<Integer> splitList = new ArrayList<>();
        System.out.println("--- Sorted Dataset ---");
        System.out.println("_______________________");
        while (node != null) {
            splitList.add(node.getData());
            node = node.next;
        }
        splitList.sort(Collections.reverseOrder());

        String timeGapName;
        if (splitList.size() == 24)
            timeGapName = "Month";
        else if (splitList.size() == 48)
            timeGapName = "15 days";
        else
            timeGapName = "10 days";
        for (int i = 0; i < splitList.size(); i++) {
            System.out.println(" | " + (i + 1) + ". " + timeGapName + " | " + splitList.get(i) + " | ");
            System.out.println(" -------------------");
        }
    }

    public static List<List<Integer>> addIntoList(DataService dataService) {
        Node node = dataService.head;
        List<Integer> splitList = new ArrayList<>();

        while (node != null) {
            splitList.add(node.getData());
            node = node.next;
        }

        List<Integer> head = splitList.subList(0, dataService.size / 2);
        List<Integer> tail = splitList.subList(dataService.size / 2, dataService.size);

        List<List<Integer>> list = new ArrayList<>();
        list.add(head);
        list.add(tail);
        return list;
    }

    public static List<Double> regressionPrediction(List<List<Integer>> list) {
        LinearRegression2 linearRegression2 = new LinearRegression2(list.get(0), list.get(1));

        String timeGapName;
        if (list.get(0).size() == 12)
            timeGapName = "Month";
        else if (list.get(0).size() == 24)
            timeGapName = "15 days";
        else
            timeGapName = "10 days";
        List<Double> predictionList = new ArrayList<>();
        System.out.println("\n  --------Regression Prediction----------");
        System.out.println("________________________________________________");
        for (int i = 0; i < list.get(0).size(); i++) {
            System.out.println("| " + (i + 1) + ". " + timeGapName + " Prediction | "
                    + linearRegression2.predict(list.get(0).get(i)) + " |");
            System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
            predictionList.add(linearRegression2.predict(list.get(0).get(i)));
        }

        for (int i = 0; i < list.get(1).size(); i++) {
            System.out.println("| " + (i + list.get(1).size() + 1) + ". " + timeGapName + " Prediction | "
                    + linearRegression2.predict(list.get(1).get(i)) + " |");
            System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
            predictionList.add(linearRegression2.predict(list.get(1).get(i)));
        }

        double regressionMSE = 0;
        for (int i = 0; i < list.size(); i++) {
            regressionMSE += (list.get(0).get(i) - predictionList.get(i))
                    * (list.get(0).get(i) - predictionList.get(i));
        }
        for (int i = 0; i < list.size(); i++) {
            regressionMSE += (list.get(1).get(i) - predictionList.get(i))
                    * (list.get(0).get(i) - predictionList.get(i));
        }

        System.out.println("MSE for Regression Analysis: " + regressionMSE / predictionList.size());
        double mse = regressionMSE / predictionList.size();
        predictionList.add(mse);
        return predictionList;
    }

    public static List<Double> deseasonalizedRegression(List<List<Integer>> list) {
        List<Double> predictionList = new ArrayList<>();
        List<Double> deseasonalize = new ArrayList<>();

        double ratioSum = 0;
        System.out.println("\n  --Deseasonalized Regression Prediction--");
        System.out.println("___________________________________________");
        for (int i = 0; i < list.get(0).size(); i++) {
            deseasonalize.add(((list.get(0).get(i) + list.get(1).get(i)) / 2.0));
            ratioSum += ((list.get(0).get(i) + list.get(1).get(i)) / 2.0);
        }

        ratioSum = ratioSum / list.get(0).size();
        List<Double> ratioList = new ArrayList<>();

        for (Double aDouble : deseasonalize) {
            ratioList.add(aDouble / ratioSum);
        }
        String timeGapName;
        if (deseasonalize.size() == 12)
            timeGapName = "Month";
        else if (deseasonalize.size() == 24)
            timeGapName = "15 days";
        else
            timeGapName = "10 days";

        double deseasonalizedMSE = 0;

        System.out.println(
                "--------Predictions for the next " + ((deseasonalize.size())) + " " + timeGapName + "--------");
        System.out.println("______________________________________________________");
        for (int i = 0; i < deseasonalize.size(); i++) {
            System.out.println("| " + (i + 1) + ". next " + timeGapName + " Prediction " + " |\t"
                    + deseasonalize.get(i) * ratioList.get(i) + " |");
            System.out.println("--------------------------------------------------");
            predictionList.add(deseasonalize.get(i) * ratioList.get(i));
            deseasonalizedMSE += ((list.get(1).get(i) - deseasonalize.get(i) * ratioList.get(i))
                    * ((list.get(1).get(i) - deseasonalize.get(i) * ratioList.get(i))));

        }

        deseasonalizedMSE = deseasonalizedMSE / list.get(0).size();
        System.out.println("MSE for Deseasonalized Regression: " + deseasonalizedMSE);
        predictionList.add(deseasonalizedMSE);
        return predictionList;
    }

    public static double findBestForecastAndMinSales(List<Double> list) {

        double min = list.get(0);
        for (Double aDouble : list) {
            if (aDouble < min) {
                min = aDouble;
            }
        }
        return min;
    }

    public static double findMaxSaleForecast(List<Double> list) {
        double max = list.get(0);
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > max) {
                max = list.get(i);
            }
        }
        return max;
    }
}