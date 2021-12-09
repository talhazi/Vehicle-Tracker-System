import java.io.*;

public class CSVHandler {

    private final String fileName;

    public CSVHandler(String fileName) {
        this.fileName = fileName;
    }

    public void createVehicleTrackerCsv() throws IOException {
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

    public void updateCsv(String emlFileName, VehicleInfo vehicleInfo) throws IOException {
        FileWriter csvWriter = new FileWriter(this.fileName,true);
        addData(csvWriter, emlFileName, vehicleInfo);
    }

    public String getFileName() {
        return fileName;
    }

    private void addHeader(FileWriter csvWriter) throws IOException {
        csvWriter.append("Name of EML").append(",");
        csvWriter.append("Employee Name").append(",");
        csvWriter.append("Car License Plate Number").append(",");
        csvWriter.append("Date & Time").append(",");
        csvWriter.append("Odometer Read (km)").append(",");
        csvWriter.append("Parking Location").append("\n");
        csvWriter.flush();
        csvWriter.close();
    }

    private void addData(FileWriter csvWriter, String emlFileName, VehicleInfo vehicleInfo) throws IOException {
        csvWriter.append(emlFileName).append(",");
        csvWriter.append(vehicleInfo.getEmployeeName()).append(",");
        csvWriter.append(vehicleInfo.getLicensePlate()).append(",");
        csvWriter.append(vehicleInfo.getDateAndTime()).append(",");
        csvWriter.append(vehicleInfo.getOdometer()).append(",");
        csvWriter.append(vehicleInfo.getLocation()).append("\n");
        csvWriter.flush();
        csvWriter.close();
    }
}
