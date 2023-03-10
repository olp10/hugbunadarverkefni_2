package is.hi.hbv501g.SportAppBackend.Controllers;

import is.hi.hbv501g.SportAppBackend.Persistence.Entities.*;
import is.hi.hbv501g.SportAppBackend.Persistence.Entities.Thread;
import is.hi.hbv501g.SportAppBackend.Services.CommentService;
import is.hi.hbv501g.SportAppBackend.Services.SportService;
import is.hi.hbv501g.SportAppBackend.Services.ThreadService;
import is.hi.hbv501g.SportAppBackend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ThreadController {
    private final ThreadService threadService;
    private final UserService userService;
    private final CommentService commentService;

    // Má Deleta seinna: Kemur í veg fyrir að dummy gögn séu endalaust búin til
    private int dummyTeljari = 0;

    @Autowired
    public ThreadController(ThreadService threadService, SportService sportService, UserService userService, CommentService commentService) {
        this.commentService = commentService;
        this.threadService = threadService;
        this.userService = userService;
    }

//    @GetMapping("/home/{sport}/thread/{id}")
//    public Thread getThreadById(@PathVariable Long id) {
//        return threadService.findThreadById(id);
//    }
//
    @GetMapping("/thread/{id}")
    public Thread getThreadById(@PathVariable Long id) {
        Thread thread = threadService.findThreadById(id);
        return thread;
    }

    @GetMapping("/allThreads")
    public List<Thread> getAllThreads() {
//        createDummyData();
//        System.out.println("allThreads was called!");
        return threadService.findAllThreads();
    }

    @PostMapping("/newComment")
    public String addComment(@RequestParam String userId, @RequestParam String commentBody, @RequestParam String threadId) {
        User poster = userService.findByID(Long.valueOf(userId));
        Thread threadPostedIn = threadService.findThreadById(Long.valueOf(threadId));
        Comment newComment = new Comment(poster.getUsername(), commentBody, threadPostedIn);
        threadService.addComment(newComment, threadPostedIn);
        return "Comment successfully posted!";
    }

    @DeleteMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable("id") String id) {
        try {
            System.out.println("Comment Id: " + id);
            Comment commentToDelete = commentService.findCommentById(Long.valueOf(id));
            commentService.delete(commentToDelete);
        } catch (Exception e) {
            System.out.println("Error deleting comment: " + e.getMessage());
        }
        return "Comment successfully deleted!";
    }

    @RequestMapping(value = "/home/{sport}/createThread", method = RequestMethod.POST)
    public String addThread(HttpSession session, @PathVariable("sport") String sport, String header, String body, String username, String pinned, Model model) {
        //tekur inn thread og sendir hann í repository svo redirectum við til thread með id
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        Thread thread;
        if (sessionUser == null) {
            thread = threadService.save(new Thread("anonymous", header, body, sport));
        }
        else {
            thread = threadService.save(new Thread(sessionUser.getUsername(), header, body, sport));
        }
        if (pinned != null) {
            thread.setPinned(true);
            thread = threadService.save(thread);
        }

        Long id = thread.getID();
        return "redirect:/home/{sport}/thread/"+id;
    }

    @RequestMapping(value = "/home/{sport}/thread/{id}/editThread", method = RequestMethod.GET)
    public String editThread(@PathVariable("id") String id, Model model) {
        model.addAttribute("header", threadService.findThreadById(Long.parseLong(id)).getHeader());
        model.addAttribute("body", threadService.findThreadById(Long.parseLong(id)).getBody());
        return "editThread";
    }

    @RequestMapping(value = "/home/{sport}/thread/{id}/editThread", method = RequestMethod.POST)
    public String postEditThread(@RequestParam String header, String body, @PathVariable Long id) {
        return "redirect:/home/{sport}/thread/"+id;
    }

//    @RequestMapping(value = "/home/{sport}/thread/{id}", method = RequestMethod.POST)
//    public String addComment(@PathVariable("id") Long id, String comment, HttpSession session, Model model) {
//        User poster = (User) session.getAttribute("LoggedInUser");
//        long posterID = poster.getID();
//        session.removeAttribute("LoggedInUser");
//        poster = userService.findByID(posterID);
//
//        Thread targetThread = threadService.findThreadById(id);
//        Comment newComment = new Comment(poster, comment, targetThread);
//        threadService.addComment(newComment, targetThread);
//        session.setAttribute("LoggedInUser", poster);
//        return "redirect:/home/{sport}/thread/{id}/";
//    }

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

    // Má henda? Dummydata búið til í NavController
    public void createDummyData() {
        if (dummyTeljari <= 0) {
            User admin = new User("admin","admin",true);
            userService.save(admin);
            Thread tips1 = new Thread("Íþróttasíða", "Beginner tips & FAQ", "Here are some useful tips..", "badminton");
            Thread tips2 = new Thread("Íþróttasíða", "Beginner tips & FAQ", "Here are some useful tips..", "pilukast");
            Thread tips3 = new Thread("Íþróttasíða", "Beginner tips & FAQ", "Here are some useful tips..", "Extreme Ironing");
            Comment comment = new Comment("admin", "Þetta er flottur þráður!", tips1);
            threadService.addComment(comment,tips1);
            tips1.setPinned(true);
            tips2.setPinned(true);
            tips3.setPinned(true);
            threadService.save(tips1);
            threadService.save(tips2);
            threadService.save(tips3);

            for (int i = 0; i < 10; i++) {
                threadService.save(new Thread("Jon", "Dummy Thread " + i, "Dummy Body", "badminton"));
                threadService.save(new Thread("Palli", "Dummy Thread " + i, "Dummy Body", "pilukast"));
                threadService.save(new Thread("Ragnar", "Dummy Thread " + i, "Dummy Body", "Extreme Ironing"));

            }
            dummyTeljari++;
        }
    }
}
