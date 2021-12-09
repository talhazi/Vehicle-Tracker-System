import java.io.*;

 /**
 * >>> PLEASE READ THE README FILE BEFORE RUNNING THE PROGRAM <<<
 *
 * Objective:
 * Extract data from an EML file (with attached image) to a CSV "log" file
 * The extracting data: Employee Name -> email sender
 *                      Car License Plate Number -> appears as QR code in the image
 *                      Date & time -> metadata of the image
 *                      Odometer Read -> marked in a red rectangle in the image
 *                      Parking Location -> metadata of the image
 *
 * @author: Tal Hazi
 * @version: 1.0
 *
 * Last Updated Date: Aug 5, 2021
 */

public final class VehicleTracker {

    static VehicleInfo info = new VehicleInfo();  //will hold the required data

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please provide command line argument for EML file path");
            return;
        }
        CSVHandler csvHandler = new CSVHandler("log.csv");
        csvHandler.createCsv();
        readEmlFiles(args, csvHandler);
    }

    private static void readEmlFiles(String[] emlPaths, CSVHandler csvHandler) throws Exception {
        for (String emlPath : emlPaths){
            System.out.println(">> " + emlPath);
            if (!emlPath.endsWith(".eml") | !(new File(emlPath).exists())) {
                System.out.println("does not read! you should provide valid EML file path\n");
                continue;
            }
            if (!csvHandler.emlAlreadyExist(csvHandler.getFileName() ,emlPath)){
                EMLHandler emlHandler = new EMLHandler();
                emlHandler.parseFile(emlPath);
                csvHandler.updateCsv(emlPath);
                System.out.println("Program finished, log file has been updated successfully!\n");
                continue;
            }
            System.out.println("Program finished, this EML file already been read before :)\n");
        }
    }

}
