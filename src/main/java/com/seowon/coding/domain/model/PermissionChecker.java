package com.seowon.coding.domain.model;


import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

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
        User user = null;

        for (User u : users) {
            if (u.id.equals(userId)) user = u;
        }

        if (user.equals(null)) return false;

        List<UserGroup> userGroups = new ArrayList<>();

        for (String groupId : user.groupIds) {
            for (UserGroup group : groups) {
                if (group.id.equals(groupId)) userGroups.add(group);
            }
        }

        if (userGroups.size() == 0) return false;


        List<Policy> myPolicies = new ArrayList<>();

        for (UserGroup group: userGroups) {
            for (String policyId : group.policyIds) {
                for (Policy policy : policies) {
                    if (policy.id.equals(policyId)) myPolicies.add(policy);
                }
            }
        }

        if (myPolicies.size() == 0) return false;


        for (Policy policy: myPolicies) {
            for (Statement statement : policy.statements) {
                if (statement.actions.contains(targetAction) &&
                        statement.resources.contains(targetResource)) {
                    return true;
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