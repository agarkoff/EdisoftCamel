package ru.misterparser;

import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Преобразует список мапов, который возвращает SQL, в список значений из указанного столбца.
 */
public class ResultSetToListTransformer {

    @SuppressWarnings("unused")
    public void transform(Exchange exchange, String column) {
        List<String> orderNumbers = new ArrayList<>();
        //noinspection unchecked
        List<Map<String, String>> list = (List<Map<String, String>>) exchange.getIn().getBody();
        for (Map<String, String> map : list) {
            orderNumbers.add(map.get(column));
        }
        if (!exchange.hasOut()) {
            exchange.setOut(exchange.getIn());
        }
        exchange.getOut().setBody(orderNumbers);
    }
}
