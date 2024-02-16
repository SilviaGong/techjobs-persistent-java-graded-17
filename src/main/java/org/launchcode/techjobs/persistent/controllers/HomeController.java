package org.launchcode.techjobs.persistent.controllers;

import jakarta.validation.Valid;
import org.launchcode.techjobs.persistent.models.Employer;
import org.launchcode.techjobs.persistent.models.Job;
import org.launchcode.techjobs.persistent.models.Skill;
import org.launchcode.techjobs.persistent.models.data.EmployerRepository;
import org.launchcode.techjobs.persistent.models.data.JobRepository;
import org.launchcode.techjobs.persistent.models.data.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by LaunchCode
 */
@Controller
public class HomeController {
    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    JobRepository jobRepository;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "MyJobs");
        model.addAttribute("jobs", jobRepository.findAll());
        return "index";
    }

    @GetMapping("add")
    public String displayAddJobForm(Model model) {
        model.addAttribute("title", "Add Job");
        model.addAttribute("employers", employerRepository.findAll());
        model.addAttribute("skills", skillRepository.findAll());
        model.addAttribute(new Job());
        return "add";
    }

    @PostMapping("add")
    public String processAddJobForm(@ModelAttribute @Valid Job newJob,
                                    Errors errors, Model model, @RequestParam int employerId, @RequestParam(required = false) List<Integer> skills) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Job");
//            // Add employers and skills to the model again
//            model.addAttribute("employers", employerRepository.findAll());
//            model.addAttribute("skills", skillRepository.findAll());
//            model.addAttribute("errorMessage", "There are validation errors");
            return "add";
        }
        Optional<Employer> employerOptional = employerRepository.findById(employerId);
        if (employerOptional.isPresent()) {
            Employer employer = (Employer) employerOptional.get();
            newJob.setEmployer(employer);
        }
        // Retrieve skills by their IDs
        if (skills != null) {
            List<Skill> skillObjs = (List<Skill>) skillRepository.findAllById(skills);
            newJob.setSkills(skillObjs);
        }
        jobRepository.save(newJob);
        model.addAttribute("jobs",jobRepository.findAll());
        return "redirect:view/" + newJob.getId();

    }

    @GetMapping("view/{jobId}")
    public String displayViewJob(Model model, @PathVariable int jobId) {
        Optional<Job> result = jobRepository.findById(jobId);
        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Job Id" + jobId);
        } else {
            Job job = result.get();
            model.addAttribute("title", "Job:" + job.getName());
            model.addAttribute("job", job);
        }

        return "view";
    }

}
