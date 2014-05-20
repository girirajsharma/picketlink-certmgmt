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
package org.picketlink.certmgmt.api;

import org.picketlink.certmgmt.CertificateGeneration;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.User;

import javax.inject.Inject;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * API Class to use for CRUD of X509 Certificates
 *
 * @author Anil Saldhana
 * @since March 17, 2014
 */
public class PicketLinkCertificateManagement {

    @Inject
    private CertificateGeneration certificateGeneration;

    @Inject
    private IdentityManager identityManager;

    /**
     * Create a {@link java.security.cert.X509Certificate}
     *
     * @param keyPair
     * @param numberOfDays number of days of validity
     * @param DN
     * @param version version of the {@link java.security.cert.X509Certificate}
     * @return
     * @throws CertificateException
     */
    public X509Certificate create(KeyPair keyPair, int numberOfDays, String DN, int version) throws CertificateException {
        if (version == 1) {
            return (X509Certificate) certificateGeneration.createX509V1Certificate(keyPair, numberOfDays, DN);
        }
        throw new RuntimeException("Unsupported X509 Version");
    }

    /**
     * Store a {@link java.security.cert.X509Certificate} in the data store
     *
     * @param x509Certificate
     * @return
     * @throws java.security.cert.CertificateException
     */
    public boolean store(X509Certificate x509Certificate) throws CertificateException {
        User user = new User(x509Certificate.getPublicKey().toString());
        user.setAttribute(new Attribute<X509Certificate>("X509Certificate", x509Certificate));
        identityManager.add(user);
        return true;
    }

    /**
     * Given a key, return the {@link java.security.cert.X509Certificate}
     *
     * @param key
     * @return
     * @throws java.security.cert.CertificateException
     */
    public X509Certificate get(String key) throws CertificateException {
        User user = BasicModel.getUser(identityManager, key);
        if (user != null) {
            Object X509CertificateObject = user.getAttribute("X509Certificate");
            X509Certificate x509Certificate = (X509Certificate) X509CertificateObject;
            return x509Certificate;
        }
        return null;
    }

    /**
     * Given a key and a new {@link java.security.cert.X509Certificate}, update the previously stored
     * {@link java.security.cert.X509Certificate}
     *
     * @param key
     * @param x509Certificate
     * @return true if the update was successful
     * @throws java.security.cert.CertificateException
     */
    public boolean update(String key, X509Certificate x509Certificate) throws CertificateException {
        User user = BasicModel.getUser(identityManager, key);
        if (user != null) {
            user.setAttribute(new Attribute<X509Certificate>("X509Certificate", x509Certificate));
            identityManager.update(user);
            return true;
        }
        return false;
    }

    /**
     * Delete a {@link java.security.cert.X509Certificate}
     *
     * @param key
     * @return
     * @throws CertificateException
     */
    public boolean delete(String key) throws CertificateException {
        // Use identitymanager to delete the certificate
        User user = BasicModel.getUser(identityManager, key);
        if (user != null) {
            identityManager.remove(user);
            return true;
        }
        return false;
    }
}