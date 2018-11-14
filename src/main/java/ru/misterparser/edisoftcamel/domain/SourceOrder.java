package ru.misterparser.edisoftcamel.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "source_order")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SourceOrder implements Persistable<Long> {

    @Id
    @SequenceGenerator(name = "source_order_id_gen", sequenceName = "source_order_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "source_order_id_gen")
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "xml", length = Integer.MAX_VALUE)
    private String xml;

    @Column(name = "date")
    private Date date;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
