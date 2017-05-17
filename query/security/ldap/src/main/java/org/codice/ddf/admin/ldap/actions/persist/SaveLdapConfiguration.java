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
package org.codice.ddf.admin.ldap.actions.persist;

import static org.codice.ddf.admin.ldap.actions.commons.services.LdapServiceCommons.ldapConfigToLdapClaimsHandlerService;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN_AND_ATTRIBUTE_STORE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.actions.commons.services.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.internal.admin.configurator.opfactory.FeatureOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.PropertyOpFactory;

import com.google.common.collect.ImmutableList;

public class SaveLdapConfiguration extends BaseAction<ListField<LdapConfigurationField>> {

    public static final String NAME = "saveLdapConfig";

    public static final String DESCRIPTION = "Saves the LDAP configuration.";

    private LdapConfigurationField config;

    private ConfiguratorFactory configuratorFactory;

    private FeatureOpFactory featureOpFactory;

    private ManagedServiceOpFactory managedServiceOpFactory;

    private PropertyOpFactory propertyOpFactory;

    private LdapServiceCommons serviceCommons;

    public SaveLdapConfiguration(ConfiguratorFactory configuratorFactory,
            FeatureOpFactory featureOpFactory, ManagedServiceOpFactory managedServiceOpFactory,
            PropertyOpFactory propertyOpFactory) {
        super(NAME, DESCRIPTION, new ListFieldImpl<>(LdapConfigurationField.class));
        config = new LdapConfigurationField();
        this.configuratorFactory = configuratorFactory;
        this.featureOpFactory = featureOpFactory;
        this.managedServiceOpFactory = managedServiceOpFactory;
        this.propertyOpFactory = propertyOpFactory;
        this.serviceCommons = new LdapServiceCommons(managedServiceOpFactory, propertyOpFactory);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public ListField<LdapConfigurationField> performAction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        if (config.settingsField()
                .useCase()
                .equals(LOGIN) || config.settingsField()
                .useCase()
                .equals(LOGIN_AND_ATTRIBUTE_STORE)) {

            Map<String, Object> ldapLoginServiceProps =
                    serviceCommons.ldapConfigurationToLdapLoginService(config);
            configurator.add(featureOpFactory.start(LdapLoginServiceProperties.LDAP_LOGIN_FEATURE));
            configurator.add(managedServiceOpFactory.create(LdapLoginServiceProperties.LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID,
                    ldapLoginServiceProps));
        }

        if (config.settingsField()
                .useCase()
                .equals(ATTRIBUTE_STORE) || config.settingsField()
                .useCase()
                .equals(LOGIN_AND_ATTRIBUTE_STORE)) {

            Path newAttributeMappingPath = Paths.get(System.getProperty("ddf.home"),
                    "etc",
                    "ws-security",
                    "ldapAttributeMap-" + UUID.randomUUID()
                            .toString() + ".props");
            Map<String, Object> ldapClaimsServiceProps =
                    ldapConfigToLdapClaimsHandlerService(config);
            configurator.add(propertyOpFactory.create(newAttributeMappingPath,
                    config.settingsField()
                            .attributeMap()));
            configurator.add(featureOpFactory.start(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE));
            configurator.add(managedServiceOpFactory.create(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID,
                    ldapClaimsServiceProps));
        }

        OperationReport report = configurator.commit("LDAP Configuration saved with details: {}",
                config.toString());

        // TODO: tbatie - 4/3/17 - Handle error messages
        return serviceCommons.getLdapConfigurations();
    }
}
