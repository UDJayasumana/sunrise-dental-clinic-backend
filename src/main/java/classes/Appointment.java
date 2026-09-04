package classes;

import exception.SunriseException;
import org.w3c.dom.Text;

public class Appointment {

    private long appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private String dentistName;
    private String treatmentType;

    public long getAppointmentNumber() {return appointmentNumber;}
    public void setAppointmentNumber(long appointmentNumber) {this.appointmentNumber = appointmentNumber;}
    public String getPatientName() {return patientName;}
    public void setPatientName(String patientName) {this.patientName = patientName;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public String getContactNumber() {return contactNumber;}
    public void setContactNumber(String contactNumber) {this.contactNumber = contactNumber;}
    public String getDentistName() {return dentistName;}
    public void setDentistName(String dentistName) {this.dentistName = dentistName;}
    public String getTreatmentType() {return treatmentType;}
    public void setTreatmentType(String treatmentType) {this.treatmentType = treatmentType;}

    public void validate() throws SunriseException {

        if(patientName == null || patientName.trim().isEmpty()){
            throw new SunriseException(400, "patientName", "patientName cannot be null or empty", "Validation failed");
        }

        if(contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new SunriseException(400, "contactNumber", "contactNumber cannot be null or empty", "Validation failed");
        }

        if (!contactNumber.trim().matches("^\\d{10}$")) {
            throw new SunriseException(400, "contactNumber", "contactNumber must be exactly 10 digits", "Validation failed");
        }

        if(dentistName == null || dentistName.trim().isEmpty()){
            throw new SunriseException(400, "dentistName", "dentistName cannot be null or empty", "Validation failed");
        }

        if(treatmentType == null || treatmentType.trim().isEmpty()){
            throw new SunriseException(400, "treatmentType", "treatmentType cannot be null or empty", "Validation failed");
        }

    }
}
