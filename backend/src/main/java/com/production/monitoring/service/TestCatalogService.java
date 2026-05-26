package com.production.monitoring.service;

import com.production.monitoring.model.entity.TestCatalog;
import com.production.monitoring.repository.TestCatalogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestCatalogService {
    private final TestCatalogRepository testCatalogRepository;
    private final ValidationDurationService validationDurationService;

    @Transactional
    public List<TestCatalog> findAll() {
        return testCatalogRepository.findAll().stream()
                .map(this::syncEstimatedDuration)
                .toList();
    }

    public TestCatalog syncEstimatedDuration(TestCatalog test) {
        ValidationDurationService.DurationRange duration = validationDurationService.durationForTestName(test.getName());
        if (!durationMatches(test, duration)) {
            test.setEstimatedMinMinutes(duration.estimatedMinMinutes());
            test.setEstimatedMaxMinutes(duration.estimatedMaxMinutes());
            return testCatalogRepository.save(test);
        }
        return test;
    }

    private boolean durationMatches(TestCatalog test, ValidationDurationService.DurationRange duration) {
        return Integer.valueOf(duration.estimatedMinMinutes()).equals(test.getEstimatedMinMinutes())
                && Integer.valueOf(duration.estimatedMaxMinutes()).equals(test.getEstimatedMaxMinutes());
    }
}
