package is.hi.hbv501g.hugbunadarverkefni1.Controllers;

import is.hi.hbv501g.hugbunadarverkefni1.Persistence.Entities.Comment;
import is.hi.hbv501g.hugbunadarverkefni1.Persistence.Entities.Event;
import is.hi.hbv501g.hugbunadarverkefni1.Persistence.Entities.Thread;
import is.hi.hbv501g.hugbunadarverkefni1.Persistence.Entities.User;
import is.hi.hbv501g.hugbunadarverkefni1.Services.SportService;
import is.hi.hbv501g.hugbunadarverkefni1.Services.ThreadService;
import is.hi.hbv501g.hugbunadarverkefni1.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class ThreadController {
    private final ThreadService threadService;
    private final SportService sportService;
    private final UserService userService;

    @Autowired
    public ThreadController(ThreadService threadService, SportService sportService, UserService userService){
        this.threadService = threadService;
        this.sportService = sportService;
        this.userService = userService;
    }

    @RequestMapping(value = "/home/{sport}/createThread", method = RequestMethod.GET)
    public String newThread(@PathVariable("sport") String sport, Model model) {
        List<String> sports = sportService.findAllSports();
        List<Event> events = sportService.findAllEventsBySport(sport);
        model.addAttribute("events", events);
        model.addAttribute("sports", sports);
        return "createThread";
    }

    @RequestMapping(value = "/home/{sport}/createThread", method = RequestMethod.POST)
    public String addThread(@PathVariable("sport") String sport, String header, String body, User user, Model model) {
        //tekur inn thread og sendir hann í repository svo redirectum við til thread með id
        Thread thread = threadService.save(new Thread(user.getUsername(), header, body, sport));
        Long id = thread.getID();
        return "redirect:/home/{sport}/thread/"+id;
    }

    @RequestMapping(value = "/home/{sport}/thread/{id}", method = RequestMethod.POST)
    public String addComment(@PathVariable("id") Long id, String comment) {
        threadService.addComment(comment, threadService.findThreadById(id));
        return "redirect:/home/{sport}/thread/{id}/";
    }


    @RequestMapping(value = "/home/{sport}/thread/{id}/comment/stuff", method = RequestMethod.POST)
    public String deleteComment(Model model) {
        //duno
        return "redirect:/home/{sport}/thred/{id}";
    }

    @RequestMapping(value = "/home/{sport}/thread/{id}/delete", method = RequestMethod.POST)
    public String deleteThread(Model model) {
        //tekur inn thred id og deletes thred svo redirectum við á sport
        return "redirect:/home/{sport}";
    }



}
