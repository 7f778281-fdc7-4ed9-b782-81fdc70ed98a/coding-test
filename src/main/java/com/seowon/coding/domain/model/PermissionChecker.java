package com.seowon.coding.domain.model;


import java.util.List;
import lombok.Builder;

class PermissionChecker {

    /**
     * TODO #7: 코드를 최적화하세요
     * 테스트 코드`PermissionCheckerTest`를 활용하시면 리펙토링에 도움이 됩니다.
     */
//    public static boolean hasPermission(
//            String userId,
//            String targetResource,
//            String targetAction,
//            List<User> users,
//            List<UserGroup> groups,
//            List<Policy> policies
//    ) {
//        for (User user : users) {
//            if (user.id.equals(userId)) {   // userId가 맞는 경우
//                for (String groupId : user.groupIds) {  // 각 그룹 id를 뽑고
//                    for (UserGroup group : groups) {    // 그룹 반복
//                        if (group.id.equals(groupId)) { // groupId가 맞는 경우
//                            for (String policyId : group.policyIds) {   // 정책 id 뽑고
//                                for (Policy policy : policies) {    // 정책 반복
//                                    if (policy.id.equals(policyId)) {   // policyId가 맞는 경우
//                                        for (Statement statement : policy.statements) { // 상태 반복
//                                            if (statement.actions.contains(targetAction) && // action과 resource가 같으면 true
//                                                statement.resources.contains(targetResource)) {
//                                                return true;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
    public static boolean hasPermission(
            String userId,
            String targetResource,
            String targetAction,
            List<User> users,
            List<UserGroup> groups,
            List<Policy> policies
    ) {
//        for (User user : users) {
//            if (user.id.equals(userId)) {   // userId가 맞는 경우
//                for (String groupId : user.groupIds) {  // 각 그룹 id를 뽑고
//                    for (UserGroup group : groups) {    // 그룹 반복
//                        if (group.id.equals(groupId)) { // groupId가 맞는 경우
//                            for (String policyId : group.policyIds) {   // 정책 id 뽑고
//                                for (Policy policy : policies) {    // 정책 반복
//                                    if (policy.id.equals(policyId)) {   // policyId가 맞는 경우
//                                        for (Statement statement : policy.statements) { // 상태 반복
//                                            if (statement.actions.contains(targetAction) &&
//                                                    // action과 resource가 같으면 true
//                                                    statement.resources.contains(targetResource)) {
//                                                return true;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

        for (User user : users) {
            if (!user.id.equals(userId)) {
                return false;
            }
            for (String groupId : user.groupIds) {
                for (UserGroup group : groups) {
                    if (!group.id.equals(groupId)) {
                        return false;
                    }
                }
            }

        }

        for (UserGroup group : groups) {
            for (String policyId : group.policyIds) {
                for (Policy policy : policies) {
                    if (!policy.id.equals(policyId)) {
                        return false;
                    }

                }
            }
        }

        for (Policy policy : policies) {
            for (Statement statement : policy.statements) {
                if (!statement.actions.contains(targetAction) ||
                        !statement.resources.contains(targetResource)) {
                    return true;
                }
            }
        }

        return true;
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