/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.user.metadata;

import static io.crate.types.DataTypes.BOOLEAN;
import static io.crate.types.DataTypes.STRING;

import io.crate.user.User;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.RelationName;
import io.crate.metadata.SystemTable;
import io.crate.metadata.sys.SysSchemaInfo;

public class SysUsersTableInfo {

    private static final RelationName IDENT = new RelationName(SysSchemaInfo.NAME, "users");
    private static final String PASSWORD_PLACEHOLDER = "********";

    public static SystemTable<User> create() {
        return SystemTable.<User>builder(IDENT)
            .add("name", STRING, User::name)
            .add("superuser", BOOLEAN, User::isSuperUser)
            .add("password", STRING, x -> x.password() == null ? null : PASSWORD_PLACEHOLDER)
            .setPrimaryKeys(new ColumnIdent("name"))
            .build();
    }
}
