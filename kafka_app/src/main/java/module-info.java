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
module gdimitriu.kafka_app {
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires org.apache.tomcat.embed.core;
    requires spring.jcl;
    requires spring.beans;
    requires spring.context;
    requires spring.webmvc;
    requires java.management;
    requires org.slf4j;
    requires java.annotation;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires kafka.clients;
    requires spring.security.core;
    requires spring.security.config;
    opens gdimitriu.kafka_app.security to spring.core,spring.core.security,spring.beans, spring.context;
    opens gdimitriu.kafka_app.application to spring.core,spring.beans, spring.context;
    opens gdimitriu.kafka_app.dao to spring.core,spring.beans, spring.context, com.fasterxml.jackson.databind;
    opens gdimitriu.kafka_app.properties to spring.core,spring.beans, spring.context, spring.boot;
    opens gdimitriu.kafka_app.controllers.rest to spring.core,spring.beans,spring.webmvc,spring.web,spring.context;
}