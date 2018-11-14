package ru.misterparser.edisoftcamel.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;
import ru.misterparser.edisoftcamel.domain.DestOrder;
import ru.misterparser.edisoftcamel.domain.SourceOrder;
import ru.misterparser.edisoftcamel.repository.DestOrderRepository;
import ru.misterparser.edisoftcamel.repository.SourceOrderRepository;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Slf4j
public class SaveService {

    private final SourceOrderRepository sourceOrderRepository;
    private final DestOrderRepository destOrderRepository;

    public SaveService(SourceOrderRepository sourceOrderRepository, DestOrderRepository destOrderRepository) {
        this.sourceOrderRepository = sourceOrderRepository;
        this.destOrderRepository = destOrderRepository;
    }

    @SuppressWarnings("unused")
    @Transactional
    public void save(Exchange exchange) {
        SourceOrder sourceOrder = SourceOrder.builder()
                .orderNumber((String) exchange.getIn().getHeader("orderNumber"))
                .xml((String) exchange.getIn().getHeader("sourceBody"))
                .date(new Date())
                .build();
        sourceOrderRepository.save(sourceOrder);
        log.debug("Saved: " + sourceOrder);

        DestOrder destOrder = DestOrder.builder()
                .orderNumber(sourceOrder.getOrderNumber())
                .sourceOrder(sourceOrder)
                .xml((String) exchange.getIn().getBody())
                .date(new Date())
                .build();
        destOrderRepository.save(destOrder);
        log.debug("Saved: " + destOrder);
    }
}
