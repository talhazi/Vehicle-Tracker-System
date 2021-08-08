import java.io.*;

public final class VehicleInfo {
    String employee_name;
    String license_plate;
    String odometer;
    String date_time;
    String location;
    final String log_file_name;

    VehicleInfo(){
        employee_name = "";
        license_plate = "";
        odometer = "";
        date_time = "";
        location = "";
        log_file_name = "log.csv";
    }

    public void print(){
        System.out.println("Name: " + employee_name);
        System.out.println("License Plate: " + license_plate);
        System.out.println("Date & Time: " + date_time);
        System.out.println("Odometer: " + odometer);
        System.out.println("Location: " + location);
    }

    public void create_csv(String eml_file_name) throws IOException {
        if (!(new File(eml_file_name).exists())) {
            FileWriter csvWriter = new FileWriter(eml_file_name, true);
            addHeader(csvWriter);
        }
    }

    public void update_csv(String eml_file_name) throws IOException {
        FileWriter csvWriter = new FileWriter(log_file_name,true);
        addData(csvWriter, eml_file_name);
    }

    public boolean emlAlreadyExist(String log_file_name, String eml_file_name) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(log_file_name));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if (data[0].equals(eml_file_name)) {
                return true;
            }
        }
        return false;
    }

    private void addHeader(FileWriter csvWriter) throws IOException {
        csvWriter.append("Name of EML");
        csvWriter.append(",");
        csvWriter.append("Employee Name");
        csvWriter.append(",");
        csvWriter.append("Car License Plate Number");
        csvWriter.append(",");
        csvWriter.append("Date & Time");
        csvWriter.append(",");
        csvWriter.append("Odometer Read (km)");
        csvWriter.append(",");
        csvWriter.append("Parking Location");
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }

    private void addData(FileWriter csvWriter, String eml_file_name) throws IOException {
        csvWriter.append(eml_file_name);
        csvWriter.append(",");
        //cleaning the fields after each update (for safe)
        csvWriter.append(employee_name);
        employee_name = "";
        csvWriter.append(",");
        csvWriter.append(license_plate);
        license_plate = "";
        csvWriter.append(",");
        csvWriter.append(date_time);
        date_time = "";
        csvWriter.append(",");
        csvWriter.append(odometer);
        odometer = "";
        csvWriter.append(",");
        csvWriter.append(location);
        location = "";
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }

}
