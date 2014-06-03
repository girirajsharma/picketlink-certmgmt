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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Object for representing X509 Certificate version1 Creation request
 *
 * @author Giriraj Sharma
 * @since Jan 21, 2014
 */
@XmlRootElement
@JsonSerialize
public class X509Certificatev1CreationRequest implements Serializable {
    private static final long serialVersionUID = 2568144752362123865L;

    private String alias, subjectDN, numberOfDaysOfValidity, keyPassword;

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

    public String getNumberOfDaysOfValidity() {
        return numberOfDaysOfValidity;
    }

    public void setNumberOfDaysOfValidity(String numberOfDaysOfValidity) {
        this.numberOfDaysOfValidity = numberOfDaysOfValidity;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public boolean isValid() {
        return this.alias != null && this.subjectDN != null && this.numberOfDaysOfValidity != null && this.keyPassword != null;
    }
}