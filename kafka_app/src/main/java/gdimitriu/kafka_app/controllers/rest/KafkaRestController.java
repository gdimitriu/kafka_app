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
package gdimitriu.kafka_app.controllers.rest;

import gdimitriu.kafka_app.dao.RequestPostTopic;
import gdimitriu.kafka_app.properties.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
@Slf4j
public class KafkaRestController {

    @Autowired
    private KafkaProperties properties;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String getInfo() {
        return properties.getServersList();
    }

    @RequestMapping(value = "/topics/{topic}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public String postTopic(@PathVariable("topic") String topicName, @RequestBody  RequestPostTopic dataTopic) {
        log.info("topic:" + topicName + " data = " + dataTopic.getKey() + ":" + dataTopic.getValue());
        return "success";
    }
    @RequestMapping(value = "/topics/test", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public RequestPostTopic getTest() {
        return new RequestPostTopic("aa1","bb1");
    }
}
