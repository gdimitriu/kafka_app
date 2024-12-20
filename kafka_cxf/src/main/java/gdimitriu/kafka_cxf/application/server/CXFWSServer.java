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

import gdimitriu.kafka_cxf.controllers.KafkaWSClientController;
import gdimitriu.kafka_cxf.properties.WSProperties;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import jakarta.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
@ComponentScan(basePackages = "gdimitriu.kafka_cxf")
public class CXFWSServer {

    private JaxWsServerFactoryBean wsServer;

    @Autowired
    private KafkaWSClientController wsController;

    @Autowired
    private WSProperties wsProperties;

    CXFWSServer() {
    }

    @PostConstruct
    void startServer() {
        System.out.println("here");
        wsServer = new JaxWsServerFactoryBean();
        wsServer.setServiceBean(wsController);
        String hostname = "localhost";
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
        }
        wsServer.setAddress("http://" + hostname + ":" + wsProperties.getPort() + "/Kafka");
        wsServer.create();
    }
}
