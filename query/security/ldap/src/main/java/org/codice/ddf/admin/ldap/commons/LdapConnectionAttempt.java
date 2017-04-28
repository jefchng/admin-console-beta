/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.ldap.commons;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codice.ddf.admin.api.report.Message;
import org.forgerock.opendj.ldap.Connection;

public class LdapConnectionAttempt implements Closeable {

    // TODO: tbatie - 5/1/17 - Change this to maintain an arg messages and return value list of msgs
    private List<Message> argumentMsgs;
    private List<Message> msgs;

    private Optional<Connection> connection;

    public LdapConnectionAttempt() {
        msgs = new ArrayList<>();
        argumentMsgs = new ArrayList<>();
        connection = Optional.empty();
    }

    public LdapConnectionAttempt(Connection connection) {
        this();
        this.connection = Optional.of(connection);
    }

    public LdapConnectionAttempt addArgumentMessage(Message msg) {
        argumentMsgs.add(msg);
        return this;
    }

    public LdapConnectionAttempt addMessage(Message msg) {
        msgs.add(msg);
        return this;
    }

    public List<Message> messages() {
        return msgs;
    }

    public List<Message> argumentMessages() {
        return argumentMsgs;
    }

    public Optional<Connection> connection() {
        return connection;
    }

    public LdapConnectionAttempt connection(Optional<Connection> connection) {
        if(connection == null) {
            this.connection = Optional.empty();
        } else {
            this.connection = connection;
        }
        return this;
    }

    @Override
    public void close() throws IOException {
        if (connection.isPresent() && !connection.get().isClosed()) {
            connection.get().close();
        }
    }
}
