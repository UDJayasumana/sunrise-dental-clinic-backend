package repository;

import classes.Appointment;
import database.BaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {
    private final BaseDatabase database;
    private final Connection connection;

    public AppointmentRepository(BaseDatabase database) throws SQLException{
        this.database = database;
        connection = database.getConnection();
    }

    public void post(Appointment appointment) throws Exception{

        String sql = "INSERT INTO appointments (patient_name, address, contact_number, dentist_name, treatment_type) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, appointment.getPatientName());
            stmt.setString(2, appointment.getAddress());
            stmt.setString(3, appointment.getContactNumber());
            stmt.setString(4, appointment.getDentistName());
            stmt.setString(5, appointment.getTreatmentType());
            stmt.executeUpdate();
            System.out.println("Inserted successfully!");

        }catch (Exception e){
            throw new IllegalArgumentException("appointment saving failed");
        }


    }

    public Appointment getById(long appointmentNumber)throws Exception{
        String sql = "SELECT appointment_number, patient_name, address, contact_number, dentist_name, treatment_type, created_at " +
                      "FROM appointments WHERE appointment_number = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            // Set the ID parameter
            pstmt.setLong(1, appointmentNumber);

            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    Appointment appointment = new Appointment();

                    // Map database columns to your Appointment object setters
                    appointment.setAppointmentNumber(rs.getLong("appointment_number"));
                    appointment.setPatientName(rs.getString("patient_name"));
                    appointment.setAddress(rs.getString("address"));
                    appointment.setContactNumber(rs.getString("contact_number"));
                    appointment.setDentistName(rs.getString("dentist_name"));
                    appointment.setTreatmentType(rs.getString("treatment_type"));

                    // If you have a setter for the timestamp/created_at, you can map it too:
                    // appointment.setCreatedAt(rs.getTimestamp("created_at"));

                    return appointment;
                }
            }
        }catch (Exception e){
            throw new IllegalArgumentException("appointment saving failed");
        }
        return null;
    }

    public List<Appointment> getAll() throws Exception{
        String sql = "SELECT appointment_number, patient_name, address, contact_number, dentist_name, treatment_type, created_at FROM appointments";

        List<Appointment> appointments = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setAppointmentNumber(rs.getLong("appointment_number"));
                    appointment.setPatientName(rs.getString("patient_name"));
                    appointment.setAddress(rs.getString("address"));
                    appointment.setContactNumber(rs.getString("contact_number"));
                    appointment.setDentistName(rs.getString("dentist_name"));
                    appointment.setTreatmentType(rs.getString("treatment_type"));

                    appointments.add(appointment);
                }
            }
        }catch (Exception e){
            throw new IllegalArgumentException("appointment saving failed");
        }
        return appointments;
    }
}
