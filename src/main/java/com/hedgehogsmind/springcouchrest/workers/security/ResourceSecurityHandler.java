package com.hedgehogsmind.springcouchrest.workers.security;

import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouchrest.workers.security.exceptions.AccessForbiddenException;
import org.springframework.expression.Expression;

import java.util.Optional;

/**
 * Provides handling base security rule and an generic method to check endpoint level security rules
 * for a {@link MappedResource}.
 */
public class ResourceSecurityHandler {

    private final MappedResource resourceToSecure;

    /**
     * Stores resource.
     *
     * @param resourceToSecure Resource which all check relate to.
     */
    public ResourceSecurityHandler(MappedResource resourceToSecure) {
        this.resourceToSecure = resourceToSecure;
    }

    /**
     * Returns resource to secure.
     * @return Resource to secure.
     */
    protected MappedResource getResourceToSecure() {
        return resourceToSecure;
    }

    /**
     * Convenience method: Retrieves core of {@link #getResourceToSecure()}.
     *
     * @return Related CouchRestCore.
     */
    protected CouchRestCore getCore() {
        return resourceToSecure.getCore();
    }

    /**
     * Checks flag of {@link com.hedgehogsmind.springcouchrest.annotations.CouchRest} annotation
     * found on the mapped element.
     *
     * @return Flag of {@link com.hedgehogsmind.springcouchrest.annotations.CouchRest} annotation.
     */
    public boolean isBaseSecurityRuleCheckNeeded() {
        return resourceToSecure.getMappingSource().getTagAnnotation().checkBaseSecurityRule();
    }

    /**
     * In case {@link #isBaseSecurityRuleCheckNeeded()} is true and
     * {@link CouchRestCore#evaluateBaseSecurityRule()} returns false, an {@link AccessForbiddenException}
     * will be thrown.
     *
     * @throws AccessForbiddenException if base security rule shall be checked but evaluated to false.
     */
    public void assertBaseSecurityRuleIfNecessary() {
        if ( isBaseSecurityRuleCheckNeeded() && !resourceToSecure.getCore().evaluateBaseSecurityRule() ) {
            throw new AccessForbiddenException();
        }
    }

    /**
     * Calls {@link #assertBaseSecurityRuleIfNecessary()} and afterwards checks endpoint level security rule.
     * If the given rule is present it will be checked. If not, then
     * {@link CouchRestCore#evaluateDefaultEndpointSecurityRule()} will be used. If access is denied,
     * an {@link AccessForbiddenException} will be thrown.
     *
     * @param optionalEndpointSecurityRule Optional overwriting endpoint level security rule.
     * @throws AccessForbiddenException if rules evaluate to false (access denied).
     */
    public void assertBaseAndEndpointLevelRule(final Optional<Expression> optionalEndpointSecurityRule) {
        assertBaseSecurityRuleIfNecessary();

        final boolean access = optionalEndpointSecurityRule.isPresent() ?
                resourceToSecure.getCore().evaluateExpression(optionalEndpointSecurityRule.get(), Boolean.class) :
                resourceToSecure.getCore().evaluateDefaultEndpointSecurityRule();

        if ( !access ) {
            throw new AccessForbiddenException();
        }
    }

}
