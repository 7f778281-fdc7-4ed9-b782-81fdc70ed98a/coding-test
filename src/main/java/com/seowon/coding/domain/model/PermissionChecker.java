package com.seowon.coding.domain.model;


import lombok.Builder;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PermissionChecker {

    /**
     * TODO #7: 코드를 최적화하세요
     * 테스트 코드`PermissionCheckerTest`를 활용하시면 리펙토링에 도움이 됩니다.
     */
    public static boolean hasPermission(
            String userId,
            String targetResource,
            String targetAction,
            List<User> users,
            List<UserGroup> groups,
            List<Policy> policies
    ) {
        Map<String, UserGroup> groupMap = groups.stream().collect(Collectors.toMap(g -> g.id, g -> g));

        Map<String, Policy> policyMap = policies.stream().collect(Collectors.toMap(p -> p.id, p -> p));

        User user = users.stream().filter(u -> u.id.equals(userId))
                .findFirst().orElse(null);


        assert user != null;
        for (String groupId : user.groupIds) {
            UserGroup group = groupMap.get(groupId);
            if (group == null) continue;


            for (String policyId : group.policyIds) {
                Policy policy = policyMap.get(policyId);
                if (policy == null) continue;

                for (Statement statement : policy.statements) {
                    if (statement.actions.contains(targetAction) &&
                            statement.resources.contains(targetResource)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

class User {
    String id;
    List<String> groupIds;

    public User(String id, List<String> groupIds) {
        this.id = id;
        this.groupIds = groupIds;
    }
}

class UserGroup {
    String id;
    List<String> policyIds;

    public UserGroup(String id, List<String> policyIds) {
        this.id = id;
        this.policyIds = policyIds;
    }
}

class Policy {
    String id;
    List<Statement> statements;

    public Policy(String id, List<Statement> statements) {
        this.id = id;
        this.statements = statements;
    }
}

class Statement {
    List<String> actions;
    List<String> resources;

    @Builder
    public Statement(List<String> actions, List<String> resources) {
        this.actions = actions;
        this.resources = resources;
    }
}
