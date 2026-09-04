package repository;

import classes.Appointment;
import database.BaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class AppointmentRepository {
    private final BaseDatabase database;
    private final Connection connection;

    public AppointmentRepository(BaseDatabase database) throws SQLException{
        this.database = database;
        connection = database.getConnection();
    }

    public void post(Appointment appointment) throws Exception{

        String sql = "INSERT INTO appointments (patient_name, appo_date_time, treatment_type, age, address, contact_num, dentist) VALUES (?, ?::timestamp, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = connection.prepareStatement(sql, RETURN_GENERATED_KEYS)){

           // stmt.setString(1, appointment.getAppoNum());
            stmt.setString(1, appointment.getPatientName());
            stmt.setString(2, appointment.getAppoDateTime());
            stmt.setString(3, appointment.getTreatmentType());
            stmt.setInt(4, appointment.getAge());
            stmt.setString(5, appointment.getAddress());
            stmt.setString(6, appointment.getContactNum());
            stmt.setString(7, appointment.getDentist());

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);

                    String appoNum = String.format("APT-%05d", generatedId);
                    String updateSql = "UPDATE appointments SET appo_num = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setString(1, appoNum);
                        updateStmt.setInt(2, generatedId);
                        updateStmt.executeUpdate();
                    }
                }
            }

            System.out.println("Inserted successfully with custom appointment number!");

        }catch (Exception e){
            throw new IllegalArgumentException("appointment saving failed" + e.getMessage());
        }


    }

    public Appointment getById(String appoNum)throws Exception{
        String sql = "SELECT id, appo_num, patient_name, appo_date_time, treatment_type, age, address, contact_num, dentist " +
                      "FROM appointments WHERE appo_num = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            // Set the ID parameter
            pstmt.setString(1, appoNum);

            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    Appointment appointment = new Appointment();

                    // Map database columns to your Appointment object setters
                    appointment.setId(rs.getLong("id"));
                    appointment.setAppoNum(rs.getString("appo_num"));
                    appointment.setPatientName(rs.getString("patient_name"));
                    appointment.setAppoDateTime(rs.getString("appo_date_time"));
                    appointment.setTreatmentType(rs.getString("treatment_type"));
                    appointment.setAge(rs.getInt("age"));
                    appointment.setAddress(rs.getString("address"));
                    appointment.setContactNum(rs.getString("contact_num"));
                    appointment.setDentist(rs.getString("dentist"));

                    return appointment;
                }
            }
        }catch (Exception e){
            throw new IllegalArgumentException("appointment saving failed");
        }
        return null;
    }

    public List<Appointment> getAll() throws Exception{
        String sql = "SELECT id, appo_num, patient_name, appo_date_time, treatment_type, age, address, contact_num, dentist FROM appointments";

        List<Appointment> appointments = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Appointment appointment = new Appointment();

                    // Map database columns to your Appointment object setters
                    appointment.setId(rs.getLong("id"));
                    appointment.setAppoNum(rs.getString("appo_num"));
                    appointment.setPatientName(rs.getString("patient_name"));
                    appointment.setAppoDateTime(rs.getString("appo_date_time"));
                    appointment.setTreatmentType(rs.getString("treatment_type"));
                    appointment.setAge(rs.getInt("age"));
                    appointment.setAddress(rs.getString("address"));
                    appointment.setContactNum(rs.getString("contact_num"));
                    appointment.setDentist(rs.getString("dentist"));

                    appointments.add(appointment);
                }
            }
        }catch (Exception e){
            throw new IllegalArgumentException("appointment saving failed");
        }
        return appointments;
    }
}
