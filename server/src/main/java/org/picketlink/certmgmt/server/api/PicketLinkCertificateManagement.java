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
package org.picketlink.certmgmt.server.api;

import org.picketlink.certmgmt.server.CertificateGeneration;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.config.IdentityStoreConfiguration;
import org.picketlink.idm.config.JPAIdentityStoreConfiguration;
import org.picketlink.idm.config.LDAPIdentityStoreConfiguration;

import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * API Class to use for CRUD of X509 Certificates
 * @author Anil Saldhana
 * @since March 17, 2014
 */
public class PicketLinkCertificateManagement {
    private CertificateGeneration certificateGeneration = new CertificateGeneration();

    private IdentityManager identityManager;

    public PicketLinkCertificateManagement(IdentityStoreConfiguration identityStoreConfiguration){
        if(identityStoreConfiguration instanceof JPAIdentityStoreConfiguration){
            //JPA
        }else if(identityStoreConfiguration instanceof LDAPIdentityStoreConfiguration){
            //LDAP
        }

        //Instantiate IdentityManager
    }

    /**
     * Create a {@link java.security.cert.X509Certificate}
     * @param keyPair
     * @param numberOfDays number of days of validity
     * @param DN
     * @param version version of the {@link java.security.cert.X509Certificate}
     * @return
     * @throws CertificateException
     */
    public X509Certificate create(KeyPair keyPair, int numberOfDays,String DN, int version) throws CertificateException {
        if(version == 1){
            return (X509Certificate) certificateGeneration.createX509V1Certificate(keyPair,numberOfDays,DN);
        }
        throw new RuntimeException("Unsupported X509 Version");
    }

    /**
     * Store a {@link java.security.cert.X509Certificate} in the data store
     * @param x509Certificate
     * @return
     * @throws java.security.cert.CertificateException
     */
    public boolean store(X509Certificate x509Certificate) throws CertificateException{
        //use identitymanager to store
        return true;
    }

    /**
     * Given a key, return the {@link java.security.cert.X509Certificate}
     * @param key
     * @return
     * @throws java.security.cert.CertificateException
     */
    public X509Certificate get(String key) throws  CertificateException{
        //Use identitymanager to retrieve the cert
        return null;
    }

    /**
     * Given a key and a new {@link java.security.cert.X509Certificate}, update the
     * previously stored {@link java.security.cert.X509Certificate}
     * @param key
     * @param x509Certificate
     * @return true if the update was successful
     * @throws java.security.cert.CertificateException
     */
    public boolean update(String key, X509Certificate x509Certificate) throws  CertificateException{
        //Use identitymanager to update the certificate
        return true;
    }

    /**
     * Delete a {@link java.security.cert.X509Certificate}
     * @param key
     * @return
     * @throws CertificateException
     */
    public boolean delete(String key) throws CertificateException{
        //Use identitymanager to delete the certificate
        return true;
    }
}