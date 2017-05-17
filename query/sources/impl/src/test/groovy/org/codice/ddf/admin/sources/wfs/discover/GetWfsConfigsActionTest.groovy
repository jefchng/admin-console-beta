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
package org.codice.ddf.admin.sources.wfs.discover

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField
import org.codice.ddf.admin.sources.services.WfsServiceProperties
import org.codice.ddf.internal.admin.configurator.opfactory.AdminOpFactory
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory
import org.codice.ddf.internal.admin.configurator.opfactory.ServiceReader
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class GetWfsConfigsActionTest extends Specification {

    Action getWfsConfigsAction

    ConfiguratorFactory configuratorFactory

    ServiceReader serviceReader

    AdminOpFactory adminOpFactory

    ManagedServiceOpFactory managedServiceOpFactory

    static TEST_WFS_VERSION_1 = WfsVersion.WFS_VERSION_1

    static TEST_WFS_VERSION_2 = WfsVersion.WFS_VERSION_2

    static TEST_FACTORY_PID_1 = WfsServiceProperties.WFS1_FACTORY_PID

    static TEST_FACTORY_PID_2 = WfsServiceProperties.WFS2_FACTORY_PID

    static RESULT_ARGUMENT_PATH = [GetWfsConfigsAction.ID]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, BaseAction.ARGUMENT].flatten()

    def managedServiceConfigs

    def actionArgs = [
        (PID): S_PID_2
    ]

    def setup() {
        managedServiceConfigs = createWfsManagedServiceConfigs()
        serviceReader = Mock(ServiceReader)
        adminOpFactory = Mock(AdminOpFactory)
        managedServiceOpFactory = Mock(ManagedServiceOpFactory)
        configuratorFactory = Mock(ConfiguratorFactory)
        getWfsConfigsAction = new GetWfsConfigsAction(adminOpFactory, managedServiceOpFactory, serviceReader)
    }

    def 'No pid argument returns all configs'() {
        setup:
        serviceReader.getServices(_, _) >> []

        when:
        def report = getWfsConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        1 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * managedServiceOpFactory.read(_ as String) >> managedServiceConfigs
        1 * managedServiceOpFactory.read(_ as String) >> [:]
        report.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, SOURCE_ID_1, S_PID_1, true, TEST_WFS_VERSION_1)
        assertConfig(list.getList().get(1), 1, SOURCE_ID_2, S_PID_2, false, TEST_WFS_VERSION_2)
    }

    def 'Pid filter returns 1 result'() {
        setup:
        getWfsConfigsAction.setArguments(actionArgs)

        when:
        def report = getWfsConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * serviceReader.getServices(_, _) >> []
        adminOpFactory.read(S_PID_2) >> managedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, SOURCE_ID_2, S_PID_2, false, TEST_WFS_VERSION_2)
    }

    def 'Fail due to no existing config with specified pid'() {
        setup:
        actionArgs.put(PID, S_PID)
        getWfsConfigsAction.setArguments(actionArgs)
        adminOpFactory.read(S_PID) >> [:]

        when:
        def report = getWfsConfigsAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def assertConfig(Field field, int index, String sourceName, String pid, boolean availability, String wfsVersion) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.fieldName() == ListFieldImpl.INDEX_DELIMETER + index
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.config().credentials().password() == FLAG_PASSWORD
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().pid() == pid
        assert ((WfsSourceConfigurationField)sourceInfo.config()).wfsVersion() == wfsVersion
        return true
    }

    def createWfsManagedServiceConfigs() {
        managedServiceConfigs = baseManagedServiceConfigs
        managedServiceConfigs.get(S_PID_1).put(FACTORY_PID_KEY, TEST_FACTORY_PID_1)
        managedServiceConfigs.get(S_PID_2).put(FACTORY_PID_KEY, TEST_FACTORY_PID_2)
        return managedServiceConfigs
    }
}
