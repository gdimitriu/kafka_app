module kafka.cxf {
    requires kafka.clients;
    requires com.fasterxml.jackson.annotation;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.context;
    requires spring.beans;
    requires java.annotation;
    requires org.slf4j;
    opens gdimitriu.kafka_cxf.application to spring.core,spring.beans, spring.context;
    opens gdimitriu.kafka_cxf.dao to spring.core,spring.beans, spring.context, com.fasterxml.jackson.databind;
    opens gdimitriu.kafka_cxf.properties to spring.core,spring.beans, spring.context, spring.boot;
}