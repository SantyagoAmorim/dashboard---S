package com.agency.dashboard.service;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.CreativeRequest;
import com.agency.dashboard.domain.RequestStatus;
import com.agency.dashboard.repo.CreativeRequestRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final CreativeRequestRepository repo;

    public MetricsService(CreativeRequestRepository repo) {
        this.repo = repo;
    }

    public record MonthWindow(LocalDateTime start, LocalDateTime end) {}

    public MonthWindow window(LocalDate month) {
        LocalDate first = month.withDayOfMonth(1);
        LocalDate next = first.plusMonths(1);
        return new MonthWindow(first.atStartOfDay(), next.atStartOfDay());
    }

    public long totalRequested(LocalDate month) {
        MonthWindow w = window(month);
        return repo.countByCreatedAtBetween(w.start(), w.end());
    }

    public long totalDelivered(LocalDate month) {
        MonthWindow w = window(month);
        return repo.countByStatusAndDeliveredAtBetween(RequestStatus.ENTREGUE, w.start(), w.end());
    }

    public long totalOpen(LocalDate month) {
        MonthWindow w = window(month);
        // “pendentes do mês” = criados no mês e ainda não ENTREGUE
        return repo.countByStatusNotAndCreatedAtBetween(RequestStatus.ENTREGUE, w.start(), w.end());
    }

    public double avgLeadTimeHours(LocalDate month) {
        MonthWindow w = window(month);

        var delivered = repo.findByCreatedAtBetween(w.start(), w.end()).stream()
                .filter(r -> r.getStatus() == RequestStatus.ENTREGUE && r.getDeliveredAt() != null)
                .toList();

        if (delivered.isEmpty()) return 0;

        double avg = delivered.stream()
                .mapToLong(r -> Duration.between(r.getCreatedAt(), r.getDeliveredAt()).toHours())
                .average()
                .orElse(0);

        return Math.round(avg * 10.0) / 10.0;
    }

    public Map<String, Long> requestsByClient(LocalDate month) {
        MonthWindow w = window(month);
        List<CreativeRequest> list = repo.findByCreatedAtBetween(w.start(), w.end());

        return list.stream()
                .collect(Collectors.groupingBy(r -> r.getClient().getName(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (x, y) -> x,
                        LinkedHashMap::new
                ));
    }

    public List<CreativeRequest> recent() {
        return repo.findTop50ByOrderByCreatedAtDesc();
    }

    public List<CreativeRequest> filter(LocalDate month, Client client, RequestStatus status) {
        MonthWindow w = window(month);
        List<CreativeRequest> base = repo.findByCreatedAtBetween(w.start(), w.end());

        return base.stream()
                .filter(r -> client == null || r.getClient().getId().equals(client.getId()))
                .filter(r -> status == null || r.getStatus() == status)
                .sorted(Comparator.comparing(CreativeRequest::getCreatedAt).reversed())
                .toList();
    }
}