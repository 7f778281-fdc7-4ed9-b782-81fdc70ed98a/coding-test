package com.seowon.coding.domain.model;


import lombok.Builder;

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
        for (User user : users) {
            if(user.checkPermission(userId, groups, policies, targetResource, targetAction))
                return true;
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

    public boolean checkPermission(String userId, List<UserGroup> groups, List<Policy> policies, String targetResource, String targetAction) {
        if (this.id.equals(userId)) {
            for (UserGroup group : groups) {
                if (group.checkUserGroup(this.groupIds, policies, targetAction, targetResource)) {
                    return true;
                }
            }
        }
        return false;
    }
}

class UserGroup {
    String id;
    List<String> policyIds;

    public UserGroup(String id, List<String> policyIds) {
        this.id = id;
        this.policyIds = policyIds;
    }

    public boolean checkUserGroup(List<String> groupIds, List<Policy> policies, String targetResource, String targetAction) {
        for (String groupId : groupIds) {
            if (groupId.equals(id)) {
                for (Policy policy : policies) {
                    if (policy.checkPolicy(this.policyIds, targetAction, targetResource)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

class Policy {
    String id;
    List<Statement> statements;

    public Policy(String id, List<Statement> statements) {
        this.id = id;
        this.statements = statements;
    }

    public boolean checkPolicy(List<String> policyIds, String targetResource, String targetAction) {
        for (String policyId : policyIds) {
            if (this.id.equals(policyId)) {
                for (Statement statement : statements) {
                    if (statement.checkActionsAndResources(targetResource, targetAction)) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    public boolean checkActionsAndResources(String targetResource, String targetAction) {
        if (this.actions.contains(targetAction) && this.resources.contains(targetResource)) {
            return true;
        }
        return false;
    }
}