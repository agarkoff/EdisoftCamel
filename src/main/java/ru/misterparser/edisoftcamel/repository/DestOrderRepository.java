package ru.misterparser.edisoftcamel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.misterparser.edisoftcamel.domain.DestOrder;

@Transactional(readOnly = true)
public interface DestOrderRepository extends JpaRepository<DestOrder, Long> {
}
