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
package org.picketlink.certmgmt.rest;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.picketlink.certmgmt.CertificateGeneration;
import org.picketlink.certmgmt.CertificateUtil;
import org.picketlink.certmgmt.api.PicketLinkCertificateManagement;
import org.picketlink.certmgmt.model.X509Certificatev1CreationRequest;
import org.picketlink.certmgmt.model.X509Certificatev1DetailResponse;
import org.picketlink.certmgmt.model.X509Certificatev1Response;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.User;

///**
// * Endpoint for User Account Registration
// * 
// * @author Giriraj Sharma
// * @since May 05, 2014
// */
@Stateless
@Path("/X509v1Certificate")
public class X509Certificatev1Endpoint {

    @Inject
    private IdentityManager identityManager;

    @Inject
    private PicketLinkCertificateManagement picketLinkCertificateManagement;

    @Inject
    private CertificateGeneration certificateGeneration;

    @Inject
    private CertificateUtil certificateUtil;

    // /**
    // * Register an user account
    // *
    // * @param request
    // * @return
    // */

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public X509Certificatev1Response create(X509Certificatev1CreationRequest request) throws Exception {
        X509Certificatev1Response response = new X509Certificatev1Response();

        String alias = request.getAlias();
        String subjectDN = request.getSubjectDN();
        String keyPassword = request.getKeyPassword();
        String saltedPassword = certificateUtil.saltedHmacMD5("salt", (new String(keyPassword)).getBytes());
        String numberOfDaysOfValidity = request.getNumberOfDaysOfValidity();
        int version = 1;

        User user = BasicModel.getUser(identityManager, keyPassword);
        if (user == null) {
            // Alias is not already registered
            user = new User(keyPassword);

            user.setAttribute(new Attribute<String>("alias", alias));
            user.setAttribute(new Attribute<String>("subjectDN", subjectDN));
            user.setAttribute(new Attribute<String>("password", saltedPassword));
            user.setAttribute(new Attribute<String>("numberOfDaysOfValidity", numberOfDaysOfValidity));

            KeyPair keyPair = certificateGeneration.generateKeyPair("RSA");
            String encodedPrivateKey = certificateUtil.getEncodedKey(keyPair.getPrivate());
            String encodedPublicKey = certificateUtil.getEncodedKey(keyPair.getPublic());

            user.setAttribute(new Attribute<String>("publicKey", encodedPublicKey));
            user.setAttribute(new Attribute<String>("privateKey", encodedPrivateKey));

            Certificate cert = picketLinkCertificateManagement.create(keyPair, Integer.parseInt(numberOfDaysOfValidity),
                    subjectDN, version);
            String encodedCert = certificateUtil.getEncodedCertificate(cert);
            user.setAttribute(new Attribute<String>("X509v1Certificate", encodedCert));
            this.identityManager.add(user);
            // this.identityManager.updateCredential(user, new Password(request.getPassword()));

            response.setStatus(200);
            response.setState("CREATED");
        } else {
            // Alias is already registered
            response.setStatus(400);
            response.setState("FAILED");
        }

        return response;
    }

    @GET
    @Path("/{keyPassword}/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    public X509Certificatev1DetailResponse get(@PathParam("keyPassword") String keyPassword, @PathParam("alias") String alias)
            throws InvalidKeyException, NoSuchAlgorithmException {
        X509Certificatev1DetailResponse response = new X509Certificatev1DetailResponse();
        User user = BasicModel.getUser(identityManager, keyPassword);
        if (user == null) {
            response.setStatus(400);
            return response;
        }

        String trueAlias = String.valueOf(user.getAttribute("alias"));
        if (!alias.equals(trueAlias)) {
            response.setStatus(400);
            return response;
        }

        response.setStatus(200);
        response.setSubjectDN(String.valueOf(user.getAttribute("subjectDN")));
        response.setAlias(String.valueOf(user.getAttribute("alias")));
        response.setKeyPassword(keyPassword);
        response.setNumberOfDaysOfValidity(String.valueOf(user.getAttribute("validity")));

        String encodedPublicKey = String.valueOf(user.getAttribute("publicKey"));
        Key publicKey = certificateUtil.getDecodedKey(encodedPublicKey);
        String encodedPrivateKey = String.valueOf(user.getAttribute("privateKey"));
        Key privateKey = certificateUtil.getDecodedKey(encodedPrivateKey);

        String encodedCertificate = String.valueOf(user.getAttribute("publicKey"));
        Certificate certificate = certificateUtil.getDecodedCertificate(encodedCertificate);
        response.setPrivateKey(privateKey);
        response.setPublicKey(publicKey);
        response.setX509Certificatev1(certificate);
        return response;

    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public X509Certificatev1Response update(X509Certificatev1CreationRequest request) throws Exception {
        X509Certificatev1Response response = new X509Certificatev1Response();
        String alias = request.getAlias();
        String subjectDN = request.getSubjectDN();
        String keyPassword = request.getKeyPassword();
        String numberOfDaysOfValidity = request.getNumberOfDaysOfValidity();
        int version = 1;

        User user = BasicModel.getUser(identityManager, keyPassword);
        if (user == null) {
            response.setStatus(400);
            return response;
        }

        user.setAttribute(new Attribute<String>("alias", alias));
        user.setAttribute(new Attribute<String>("subjectDN", subjectDN));
        // user.setAttribute(new Attribute<String>("password", saltedPassword));
        user.setAttribute(new Attribute<String>("numberOfDaysOfValidity", numberOfDaysOfValidity));

        KeyPair keyPair = certificateGeneration.generateKeyPair("RSA");
        String encodedPrivateKey = certificateUtil.getEncodedKey(keyPair.getPrivate());
        String encodedPublicKey = certificateUtil.getEncodedKey(keyPair.getPublic());

        user.setAttribute(new Attribute<String>("publicKey", encodedPublicKey));
        user.setAttribute(new Attribute<String>("privateKey", encodedPrivateKey));

        Certificate cert = picketLinkCertificateManagement.create(keyPair, Integer.parseInt(numberOfDaysOfValidity), subjectDN,
                version);
        String encodedCert = certificateUtil.getEncodedCertificate(cert);
        user.setAttribute(new Attribute<String>("X509v1Certificate", encodedCert));

        this.identityManager.update(user);
        // this.identityManager.updateCredential(user, new Password(request.getPassword()));

        response.setStatus(200);
        response.setState("UPDATED");
        return response;
    }

    @DELETE
    @Path("/{keyPassword}")
    @Produces(MediaType.APPLICATION_JSON)
    public X509Certificatev1Response delete(@PathParam("keyPassword") String keyPassword) throws Exception {
        X509Certificatev1Response response = new X509Certificatev1Response();
        User user = BasicModel.getUser(identityManager, keyPassword);
        if (user == null) {
            response.setStatus(400);
            return response;
        }
        picketLinkCertificateManagement.delete(keyPassword);
        response.setStatus(200);
        response.setState("DELETED");
        return response;
    }
}