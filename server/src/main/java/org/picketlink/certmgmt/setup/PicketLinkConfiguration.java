/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.certmgmt.setup;

import org.picketlink.certmgmt.model.MyUser;
import org.picketlink.certmgmt.model.entity.MyUserTypeEntity;
import org.picketlink.idm.config.IdentityConfiguration;
import org.picketlink.idm.config.IdentityConfigurationBuilder;
import org.picketlink.idm.jpa.model.sample.simple.AttributeTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.GroupTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.IdentityTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.PartitionTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.PasswordCredentialTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RelationshipIdentityTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RelationshipTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RoleTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.TokenCredentialTypeEntity;
import org.picketlink.internal.EEJPAContextInitializer;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Pedro Igor
 */
public class PicketLinkConfiguration {

    @Inject
    private EEJPAContextInitializer contextInitializer;

    /**
     * <p>We need to manually produce a {@link org.picketlink.idm.config.IdentityConfiguration} because we must
     * provide our custom identity types, such as {@link org.picketlink.certmgmt.model.MyUser}.</p>
     *
     * @return
     */
    @Produces
    public IdentityConfiguration produceIdentityConfiguration() {
        IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();

        builder
            .named("default.config")
                .stores()
                    .jpa()
                        .mappedEntity(
                            PartitionTypeEntity.class,
                            RoleTypeEntity.class,
                            GroupTypeEntity.class,
                            IdentityTypeEntity.class,
                            RelationshipTypeEntity.class,
                            RelationshipIdentityTypeEntity.class,
                            PasswordCredentialTypeEntity.class,
                            TokenCredentialTypeEntity.class,
                            AttributeTypeEntity.class,
                            MyUserTypeEntity.class)
                        .addContextInitializer(this.contextInitializer)
                        .supportType(MyUser.class)
                        .supportAllFeatures();

        return builder.build();
    }

}
