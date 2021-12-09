public final class VehicleInfo {
    private String employeeName;
    private String licensePlate;
    private String odometer;
    private String dateAndTime;
    private String location;

    VehicleInfo(){
    }

    String getEmployeeName() {
        return employeeName;
    }

    void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    String getLicensePlate() {
        return licensePlate;
    }

    void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    String getOdometer() {
        return odometer;
    }

    void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    String getDateAndTime() {
        return dateAndTime;
    }

    void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location;
    }
}
