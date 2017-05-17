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
package org.codice.ddf.admin.common.fields.common;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.duplicateMapKeyError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;

import com.google.common.collect.ImmutableList;

public class MapField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "map";

    public static final String FIELD_TYPE_NAME = "Map";

    public static final String DESCRIPTION = "A map backed by a list of key value pairs.";

    public static final String ENTRIES = "entries";

    protected ListField<PairField> entries;

    public MapField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        entries = new ListFieldImpl<>(ENTRIES, PairField.class);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(entries);
    }

    public MapField put(String key, String value) {
        if (containsKey(key)) {
            updateKeyValue(key, value);
        } else {
            add(key, value);
        }
        return this;
    }

    public boolean containsValue(String value) {
        return entries.getList()
                .stream()
                .anyMatch(pair -> pair.value()
                        .equals(value));
    }

    public boolean containsKey(String key) {
        return entries.getList()
                .stream()
                .anyMatch(pair -> pair.key()
                        .equals(key));
    }

    public boolean isEmpty() {
        return entries.getList()
                .isEmpty();
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = new ArrayList<>();
        long skip = 0;
        for (PairField pairField : entries.getList()) {
            List<PairField> duplicatePairs = entries.getList()
                    .stream()
                    .skip(skip)
                    .filter(pair -> pair.key()
                            .equals(pairField.key()))
                    .collect(Collectors.toList());

            if (duplicatePairs.size() > 1) {
                validationMsgs.add(duplicateMapKeyError(duplicatePairs.get(1)
                        .path()));
                break;
            }
            skip++;
        }
        return validationMsgs;
    }

    private void add(String key, String value) {
        entries.add(new PairField().key(key)
                .value(value));
    }

    private void updateKeyValue(String key, String value) {
        entries.getList()
                .stream()
                .filter(p -> p.key()
                        .equals(key))
                .findFirst()
                .ifPresent(pair -> pair.value(value));
    }
}
