package com.agency.dashboard.security;

import com.agency.dashboard.domain.User;
import com.agency.dashboard.domain.UserRole;

public class AccessControl {

    private AccessControl() {
    }

    public static boolean canAccessDesign(User user) {
        return hasAnyRole(user, UserRole.ADMIN, UserRole.MANAGEMENT, UserRole.DESIGN);
    }

    public static boolean canAccessTraffic(User user) {
        return hasAnyRole(user, UserRole.ADMIN, UserRole.MANAGEMENT, UserRole.TRAFFIC);
    }

    public static boolean canAccessCommercial(User user) {
        return hasAnyRole(user, UserRole.ADMIN, UserRole.MANAGEMENT, UserRole.COMMERCIAL);
    }

    public static boolean canAccessManagement(User user) {
        return hasAnyRole(user, UserRole.ADMIN, UserRole.MANAGEMENT);
    }

    public static boolean canAccessUsers(User user) {
        return hasAnyRole(user, UserRole.ADMIN, UserRole.MANAGEMENT);
    }

    public static boolean hasAnyRole(User user, UserRole... roles) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        for (UserRole role : roles) {
            if (user.getRole() == role) {
                return true;
            }
        }

        return false;
    }
}