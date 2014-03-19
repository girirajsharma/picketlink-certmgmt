package org.picketlink.certmgmt.server.api;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.picketlink.annotations.PicketLink;
import org.picketlink.idm.config.IdentityConfigurationBuilder;
import org.picketlink.idm.jpa.model.sample.simple.AccountTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.AttributeTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.DigestCredentialTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.GroupTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.IdentityTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.OTPCredentialTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.PartitionTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.PasswordCredentialTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RelationshipIdentityTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RelationshipTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RoleTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.X509CredentialTypeEntity;
import org.picketlink.internal.EEJPAContextInitializer;

@ApplicationScoped
public class PicketLinkIDMConfigurationBuilder {

    @Inject
    private EEJPAContextInitializer contextInitializer;

    @PicketLink
    @Produces
    public IdentityConfigurationBuilder produceJPAConfigurationBuilder() {

        IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();
        builder
            .named("jpa.config")
            .stores()
            .jpa()
            .mappedEntity(
                AccountTypeEntity.class,
                RoleTypeEntity.class,
                GroupTypeEntity.class,
                IdentityTypeEntity.class,
                RelationshipTypeEntity.class,
                RelationshipIdentityTypeEntity.class,
                PartitionTypeEntity.class,
                PasswordCredentialTypeEntity.class,
                DigestCredentialTypeEntity.class,
                X509CredentialTypeEntity.class,
                OTPCredentialTypeEntity.class,
                AttributeTypeEntity.class
            )
            .addContextInitializer(this.contextInitializer)
            .supportAllFeatures();

        return builder;
    }

    @PicketLink
    @Produces
    public IdentityConfigurationBuilder produceLDAPConfigurationBuilder() {

        IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();
        builder
            .named("ldap.config")
            .stores()
            .ldap()
            // configuration options for the ldap store
            .addContextInitializer(this.contextInitializer)
            .supportAllFeatures();

        return builder;
    }
}
