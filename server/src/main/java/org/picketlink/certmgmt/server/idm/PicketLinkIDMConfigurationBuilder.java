/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.picketlink.certmgmt.server.idm;

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

/**
 * API Class to use for CRUD of X509 Certificates
 *
 * @author Giriraj Sharma
 * @since March 20, 2014
 */
@ApplicationScoped
public class PicketLinkIDMConfigurationBuilder {

    @Inject
    private EEJPAContextInitializer contextInitializer;

    @PicketLink
    @Produces
    public IdentityConfigurationBuilder produceJPAConfigurationBuilder() {

        IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();
        builder.named("jpa.config")
                .stores()
                .jpa()
                .mappedEntity(AccountTypeEntity.class, RoleTypeEntity.class, GroupTypeEntity.class, IdentityTypeEntity.class,
                        RelationshipTypeEntity.class, RelationshipIdentityTypeEntity.class, PartitionTypeEntity.class,
                        PasswordCredentialTypeEntity.class, DigestCredentialTypeEntity.class, X509CredentialTypeEntity.class,
                        OTPCredentialTypeEntity.class, AttributeTypeEntity.class)
                .addContextInitializer(this.contextInitializer).supportAllFeatures();

        return builder;
    }

    @PicketLink
    @Produces
    public IdentityConfigurationBuilder produceLDAPConfigurationBuilder() {

        IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();
        builder.named("ldap.config").stores().ldap()
        // configuration options for the ldap store
                .addContextInitializer(this.contextInitializer).supportAllFeatures();

        return builder;
    }
}
