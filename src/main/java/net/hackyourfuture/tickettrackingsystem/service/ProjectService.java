package net.hackyourfuture.tickettrackingsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import net.hackyourfuture.tickettrackingsystem.dto.responses.ProjectSummary;
import net.hackyourfuture.tickettrackingsystem.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public List<ProjectSummary> getAllProjectSummaries() {
        return repository.fetchAllProjectSummaries();
    }
}
