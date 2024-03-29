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
module kafka.cxf {
    requires kafka.clients;
    requires com.fasterxml.jackson.annotation;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.context;
    requires spring.beans;
    requires java.annotation;
    requires org.slf4j;
    requires java.validation;
    requires jakarta.activation;
    requires org.apache.cxf.frontend.jaxrs;
    requires com.fasterxml.jackson.jaxrs.json;
    requires org.apache.cxf.rs.openapi.v3;
    requires org.apache.cxf.rs.swagger.ui;
    requires org.apache.cxf.core;
    requires org.apache.cxf.frontend.jaxws;
    requires java.ws.rs;
    requires java.jws;
    requires swagger.ui;
    requires com.fasterxml.jackson.dataformat.xml;
    opens gdimitriu.kafka_cxf.application to spring.core,spring.beans, spring.context;
    opens gdimitriu.kafka_cxf.application.server to spring.core,spring.beans, spring.context, org.apache.cxf.frontend.jaxrs;
    opens gdimitriu.kafka_cxf.dao to spring.core,spring.beans, spring.context, com.fasterxml.jackson.databind, swagger.ui;
    opens gdimitriu.kafka_cxf.properties to spring.core,spring.beans, spring.context, spring.boot;
    opens gdimitriu.kafka_cxf.controllers to spring.core,spring.beans, spring.context, spring.boot, gdimitriu.kafka_cxf.application.server, swagger.ui;

}