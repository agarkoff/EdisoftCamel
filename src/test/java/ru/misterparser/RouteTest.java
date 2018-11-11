package ru.misterparser;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Random;

public class RouteTest extends CamelSpringTestSupport {

    private static final Random RANDOM = new Random();

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/flow.xml");
    }

    @Override
    @Before
    public void setUp() throws Exception {
        deleteDirectory("data");
        super.setUp();
    }

    @Test
    public void testRoute() throws Exception {
        int i = RANDOM.nextInt(100);

        MockEndpoint mockEndpoint = getMockEndpoint("mock:result");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedFileExists("data2/order" + i + ".zip");
        mockEndpoint.setResultWaitTime(8000);

        template.sendBodyAndHeader("file:data", getClass().getResourceAsStream("/original_order.xml"), Exchange.FILE_NAME, "order" + i + ".xml");

        context.start();

        assertMockEndpointsSatisfied();
    }
}
