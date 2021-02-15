package com.polinakulyk.cashregister.security.dto;

import java.util.Optional;
import org.springframework.util.Assert;

public enum UserRole {

    MERCH,
    TELLER,
    SR_TELLER;

    /**
     * Finds the existing enum UserRole value by a given string.
     *
     * Case-insensitive.
     *
     * @param userRoleStr
     * @return
     */
    public static Optional<UserRole> fromString(String userRoleStr) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.toString().equals(userRoleStr.toUpperCase())) {
                return Optional.of(userRole);
            }
        }
        return Optional.empty();
    }

    /**
     * Class with String constants for user roles.
     *
     * The purpose is using it in @RolesAllowed annotations (constant String is required).
     */
    public static class Value {

        public static final String MERCH = "MERCH";
        public static final String TELLER = "TELLER";
        public static final String SR_TELLER = "SR_TELLER";

        private Value() {
            throw new UnsupportedOperationException();
        }
    }
}
