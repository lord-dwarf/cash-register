package com.polinakulyk.cashregister.security.api;

import com.polinakulyk.cashregister.security.dto.JwtDto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public interface AuthHelper {
    String createJwt(String userId, String role);
    boolean validateJwt(String jwt);
    Optional<String> getJwtFromRequest(HttpServletRequest req);
    JwtDto parseJwt(String jwt);
    List<GrantedAuthority> getAuthRolesFromUserRole(String userRole);
    String getUserRoleFromAuthRoles(Collection<? extends GrantedAuthority> authRoles);
    Authentication getAuthentication();
    void setAuthentication(Authentication authentication);
}
