package ru.misterparser.edisoftcamel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.misterparser.edisoftcamel.domain.SourceOrder;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface SourceOrderRepository extends JpaRepository<SourceOrder, Long> {

    @Query(value = "SELECT DISTINCT order_number FROM source_order", nativeQuery = true)
    List<String> getAllOrderNumbers();

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = "SELECT xml FROM source_order WHERE order_number = :orderNumber limit 1", nativeQuery = true)
    Optional<String> getSourceByOrderNumber(String orderNumber);
}
