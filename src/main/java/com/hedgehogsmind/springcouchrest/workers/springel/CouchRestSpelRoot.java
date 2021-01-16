package com.hedgehogsmind.springcouchrest.workers.springel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * This class is the reference root for a SpringEL expression parsing.
 * </p>
 *
 * <p>
 * It also implements the most commons spring security SpringEL expressions. This is
 * done by implementing the interface {@link SecurityExpressionOperations}. All methods
 * operate on the current authentication held by the {@link SecurityContextHolder}.
 * There are three methods for security customizations:
 * </p>
 * <ul>
 *     <li>{@link #getAuthenticationTrustResolver()}: has an impact on {@link #isAnonymous()},
 *     {@link #isRememberMe()} and {@link #isFullyAuthenticated()}. Defaults to the standard impl.
 *     {@link AuthenticationTrustResolverImpl}.
 *     </li>
 *
 *     <li>{@link #getPermissionEvaluator()}: has an impact on {@link #hasPermission(Object, Object)}
 *     and {@link #hasPermission(Object, String, Object)}. Defaults to a {@link DenyAllPermissionEvaluator} instance!
 *     </li>
 *
 *     <li>
 *         {@link #getRolePrefix()}: has an impact on {@link #hasRole(String)},
 *         {@link #hasAnyRole(String...)} and {@link #getRoles()}.
 *     </li>
 * </ul>
 */
public class CouchRestSpelRoot
        implements SecurityExpressionOperations {

    private AuthenticationTrustResolver trustResolver =
            new AuthenticationTrustResolverImpl();

    private final PermissionEvaluator permissionEvaluator =
            new DenyAllPermissionEvaluator();

    /**
     * Returns a default {@link AuthenticationTrustResolver} of type
     * {@link AuthenticationTrustResolverImpl}. It is possible that a bean is returned,
     * which has been injected by {@link #setTrustResolver(AuthenticationTrustResolver)}.
     *
     * @return Default {@link AuthenticationTrustResolverImpl} instance.
     */
    public AuthenticationTrustResolver getAuthenticationTrustResolver() {
        return trustResolver;
    }

    /**
     * Returns an default {@link PermissionEvaluator} of type {@link DenyAllPermissionEvaluator}.
     *
     * @return Default {@link DenyAllPermissionEvaluator} instance.
     */
    public PermissionEvaluator getPermissionEvaluator() {
        return permissionEvaluator;
    }

    /**
     * Defaults to 'ROLE_'.
     *
     * @return 'ROLE_'. Used for role checks.
     */
    public String getRolePrefix() {
        return "ROLE_";
    }

    /**
     * Always evaluates to false.
     *
     * @return False.
     */
    public boolean denyAll() {
        return false;
    }

    /**
     * Always evaluates to true.
     *
     * @return True.
     */
    public boolean permitAll() {
        return true;
    }

    /**
     * Fetches the current authentication from the {@link SecurityContextHolder}.
     *
     * @return Current authentication or null if no authentication information is present.
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Convenience method. In case {@link #getAuthentication()} returns a non null
     * authentication instance, then its principal will be returned.
     *
     * @return Principal of authentication or null if there is no authentication.
     */
    public Object getPrincipal() {
        final Authentication authentication = getAuthentication();
        return authentication != null ?
                authentication.getPrincipal() :
                null;
    }

    /**
     * Fetches authentication and retrieves all granted authorities.
     *
     * @return List of granted authorities. Empty if no authentication is present.
     */
    public Set<String> getAuthorities() {
        final Authentication authentication = getAuthentication();

        if (authentication == null) return Collections.emptySet();

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * Appends {@link #getRolePrefix()} to each authority returned by {@link #getAuthorities()}.
     *
     * @return Roles of current authentication. Empty if no authentication is present.
     */
    public Set<String> getRoles() {
        return getAuthorities().stream()
                .map(authority -> getRolePrefix() + authority)
                .collect(Collectors.toSet());
    }

    /**
     * First fetches authentication via {@link #getAuthentication()}. In case it is not null,
     * the given evaluator function will be called and its result will be returned to the caller.
     * But if there is no authentication, the given default value will be returned.
     *
     * @param authenticationEvaluator Function returning boolean evaluation based on authentication.
     * @param defaultValue            Default boolean value to return in case there is no authentication.
     * @return Result of evaluator or default result.
     */
    protected boolean evaluateAuthConditionally(
            final Function<Authentication, Boolean> authenticationEvaluator,
            final boolean defaultValue
    ) {
        final Authentication authentication = getAuthentication();

        return authentication != null ?
                authenticationEvaluator.apply(authentication) :
                defaultValue;
    }

    /**
     * Checks if the current authentication has the given authority.
     *
     * @param authority Authority to check for.
     * @return True of the authentication has the authority. False if not or no authentication is present.
     */
    @Override
    public boolean hasAuthority(String authority) {
        return getAuthorities().contains(authority);
    }

    /**
     * Checks if the current authentication has any of the given authorities.
     *
     * @param authorities Authorities to check for.
     * @return True if the authentication has any of the given authorities. False if not or no authentication is present.
     */
    @Override
    public boolean hasAnyAuthority(String... authorities) {
        final Set<String> grantedAuthorities = getAuthorities();

        for (final String authority : authorities) {
            if (grantedAuthorities.contains(authority)) return true;
        }

        return false;
    }

    /**
     * Checks if {@link #getRoles()} contains the given role.
     *
     * @param role Role to check for.
     * @return True if current authentication has given role. Otherwise false.
     */
    @Override
    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    /**
     * Checks if {@link #getRoles()} contains one of the given roles.
     *
     * @param roles Roles to check for.
     * @return True if the current authentication has at least one of the given roles. Otherwise false.
     */
    @Override
    public boolean hasAnyRole(String... roles) {
        final Set<String> grantedRoles = getRoles();

        for (final String role : roles) {
            if (grantedRoles.contains(role)) return true;
        }

        return false;
    }

    /**
     * Calls {@link #getAuthenticationTrustResolver()}'s method
     * {@link AuthenticationTrustResolver#isAnonymous(Authentication)}
     * with {@link #getAuthentication()}.
     *
     * @return True if current authentication is anonymous according to trust resolver.
     */
    @Override
    public boolean isAnonymous() {
        return getAuthenticationTrustResolver().isAnonymous(getAuthentication());
    }

    /**
     * Checks if the current authentication is authenticated.
     *
     * @return True if current authentication is present and authenticated. Otherwise false.
     */
    @Override
    public boolean isAuthenticated() {
        return evaluateAuthConditionally(
                Authentication::isAuthenticated,
                false
        );
    }

    /**
     * Calls {@link #getAuthenticationTrustResolver()}'s Method {@link AuthenticationTrustResolver#isRememberMe(Authentication)}
     * for {@link #getAuthentication()}.
     *
     * @return True if trust resolver's isRememberMe() evaluates to true. Otherwise false.
     */
    @Override
    public boolean isRememberMe() {
        return getAuthenticationTrustResolver().isRememberMe(getAuthentication());
    }

    /**
     * Checks if current authentication is neither anonymous nor isRememberMe() applies for it.
     *
     * @return True if not anonymous and remember me is disabled. Otherwise false.
     */
    @Override
    public boolean isFullyAuthenticated() {
        final Authentication authentication = getAuthentication();

        return !getAuthenticationTrustResolver().isAnonymous(authentication) &&
                !getAuthenticationTrustResolver().isRememberMe(authentication);
    }

    /**
     * Calls {@link #getPermissionEvaluator()}'s method
     * {@link PermissionEvaluator#hasPermission(Authentication, Object, Object)}.
     *
     * @param target     Target.
     * @param permission Permission.
     * @return Result of PermissionEvaluator.
     */
    @Override
    public boolean hasPermission(Object target, Object permission) {
        return getPermissionEvaluator().hasPermission(
                getAuthentication(),
                target,
                permission
        );
    }

    /**
     * Calls {@link #getPermissionEvaluator()}'s method
     * {@link PermissionEvaluator#hasPermission(Authentication, Serializable, String, Object)}.
     *
     * @param targetId   TargetId.
     * @param targetType TargetType.
     * @param permission Permission.
     * @return Result of PermissionEvaluator.
     */
    @Override
    public boolean hasPermission(Object targetId, String targetType, Object permission) {
        return getPermissionEvaluator().hasPermission(
                getAuthentication(),
                (Serializable) targetId,
                targetType,
                permission
        );
    }

    @Autowired(required = false)
    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

}
