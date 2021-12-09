import java.io.*;

public class CSVHandler {

    private final String fileName;

    public CSVHandler(String fileName) {
        this.fileName = fileName;
    }

    public void createCsv() throws IOException {
        if (!(new File(fileName).exists())) {
            FileWriter csvWriter = new FileWriter(fileName, true);
            addHeader(csvWriter);
        }
    }

    public boolean emlAlreadyExist(String logFileName, String emlFileName) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(logFileName));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if (data[0].equals(emlFileName)) {
                return true;
            }
        }
        return false;
    }

    public void updateCsv(String emlFileName) throws IOException {
        FileWriter csvWriter = new FileWriter(this.fileName,true);
        addData(csvWriter, emlFileName);
    }

    public String getFileName() {
        return fileName;
    }

    private void addHeader(FileWriter csvWriter) throws IOException {
        csvWriter.append("Name of EML,");
        csvWriter.append("Employee Name,");
        csvWriter.append("Car License Plate Number,");
        csvWriter.append("Date & Time,");
        csvWriter.append("Odometer Read (km),");
        csvWriter.append("Parking Location");
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }

    private void addData(FileWriter csvWriter, String emlFileName) throws IOException {
        csvWriter.append(emlFileName).append(",");
        //cleaning the fields after each update
        csvWriter.append(VehicleTracker.info.employee_name).append(",");
        VehicleTracker.info.employee_name = "";
        csvWriter.append(VehicleTracker.info.license_plate).append(",");
        VehicleTracker.info.license_plate = "";
        csvWriter.append(VehicleTracker.info.date_time).append(",");
        VehicleTracker.info.date_time = "";
        csvWriter.append(VehicleTracker.info.odometer).append(",");
        VehicleTracker.info.odometer = "";
        csvWriter.append(VehicleTracker.info.location);
        VehicleTracker.info.location = "";
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }
}
