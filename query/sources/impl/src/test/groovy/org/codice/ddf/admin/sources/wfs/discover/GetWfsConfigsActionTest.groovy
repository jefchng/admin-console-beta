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
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField
import org.codice.ddf.admin.sources.services.WfsServiceProperties
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class GetWfsConfigsActionTest extends Specification {

    Action getWfsConfigsAction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    static TEST_WFS_VERSION_1 = WfsVersion.WFS_VERSION_1

    static TEST_WFS_VERSION_2 = WfsVersion.WFS_VERSION_2

    static TEST_FACTORY_PID_1 = WfsServiceProperties.WFS1_FACTORY_PID

    static TEST_FACTORY_PID_2 = WfsServiceProperties.WFS2_FACTORY_PID

    static BASE_PATH = [GetWfsConfigsAction.ID, BaseAction.ARGUMENT]

    static PID_PATH = [BASE_PATH, PID].flatten()

    def managedServiceConfigs

    def actionArgs = [
        (PID): S_PID_2
    ]

    def setup() {
        managedServiceConfigs = createWfsManagedServiceConfigs()
        configReader = Mock(ConfigReader)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
        }
        getWfsConfigsAction = new GetWfsConfigsAction(configuratorFactory)
    }

    def 'test no pid argument returns all configs'() {
        setup:
        configReader.getServices(_, _) >> []

        when:
        def report = getWfsConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getManagedServiceConfigs(_ as String) >> managedServiceConfigs
        1 * configReader.getManagedServiceConfigs(_ as String) >> [:]
        report.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, SOURCE_ID_1, S_PID_1, true, TEST_WFS_VERSION_1)
        assertConfig(list.getList().get(1), 1, SOURCE_ID_2, S_PID_2, false, TEST_WFS_VERSION_2)
    }

    def 'test pid filter returns 1 result'() {
        setup:
        getWfsConfigsAction.setArguments(actionArgs)

        when:
        def report = getWfsConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getServices(_, _) >> []
        1 * configReader.getConfig(S_PID_2) >> managedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, SOURCE_ID_2, S_PID_2, false, TEST_WFS_VERSION_2)
    }

    def 'test failure due to provided but empty pid field'() {
        when:
        getWfsConfigsAction.setArguments([(PID): ''])
        def report = getWfsConfigsAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == PID_PATH
    }

    def assertConfig(Field field, int index, String sourceName, String pid, boolean availability, String wfsVersion) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.fieldName() == ListFieldImpl.INDEX_DELIMETER + index
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.config().credentials().password() == "*****"
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