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
package org.picketlink.certmgmt.model;

import java.io.Serializable;
import java.security.Key;
import java.security.cert.Certificate;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.picketlink.certmgmt.model.enums.ResponseStatus;

/**
 * Object for representing X509 Certificate version1 Creation request
 *
 * @author Giriraj Sharma
 * @since Jan 21, 2014
 */
@XmlRootElement
@JsonSerialize
public class X509Certificatev1DetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;
    private ResponseStatus responseStatus;
    private int numberOfDaysOfValidity;
    private String alias, subjectDN, keyPassword;
    private Key publicKey, privateKey;
    private Certificate X509Certificatev1;

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public int getNumberOfDaysOfValidity() {
        return numberOfDaysOfValidity;
    }

    public void setNumberOfDaysOfValidity(int numberOfDaysOfValidity) {
        this.numberOfDaysOfValidity = numberOfDaysOfValidity;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public Certificate getX509Certificatev1() {
        return X509Certificatev1;
    }

    public void setX509Certificatev1(Certificate x509Certificatev1) {
        X509Certificatev1 = x509Certificatev1;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
