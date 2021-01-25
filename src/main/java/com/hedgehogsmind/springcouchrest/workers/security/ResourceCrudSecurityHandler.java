package com.hedgehogsmind.springcouchrest.workers.security;

import com.hedgehogsmind.springcouchrest.annotations.security.CrudSecurity;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntityResource;
import org.springframework.expression.Expression;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * This class represents a handler, which is capable of checking crud security rules for a {@link MappedEntityResource}.
 * In any case, the base security rule {@link CouchRestConfiguration#getBaseSecurityRule()} must be true.
 * </p>
 *
 * <p>
 * Then endpoint security rules will be checked. If a crud method has no security method defined via
 * the annotation {@link CrudSecurity}, then {@link CouchRestConfiguration#getDefaultEndpointSecurityRule()}
 * will be used instead.
 * </p>
 */
public class ResourceCrudSecurityHandler extends ResourceSecurityHandler {

    private final Optional<Expression> readRule;

    private final Optional<Expression> saveUpdateRule;

    private final Optional<Expression> deleteRule;

    /**
     * Extracts the core to later evaluate the base security rule and extracts optional overwriting
     * rules (defined via an {@link CrudSecurity} annotation). Max. 1 such annotation is allowed.
     *
     * @param entityResource Entity resource which this security handler shall handle.
     */
    public ResourceCrudSecurityHandler(final MappedEntityResource entityResource) {
        super(entityResource);

        final Optional<CrudSecurity> crudSecurityAnnotation =
                entityResource
                        .getMappingSource()
                        .getOptionalCouchRestModifierAnnotation(CrudSecurity.class);

        if ( crudSecurityAnnotation.isPresent() ) {
            final CrudSecurity securityRules = crudSecurityAnnotation.get();

            readRule = securityRules.read().isBlank() ?
                    Optional.empty() :
                    Optional.of(getCore().parseSpelExpression(securityRules.read()));

            saveUpdateRule = securityRules.saveUpdate().isBlank() ?
                    Optional.empty() :
                    Optional.of(getCore().parseSpelExpression(securityRules.saveUpdate()));

            deleteRule = securityRules.delete().isBlank() ?
                    Optional.empty() :
                    Optional.of(getCore().parseSpelExpression(securityRules.delete()));

        } else {
            readRule = Optional.empty();
            saveUpdateRule = Optional.empty();
            deleteRule = Optional.empty();
        }
    }

    /**
     * Calls {@link #assertBaseAndEndpointLevelRule(Optional)} with optional read rule.
     */
    public void assertReadAccess() {
        assertBaseAndEndpointLevelRule(readRule);
    }

    /**
     * Calls {@link #assertBaseAndEndpointLevelRule(Optional)} with optional saveUpdate rule.
     */
    public void assertSaveUpdateAccess() {
        assertBaseAndEndpointLevelRule(saveUpdateRule);
    }

    /**
     * Calls {@link #assertBaseAndEndpointLevelRule(Optional)} with optional delete rule.
     */
    public void assertDeleteAccess() {
        assertBaseAndEndpointLevelRule(deleteRule);
    }

}
