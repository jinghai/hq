package org.hyperic.hq.plugin.rabbitmq.manage;

import org.hyperic.hq.plugin.rabbitmq.AbstractPluginTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * RabbitBrokerManagerPluginTest
 *
 * @author Helena Edelson
 */
@Ignore("Need to mock the connection for automation")
public class RabbitBrokerManagerPluginTest extends AbstractPluginTest {

    @Test  
    public void testProperties(){
        Properties properties = productPlugin.getConfig().toProperties();
        assertNotNull(properties);
        for (Map.Entry prop : properties.entrySet()) {
            //todo
        }
    }
}