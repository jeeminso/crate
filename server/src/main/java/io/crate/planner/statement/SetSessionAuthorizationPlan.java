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

package io.crate.planner.statement;

import io.crate.analyze.AnalyzedSetSessionAuthorizationStatement;
import io.crate.auth.user.User;
import io.crate.auth.user.UserManager;
import io.crate.data.InMemoryBatchIterator;
import io.crate.data.Row;
import io.crate.data.RowConsumer;
import io.crate.planner.DependencyCarrier;
import io.crate.planner.Plan;
import io.crate.planner.PlannerContext;
import io.crate.planner.operators.SubQueryResults;

import static io.crate.data.SentinelRow.SENTINEL;

public class SetSessionAuthorizationPlan implements Plan {

    private final AnalyzedSetSessionAuthorizationStatement setSessionAuthorization;
    private final UserManager userManager;

    public SetSessionAuthorizationPlan(AnalyzedSetSessionAuthorizationStatement setSessionAuthorization,
                                       UserManager userManager) {
        this.setSessionAuthorization = setSessionAuthorization;
        this.userManager = userManager;
    }

    @Override
    public StatementType type() {
        return StatementType.MANAGEMENT;
    }

    @Override
    public void executeOrFail(DependencyCarrier executor,
                              PlannerContext plannerContext,
                              RowConsumer consumer,
                              Row params,
                              SubQueryResults subQueryResults) throws Exception {
        var sessionContext = plannerContext.transactionContext().sessionContext();
        String userName = setSessionAuthorization.user();
        User user;
        if (userName != null) {
            user = userManager.findUser(userName);
            if (user == null) {
                throw new IllegalArgumentException("User '" + userName + "' does not exist.");
            }
        } else {
            user = sessionContext.authenticatedUser();
        }
        sessionContext.setSessionUser(user);
        consumer.accept(InMemoryBatchIterator.empty(SENTINEL), null);
    }
}
