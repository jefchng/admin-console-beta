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
package org.codice.ddf.admin.ldap.discover

import org.codice.ddf.admin.api.action.ActionReport
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.LdapTestingCommons
import org.codice.ddf.admin.ldap.TestLdapServer
import spock.lang.Ignore
import spock.lang.Specification

class TestLdapSettingsTest extends Specification {
    static TestLdapServer server
    Map<String, Object> args
    LdapTestSettings action

    def setupSpec() {
        server = TestLdapServer.getInstance().useSimpleAuth()
        server.startListening()
    }

    def cleanupSpec() {
        server.shutdown()
        server = null
    }

    def setup() {
        LdapTestingCommons.loadLdapTestProperties()
        action = new LdapTestSettings()
    }

    def 'Fail on missing required fields'() {
        setup:
        def baseMsg = [LdapTestSettings.ID, BaseAction.ARGUMENT]
//        def missingHostMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME]
//        def missingPortMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME]
//        def missingEncryptMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME]
//        def missingUsernameMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME]
//        def missingUserpasswordMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USER_PASSWORD_FIELD_NAME]
//        def missingBindMethodMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME]
//        def missingQueryBaseMsgPath = baseMsg + [PerformLdapQuery.QUERY_BASE_FIELD_NAME]
//        def missingQueryMsgPath = baseMsg + [LdapQueryField.DEFAULT_FIELD_NAME]

        when:
        ActionReport report = action.process()

        then:
        report.messages().size() == 8
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 8
        report.result() == null

//        report.messages()*.getPath() as Set == [missingHostMsgPath, missingPortMsgPath, missingEncryptMsgPath,
//                                                missingUsernameMsgPath, missingUserpasswordMsgPath, missingBindMethodMsgPath,
//                                                missingQueryBaseMsgPath, missingQueryMsgPath] as Set
    }

    @Ignore
    def 'Fail on missing required fields for LDAP Attribute Store'() {
    }

    @Ignore
    def 'Fail to connect to LDAP'() {
    }

    @Ignore
    def 'Fail to bind to LDAP'() {
    }

    @Ignore
    def 'Fail when baseUserDN & baseGroupDN dont exist'() {
    }

    @Ignore
    def 'Fail to find entries in baseUserDN'() {
    }

    @Ignore
    def 'Fail to find usernameAttribute on users in baseUserDN'() {
    }

    @Ignore
    def 'Fail to find entries in baseGroupDn'() {
    }

    @Ignore
    def 'Fail to find specified groupObjectClass in groups'() {
    }
}
