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
package org.codice.ddf.admin.common.fields;

import static org.codice.ddf.admin.api.DataType.FieldBaseType.STRING;

import org.codice.ddf.admin.common.fields.base.BaseDataType;

public class TestDataType<T> extends BaseDataType<T> {

    public static final String FIELD_NAME = "testFieldName";

    public static final String FIELD_TYPE_NAME = "testFieldTypeName";

    public static final String DESCRIPTION = "testDescription";

    private T value;

    public TestDataType() {
        this(FIELD_NAME);
    }

    public TestDataType(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION, STRING);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }
}
