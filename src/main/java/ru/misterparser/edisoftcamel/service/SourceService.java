package ru.misterparser.edisoftcamel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.misterparser.edisoftcamel.repository.SourceOrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SourceService {

    private final SourceOrderRepository sourceOrderRepository;

    @Autowired
    public SourceService(SourceOrderRepository sourceOrderRepository) {
        this.sourceOrderRepository = sourceOrderRepository;
    }

    public List<String> getAllOrderNumbers() {
        return sourceOrderRepository.getAllOrderNumbers();
    }

    public Optional<String> getSourceByOrderNumber(String orderNumber) {
        return sourceOrderRepository.getSourceByOrderNumber(orderNumber);
    }
}
