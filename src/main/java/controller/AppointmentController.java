package controller;

import classes.Appointment;
import database.BaseDatabase;
import exception.SunriseException;
import io.javalin.http.Context;
import repository.AppointmentRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentController {
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(BaseDatabase baseDatabase) throws SQLException {
        this.appointmentRepository = new AppointmentRepository(baseDatabase);
    }

    public void createAppointment(Context ctx){
        try{
            Appointment incomingAppointment = ctx.bodyAsClass(Appointment.class);

            //validate appointment
            incomingAppointment.validate();

            appointmentRepository.post(incomingAppointment);
           // System.out.println(result);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 201);
            response.put("message", "APPOINTMENT_CREATED_SUCCESS");
            response.put("data", null); // This will render as `null` in JSON

            ctx.status(201).json(response);

        }catch (SunriseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", e.getStatusCode());
            response.put("message", e.getMessage());
            response.put("errors", Map.of(e.getField(), e.getValue()));

            ctx.status(e.getStatusCode()).json(response);

        }catch (Exception e) {
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public void getAppointmentById(Context ctx){
        try{
            String id = ctx.pathParamAsClass("id", String.class).get();
            System.out.println(id);

            Appointment appointment = appointmentRepository.getById(id);

            if (appointment != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 200);
                response.put("message", "APPOINTMENT_FETCHED");
                response.put("data", appointment);

                ctx.status(200).json(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 404);
                response.put("message", "APPOINTMENT_NOT_FOUND");
                response.put("data", null);

                ctx.status(404).json(response);
            }

        }catch (SunriseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", e.getStatusCode());
            response.put("message", e.getMessage());
            response.put("errors", Map.of(e.getField(), e.getValue()));

            ctx.status(e.getStatusCode()).json(response);
        } catch (Exception e) {
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public void getAllAppointments(Context ctx) {

        String searchTerm = ctx.queryParam("searchTerm");

        try {
            List<Appointment> appointments = appointmentRepository.getAll(searchTerm);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", "APPOINTMENTS_FETCHED");
            response.put("data", appointments); // Returns the list of appointments as JSON

            ctx.status(200).json(response);

        } catch (Exception e) {
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}
