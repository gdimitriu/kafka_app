/*
 Copyright (c) 2021 Gabriel Dimitriu All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This file is part of Kafka_app project.

 Kafka_app is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Kafka_app is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Kafka_app.  If not, see <http://www.gnu.org/licenses/>.
 */
package gdimitriu.kafka_cxf.application.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gdimitriu.kafka_cxf.controllers.KafkaRESTClientController;
import gdimitriu.kafka_cxf.properties.RESTProperties;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;

@Controller
@ComponentScan(basePackages = "gdimitriu.kafka_cxf")
public class CXFRESTServer {

    private JAXRSServerFactoryBean restServer;

    @Autowired
    private KafkaRESTClientController restController;

    @Autowired
    private RESTProperties restProperties;

    CXFRESTServer() {
    }

    private OpenApiFeature feature;

    @PostConstruct
    void startServer() {
        restServer = new JAXRSServerFactoryBean();
        feature = new OpenApiFeature();
        feature.setSwaggerUiConfig(
                new SwaggerUiConfig()
                        .url("/openapi.json"));
        restServer.setProvider(new JacksonJaxbJsonProvider());
        restServer.setServiceBean(restController);
        restServer.getFeatures().add(feature);
        restServer.setProvider(new AuthenticationHandler());
        String hostname = "localhost";
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
        }
        restServer.setAddress("http://" + hostname + ":" + restProperties.getPort());
        restServer.create();
    }
}
