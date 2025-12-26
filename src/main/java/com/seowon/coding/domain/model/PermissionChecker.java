package com.seowon.coding.domain.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;

class PermissionChecker {

    public static boolean hasPermission(
            String userId,
            String targetResource,
            String targetAction,
            List<User> users,
            List<UserGroup> groups,
            List<Policy> policies
    ) {
        Map<String, User> userMap = buildUserMap(users);
        Map<String, UserGroup> groupMap = buildUserGroupMap(groups);
        Map<String, Policy> policyMap = buildPolicyMap(policies);

        User user = userMap.get(userId);
        if (user == null) {
            return false;
        }

        for (String groupId : user.groupIds) {
            UserGroup group = groupMap.get(groupId);
            if (group == null) {
                continue;
            }

            for (String policyId : group.policyIds) {
                Policy policy = policyMap.get(policyId);
                if (policy == null) {
                    continue;
                }

                if (hasMatchStatement(policy, targetAction, targetResource)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Map<String, User> buildUserMap(List<User> users) {
        Map<String, User> map = new HashMap<>();
        for (User user : users) {
            map.put(user.id, user);
        }
        return map;
    }

    private static Map<String, UserGroup> buildUserGroupMap(List<UserGroup> userGroups) {
        Map<String, UserGroup> map = new HashMap<>();
        for (UserGroup group : userGroups) {
            map.put(group.id, group);
        }

        return map;
    }

    private static Map<String, Policy> buildPolicyMap(List<Policy> policies) {
        Map<String, Policy> map = new HashMap<>();
        for (Policy policy : policies) {
            map.put(policy.id, policy);
        }

        return map;
    }

    private static boolean hasMatchStatement(Policy policy, String targetAction, String targetResource) {
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