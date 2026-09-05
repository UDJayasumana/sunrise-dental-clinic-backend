package response;

import classes.Appointment;

import java.util.List;

public class PaginatedAppointmentsResponse {
    private List<Appointment> appointments;
    private int totalCount;

    // Constructors, Getters, and Setters
    public PaginatedAppointmentsResponse(List<Appointment> appointments, int totalCount) {
        this.appointments = appointments;
        this.totalCount = totalCount;
    }

    public List<Appointment> getAppointments() { return appointments; }
    public int getTotalCount() { return totalCount; }
}
