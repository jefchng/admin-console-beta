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

import java.util.List;

import org.codice.ddf.admin.common.report.message.ErrorMessage;
import org.codice.ddf.admin.common.report.message.WarningMessage;

public class LdapMessages {

    public static final String CANNOT_BIND = "CANNOT_BIND";

    public static ErrorMessage cannotBindError(List<String> path) {
        return new ErrorMessage(CANNOT_BIND, path);
    }

    public static ErrorMessage invalidDnFormatError(List<String> path) {
        return  new ErrorMessage("INVALID_DN", path);
    }

    public static ErrorMessage invalidQueryError(List<String> path) {
        return new ErrorMessage("INVALID_QUERY", path);
    }

    public static WarningMessage serviceAlreadyExistsWarning(List<String> path) {
        return new WarningMessage("IDENTICAL_SERVICE_EXISTS", path);
    }

    public static ErrorMessage dnDoesNotExistError(List<String> pathOrigin) {
        return new ErrorMessage("DN_DOES_NOT_EXIST", pathOrigin);
    }

    public static ErrorMessage noUsersInBaseUserDnError(List<String> path) {
        return new ErrorMessage("NO_USERS_IN_BASE_USER_DN", path);
    }

    public static ErrorMessage noGroupsInBaseGroupDnError(List<String> path) {
        return new ErrorMessage("NO_GROUPS_IN_BASE_GROUP_DN", path);
    }

    public static WarningMessage noGroupsWithMembersWarning(List<String> path) {
        return new WarningMessage("NO_GROUPS_WITH_MEMBERS", path);
    }

    public static WarningMessage noReferencedMemberWarning(List<String> path) {
        return new WarningMessage("NO_REFERENCED_MEMBER", path);
    }

    public static WarningMessage userNameAttributeNotFoundWarning(List<String> path) {
        return new WarningMessage("USER_NAME_ATTRIBUTE_NOT_FOUND", path);
    }
}
