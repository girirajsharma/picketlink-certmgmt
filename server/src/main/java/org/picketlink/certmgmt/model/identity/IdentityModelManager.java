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
package org.picketlink.certmgmt.model.identity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.picketlink.certmgmt.CertificateGeneration;
import org.picketlink.certmgmt.CertificateUtil;
import org.picketlink.certmgmt.model.KeyHolder;
import org.picketlink.certmgmt.model.MyUser;
import org.picketlink.certmgmt.model.Person;
import org.picketlink.certmgmt.model.X509Certificatev1CreationRequest;
import org.picketlink.certmgmt.model.X509Certificatev1DetailResponse;
import org.picketlink.certmgmt.model.X509Certificatev1LoadRequest;
import org.picketlink.certmgmt.model.enums.ApplicationRole;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Role;

/**
 * <p>
 * This class provides an abstraction point to the Identity Management operations required by the application./p>
 *
 * <p>
 * The main objective of this class is avoid the spread use of the <code>IdentityManager</code> by different components of the
 * application and code duplication, providing a centralized point of access for the most common operations like
 * create/update/query users and so forth.
 * </p>
 *
 * <p>
 * Also it is very useful to understand how PicketLink Identity Management is being used and what is being used by the
 * application from a IDM perspective.
 * </p>
 *
 * <p>
 * Please note that PicketLink IDM provides a very flexible and poweful identity model and API, from which you can extend and
 * fulfill your own requirements.
 * </p>
 *
 * @author Pedro Igor
 */
@Stateless
public class IdentityModelManager {

    @Inject
    private IdentityManager identityManager;

    @Inject
    private RelationshipManager relationshipManager;

    @Inject
    private CertificateUtil certificateUtil;

    @Inject
    private CertificateGeneration certificateGeneration;

    @Inject
    private IdentityModelUtil identityModelUtil;

    public MyUser createMyUser(X509Certificatev1CreationRequest request) throws NumberFormatException,
            GeneralSecurityException, IOException {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Insuficient information.");
        }

        String alias = request.getAlias();
        String subjectDN = request.getSubjectDN();
        String keyPassword = request.getKeyPassword();
        String saltedPassword = certificateUtil.saltedHmacMD5("salt", (new String(keyPassword)).getBytes());
        String numberOfDaysOfValidity = request.getNumberOfDaysOfValidity();

        Person person = new Person();

        person.setAlias(alias);
        person.setKeyPassword(saltedPassword);
        person.setSubjectDN(subjectDN);
        person.setNumberOfDaysOfValidity(Integer.parseInt(numberOfDaysOfValidity));

        KeyPair keyPair = this.certificateGeneration.generateKeyPair("RSA");
        String encodedPrivateKey = this.certificateUtil.getEncodedKey(keyPair.getPrivate());
        String encodedPublicKey = this.certificateUtil.getEncodedKey(keyPair.getPublic());
        person.setPrivateKey(encodedPrivateKey);
        person.setPublicKey(encodedPublicKey);

        Certificate cert = certificateGeneration.createX509V1Certificate(keyPair, Integer.parseInt(numberOfDaysOfValidity),
                subjectDN);
        String encodedCert = certificateUtil.getEncodedCertificate(cert);
        person.setCertificate(encodedCert);

        MyUser newUser = new MyUser(keyPassword);
        newUser.setPerson(person);

        return newUser;
    }

    public X509Certificatev1DetailResponse getMyUser(String keyPassword, IdentityManager identityManager) {
        X509Certificatev1DetailResponse response = new X509Certificatev1DetailResponse();
        MyUser myUser = findByKeyPassword(keyPassword, identityManager);
        if (myUser == null) {
            response.setStatus(400);
            return response;
        }

        Person person = myUser.getPerson();
        response.setSubjectDN(person.getSubjectDN());
        response.setAlias(person.getAlias());
        response.setKeyPassword(keyPassword);
        response.setNumberOfDaysOfValidity(person.getNumberOfDaysOfValidity());

        String encodedPublicKey = person.getPublicKey();
        Key publicKey = certificateUtil.getDecodedKey(encodedPublicKey);
        String encodedPrivateKey = person.getPrivateKey();
        Key privateKey = certificateUtil.getDecodedKey(encodedPrivateKey);

        String encodedCertificate = person.getCertificate();
        Certificate certificate = certificateUtil.getDecodedCertificate(encodedCertificate);
        response.setPrivateKey(privateKey);
        response.setPublicKey(publicKey);
        response.setX509Certificatev1(certificate);
        return response;
    }

    public MyUser loadAndStoreMyUser(X509Certificatev1LoadRequest request) throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException, InvalidKeyException {

        String keystoreurl = request.getKeyStoreURL();
        String keystorePass = request.getKeyStorePassword();
        String alias = request.getAlias();
        String keyPasword = request.getKeyPassword();
        String saltedPassword = certificateUtil.saltedHmacMD5("salt", (new String(keyPasword)).getBytes());

        KeyStore keystore = null;
        KeyHolder holder = null;
        InputStream is = IdentityModelManager.class.getClassLoader().getResourceAsStream(keystoreurl);
        if (is == null) {
            try {
                URL keyurl = new URL(keystoreurl);
                is = keyurl.openStream();
            } catch (Exception e) {
                throw new RuntimeException("Unable to load keystore:" + keystoreurl);
            }
        }
        if (is != null) {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, keystorePass.toCharArray());
        }

        if (keystore != null) {
            holder = certificateUtil.getKeyHolder(keystore, alias, keyPasword.toCharArray());
        }

        Person person = new Person();

        person.setAlias(alias);
        person.setKeyPassword(saltedPassword);
        person.setSubjectDN("subjectDN");
        person.setNumberOfDaysOfValidity(Integer.parseInt("numberOfDaysOfValidity"));

        String encodedPrivateKey = this.certificateUtil.getEncodedKey(holder.getPrivateKey());
        String encodedPublicKey = this.certificateUtil.getEncodedKey(holder.getCertificate().getPublicKey());
        person.setPrivateKey(encodedPrivateKey);
        person.setPublicKey(encodedPublicKey);

        String encodedCert = certificateUtil.getEncodedCertificate(holder.getCertificate());
        person.setCertificate(encodedCert);

        MyUser newUser = new MyUser(keyPasword);
        newUser.setPerson(person);

        return newUser;
    }

    public void updatePassword(Account account, String password) {
        this.identityManager.updateCredential(account, new Password(password));
    }

    public void grantRole(MyUser account, ApplicationRole role) {
        Role storedRole = BasicModel.getRole(this.identityManager, role.name());
        BasicModel.grantRole(this.relationshipManager, account, storedRole);
    }

    public MyUser findByLoginName(String alias, IdentityManager identityManager) {
        return identityModelUtil.findUserByAlias(alias, identityManager);
    }

    public MyUser findByKeyPassword(String keyPassword, IdentityManager identityManager) {
        return identityModelUtil.findUserByAlias(keyPassword, identityManager);
    }
}