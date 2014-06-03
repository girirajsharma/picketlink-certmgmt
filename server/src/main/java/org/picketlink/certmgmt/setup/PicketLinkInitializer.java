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
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * @author Pedro Igor
 */
@Singleton
@Startup
public class PicketLinkInitializer {

    @Inject
    private IdentityManager identityManager;

    @PostConstruct
    public void init() {
        MyUser admin = new MyUser("admin");

        this.identityManager.add(admin);

        Password password = new Password("admin");

        this.identityManager.updateCredential(admin, password);
    }
}
