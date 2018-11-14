package ru.misterparser.edisoftcamel;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

import javax.xml.transform.Source;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class RoutesTest {

    private static final Random RANDOM = new Random();
    private static final XPathEngine X_PATH_ENGINE = new JAXPXPathEngine();

    @Value("${directory.source}")
    private String directorySource;

    @Value("${directory.dest1}")
    private String directoryData1;

    @Value("${directory.dest2}")
    private String directoryData2;

    @Value("${directory.bad}")
    private String directoryBad;

    @SuppressWarnings("unused")
    @Autowired
    private MockMvc mvc;

    @SuppressWarnings("unused")
    @EndpointInject(uri = "direct:root")
    private ProducerTemplate producerTemplate;

    @SuppressWarnings("unused")
    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultMockEndpoint;

    @Before
    public void setUp() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get(directorySource));
        FileSystemUtils.deleteRecursively(Paths.get(directoryData1));
        FileSystemUtils.deleteRecursively(Paths.get(directoryData2));
        FileSystemUtils.deleteRecursively(Paths.get(directoryBad));
    }

    @Test
    public void testRoutes() throws Exception {
        int i = RANDOM.nextInt(100);
        AtomicReference<String> orderNumber = new AtomicReference<>();

        // Проверяем создание zip архива с заданным именем.
        resultMockEndpoint.expectedFileExists(directoryData2 + "/order" + i + ".zip");

        // Проверяем наличие в каталоге directoryData1 одного xml файла с именем равным OrderNumber.
        resultMockEndpoint.expects(() -> {
            File[] files = new File(directoryData1).listFiles();
            assert files != null && files.length == 1;
            File file = files[0];
            Source source = Input.fromFile(file).build();
            Iterable<Node> nodes = X_PATH_ENGINE.selectNodes("/Interchange/Group/Message/Document-Order/Order-Header/OrderNumber", source);
            Optional<Node> first = StreamSupport.stream(nodes.spliterator(), false).findFirst();
            orderNumber.set(first.orElseThrow(RuntimeException::new).getTextContent());
            assert file.getName().equals(orderNumber.get() + ".xml");
        });

        // Проверяем работу REST-метода getAllOrderNumbers.
        resultMockEndpoint.expects(() -> {
            try {
                MockHttpServletRequestBuilder requestBuilder = get("/source/getAllOrderNumbers").contentType(MediaType.APPLICATION_JSON);
                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk());
                resultActions.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                resultActions.andExpect(jsonPath("[0]", is(orderNumber.get())));
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });

        Supplier<InputStream> originalOrderXml = () -> getClass().getResourceAsStream("/original_order.xml");

        // Проверяем работу REST-метода getSourceByOrderNumber.
        resultMockEndpoint.expects(() -> {
            try {
                MockHttpServletRequestBuilder requestBuilder = get("/source/getSourceByOrderNumber/" + orderNumber.get()).contentType(MediaType.APPLICATION_JSON);
                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk());
                resultActions.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
                String s = StreamUtils.copyToString(originalOrderXml.get(), StandardCharsets.UTF_8);
                resultActions.andExpect(content().xml(s));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        resultMockEndpoint.setResultWaitTime(8000);

        producerTemplate.sendBodyAndHeader(
                "file:" + directorySource,
                originalOrderXml.get(),
                Exchange.FILE_NAME,
                "order" + i + ".xml"
        );

        resultMockEndpoint.assertIsSatisfied();
    }
}
