/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.picketlink.certmgmt.setup;

import org.jboss.logging.Logger;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.web.BasicAuthenticationScheme;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * <p>
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 * <p/>
 */
public class Resources {

    @Produces
    @PicketLink
    @PersistenceContext(unitName = "picketlink-certmgmt")
    private EntityManager em;

    @Inject
    private BasicAuthenticationScheme basicAuthenticationScheme;

    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    /**
     * <p>Returns the HTTP Authentication Scheme that should be used to authenticate users.</p>
     * @return
     */
    @Produces
    @PicketLink
    public BasicAuthenticationScheme produceHttpAuthenticationScheme() {
        return this.basicAuthenticationScheme;
    }
}
