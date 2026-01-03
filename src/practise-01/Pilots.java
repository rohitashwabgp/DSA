import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

class Pilot {

    String availableTime;

}


class Schedule {
    public String scheduleTime;
    public String insType;
    public List<Pilot> avaiablePilots;

    public String getScheduleTime() {
        return this.scheduleTime;
    }

}
public class Pilots {
  //4-6 5-6
  
    public List<Schedule> getTrips(List<Schedule> trips, List<Pilot> pilots) {
       List<Schedule> mappedTripPilots = trips.stream()
       .map(data-> mapAvailablePilots(data, pilots)).collect(Collectors.toList());

        return mappedTripPilots;
    }


    private Schedule mapAvailablePilots(Schedule data, List<Pilot> pilots) {

        data.setAvaiablePilots(pilots.stream().filter(plt-> plt.availableTime.equals(data.scheduleTime)).toList());
         return data;
    }


    public static void main(String args[]) {

    }
}