/**
 * >>> PLEASE READ THE README FILE BEFORE RUNNING THE PROGRAM <<<
 *
 * @author: Tal Hazi
 * @version: 1.0
 * @lastUpdatedDate: Aug 5, 2021
 */

public final class VehicleTracker {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please provide command line argument for EML file path");
            return;
        }
        CSVHandler csvHandler = new CSVHandler("log.csv");
        csvHandler.createVehicleTrackerCsv();
        EMLHandler emlHandler = new EMLHandler();
        emlHandler.readEmlFiles(args, csvHandler);
    }

}
