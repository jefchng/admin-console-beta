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
 */
package org.codice.ddf.admin.sources.commons.utils;

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.getAllSourceReferences;
import static org.codice.ddf.admin.sources.commons.SourceMessages.duplicateSourceNameError;

import java.util.List;

import org.codice.ddf.admin.common.Report;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.internal.admin.configurator.opfactory.ServiceReader;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.Source;
import ddf.catalog.util.Describable;

public class SourceValidationUtils {

    /**
     * Determines whether the service configuration identified by the service pid has the same name as sourceName.
     * If the service configuration's id equals the sourceName return {@code true}, otherwise {@code false}.
     *
     * @param servicePid    service pid of the service configuration
     * @param sourceName    source name to check against existing source name
     * @param serviceReader service to read service information
     * @return {@code true} if the sourceName matches the existing configuration's id, {@code false} otherwise
     */
    public static boolean hasSourceName(String servicePid, String sourceName,
            ServiceReader serviceReader) {
        Source source = getAllSourceReferences(serviceReader).stream()
                .map(ConfiguredService.class::cast)
                .filter(configuredService -> configuredService.getConfigurationPid()
                        .equals(servicePid))
                .findFirst()
                .map(Source.class::cast)
                .orElse(null);

        return source == null || source.getId()
                .equals(sourceName);
    }

    /**
     * Validates the {@code sourceName} against the existing source names in the system. An empty {@link Report} will be returned
     * if there are no existing source names with with name {@code sourceName}, or a {@link Report} with error messages.
     *
     * @param sourceName    source name to validate
     * @param serviceReader service to read service information
     * @return a {@link Report} containing a {@link org.codice.ddf.admin.sources.commons.SourceMessages#DUPLICATE_SOURCE_NAME} error, or a Report with
     * no messages on success.
     */
    // TODO: 4/24/17 phuffer -  adding a duplicate name should be valid as long as the Active Binding is different
    public static Report validateSourceName(StringField sourceName, ServiceReader serviceReader) {
        List<Source> sources = getAllSourceReferences(serviceReader);
        boolean matchFound = sources.stream()
                .map(Describable::getId)
                .anyMatch(id -> id.equals(sourceName.getValue()));

        Report report = new Report();
        if (matchFound) {
            report.argumentMessage(duplicateSourceNameError(sourceName.path()));
        }
        return report;
    }
}
