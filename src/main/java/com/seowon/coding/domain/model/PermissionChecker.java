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
            if (user.id.equals(userId)) {
				if(user.hasPermission(groups, policies, targetAction, targetResource)) {
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

	public boolean hasPermission(
		List<UserGroup> groups,
		List<Policy> policies,
		String targetAction,
		String targetResource
	){
		for (String groupId : groupIds) {
			for (UserGroup group : groups) {
				if (group.id.equals(groupId)) {
					if(group.checkPolicies(targetAction, targetResource, policies)){
						return true;
					}
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

	public boolean checkPolicies(String action, String resource, List<Policy> policies) {
		for (String policyId : policyIds) {
			for (Policy policy : policies) {
				if (policy.id.equals(policyId)) {
					if(policy.checkStatement(action, resource)) {
						return  true;
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

	public boolean checkStatement(String action, String resource) {
		for (Statement statement : statements) {
			if(statement.hasActionAndResource(action, resource))
				return  true;
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


	public boolean hasActionAndResource(String action, String resource) {
		return actions.contains(action) && resources.contains(resource);
	}
}