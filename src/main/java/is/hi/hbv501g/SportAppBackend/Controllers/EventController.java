package is.hi.hbv501g.SportAppBackend.Controllers;

import is.hi.hbv501g.SportAppBackend.Persistence.Entities.Event;
import is.hi.hbv501g.SportAppBackend.Persistence.Entities.User;
import is.hi.hbv501g.SportAppBackend.Services.EventService;
import is.hi.hbv501g.SportAppBackend.Services.SportService;
import is.hi.hbv501g.SportAppBackend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***********************************************************
 * Nafn: Brynjólfur Steingrímsson
 * Email: brs26@hi.is
 *
 * Lýsing:
 *
 *
 ***********************************************************/
@RestController
public class EventController {

    private final EventService eventService;
    private final SportService sportService;
    private final UserService userService;

    @Autowired
    public EventController(EventService eventService, SportService sportService, UserService userService) {
        this.eventService = eventService;
        this.sportService = sportService;
        this.userService = userService;
    }

    @RequestMapping(value = "/event/{id}", method = RequestMethod.GET)
    public Event getEventById(@PathVariable("id") Long id) {
        Event event = eventService.findEventById(id);
        return event;
    }

    @GetMapping("/allEvents")
    public List<Event> getAllEvents() {
        return eventService.findAll();
    }

    @PostMapping("/saveEvent")
    public Event saveEvent(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String sport,
            @RequestParam String startingDate,
            @RequestParam String image) {
        Event event = new Event();
        event.setEventName(title);
        event.setEventDescription(description);
        event.setSport(sport);
        event.setEventStartTime(startingDate);
        event.setImage(image);
        eventService.save(event);
        return event;
    }

    @PostMapping("/event/{id}")
    public String subscribeToEvent(@PathVariable("id") Long id, @RequestParam Long userId) {
        Event event = eventService.findEventById(id);
        User user = userService.findByID(userId);
        event.addSubscriber(user);
        eventService.save(event);
        return "Successfully subscribed to event!";
    }

    @GetMapping("/userInfo/{userId}/myEvents")
    public List<Event> getMyEvents(@PathVariable String userId) {
        User user = userService.findByID(Long.parseLong(userId));
        return eventService.findByUser(user);
    }

    @RequestMapping(value = "/home/{sport}/events", method = RequestMethod.GET)
    public List<Event> goToEvents(@PathVariable("sport") String sport, Model model) {
        if (!sportService.isSport(sport)) {
            return null;
        }
        return eventService.findBySport(sport);
    }

    @RequestMapping(value = "/home/{sport}/events/edit/{id}", method = RequestMethod.POST)
    public String editEvent(@PathVariable("id") Long id, Event event, Model model) {
        //takes in object and saves changes
        event.setID(id);
        eventService.save(event);
        return "redirect:/home/{sport}/events/edit";
    }

//    @RequestMapping(value = "/home/{sport}/events/save", method = RequestMethod.POST)
//    public String saveEvent(Event event, Model model) {
//        //takes in object and saves changes
//        System.err.println("Today's date: " + event.geteventStartTime());
//        eventService.save(event);
//        return "redirect:/home/{sport}/events";
//    }

    @RequestMapping(value = "/home/{sport}/events/delete/{id}", method = RequestMethod.GET)
    public String deleteEvent(@PathVariable("id") Long id, Model model) {
        //takes in object and saves changes
        eventService.delete(id);
        return "redirect:/home/{sport}/events/edit";
    }
}
