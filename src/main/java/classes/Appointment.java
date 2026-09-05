package classes;

import exception.SunriseException;
import org.w3c.dom.Text;

public class Appointment {




    private long id;
    private String appoNum;
    private String patientName;
    private String treatmentType;
    private Integer age;
    private String address;
    private String contactNum;
    private String dentist;
    private String appoDateTime;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    public String getAppoNum() {return appoNum;}
    public void setAppoNum(String appoNum) {this.appoNum = appoNum;}
    public String getPatientName() {return patientName;}
    public void setPatientName(String patientName) {this.patientName = patientName;}
    public String getTreatmentType() {return treatmentType;}
    public void setTreatmentType(String treatmentType) {this.treatmentType = treatmentType;}
    public Integer getAge() {return age;}
    public void setAge(Integer age) {this.age = age;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public String getContactNum() {return contactNum;}
    public void setContactNum(String contactNum) {this.contactNum = contactNum;}
    public String getDentist() {return dentist;}
    public void setDentist(String dentist) {this.dentist = dentist;}
    public String getAppoDateTime() {return appoDateTime;}
    public void setAppoDateTime(String appoDateTime) {this.appoDateTime = appoDateTime;}


    public void validate() throws SunriseException {

        if(patientName == null || patientName.trim().isEmpty()){
            throw new SunriseException(400, "patientName", "patientName cannot be null or empty", "Validation failed");
        }

        if(appoDateTime == null || appoDateTime.trim().isEmpty()){
            throw new SunriseException(400, "appoDateTime", "appointment Date&Time cannot be null or empty", "Validation failed");
        }

        if(age == null || age <= 0){
            throw new SunriseException(400, "age", "age cannot be empty or 0", "Validation failed");
        }

        if(contactNum == null || contactNum.trim().isEmpty()) {
            throw new SunriseException(400, "contactNum", "contactNumber cannot be null or empty", "Validation failed");
        }

        if (!contactNum.trim().matches("^\\d{10}$")) {
            throw new SunriseException(400, "contactNum", "contactNumber must be exactly 10 digits", "Validation failed");
        }

        if(dentist == null || dentist.trim().isEmpty()){
            throw new SunriseException(400, "dentistName", "dentistName cannot be null or empty", "Validation failed");
        }

        if(treatmentType == null || treatmentType.trim().isEmpty()){
            throw new SunriseException(400, "treatmentType", "treatmentType cannot be null or empty", "Validation failed");
        }

    }
}
