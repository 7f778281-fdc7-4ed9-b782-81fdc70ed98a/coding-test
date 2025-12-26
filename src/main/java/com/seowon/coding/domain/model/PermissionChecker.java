package com.seowon.coding.domain.model;


import lombok.Builder;

import java.util.List;

class PermissionChecker {

    /**
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

        return checkUser(userId, targetResource, targetAction, users, groups, policies);
    }

    private static boolean checkUser(String userId, String targetResource, String targetAction, List<User> users, List<UserGroup> groups, List<Policy> policies) {
        for (User user : users) {
            if (user.id.equals(userId)) {
                if (checkGroup(targetResource, targetAction, groups, policies, user)) return true;
            }
        }
        return false;
    }

    private static boolean checkGroup(String targetResource, String targetAction, List<UserGroup> groups, List<Policy> policies, User user) {
        for (String groupId : user.groupIds) {
            for (UserGroup group : groups) {
                if (group.id.equals(groupId)) {
                    for (String policyId : group.policyIds) {
                        if (checkPolicy(targetResource, targetAction, policies, policyId)) return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkPolicy(String targetResource, String targetAction, List<Policy> policies, String policyId) {
        for (Policy policy : policies) {
            if (policy.id.equals(policyId)) {
                if (checkStatements(targetResource, targetAction, policy)) return true;
            }
        }
        return false;
    }

    private static boolean checkStatements(String targetResource, String targetAction, Policy policy) {
        for (Statement statement : policy.statements) {
            if (statement.actions.contains(targetAction) &&
                statement.resources.contains(targetResource)) {
                return true;
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