package net.hackyourfuture.tickettrackingsystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.hackyourfuture.tickettrackingsystem.dto.responses.ProjectSummary;
import net.hackyourfuture.tickettrackingsystem.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectSummary> getAllProjects() {
        return service.getAllProjectSummaries();
    }
}