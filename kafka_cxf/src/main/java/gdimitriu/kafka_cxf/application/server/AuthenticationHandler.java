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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationHandler implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationHandler.class);
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(requestContext.getUriInfo().getPath().equals("openapi.json")) {
            return;
        }
        String authorization = requestContext.getHeaderString("Authorization");
        String[] parts = authorization.split(" ");
        if (parts.length != 2 || !"Basic".equals(parts[0])) {
            requestContext.abortWith(createFaultResponse());
            return;
        }

        String decodedValue = null;
        try {
            decodedValue = new String(Base64Utility.decode(parts[1]));
        } catch (Base64Exception ex) {
            requestContext.abortWith(createFaultResponse());
            return;
        }
        String[] namePassword = decodedValue.split(":");
        if (isAuthenticated(namePassword[0], namePassword[1])) {
            // let request to continue
        } else {
            // authentication failed, request the authetication, add the realm name if needed to the value of WWW-Authenticate
            requestContext.abortWith(Response.status(401).header("WWW-Authenticate", "Basic").build());
        }
    }

    private boolean isAuthenticated(String s, String s1) {
        return s.equals(s1);
    }

    private Response createFaultResponse() {
        return Response.status(401).header("WWW-Authenticate", "Basic realm=\"kafka_cxf\"").build();
    }
}