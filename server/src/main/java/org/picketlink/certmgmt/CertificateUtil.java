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
package org.picketlink.certmgmt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.enterprise.context.ApplicationScoped;

import org.picketlink.certmgmt.model.KeyHolder;

@ApplicationScoped
public class CertificateUtil {
    public String getEncodedKey(Key key) throws KeyStoreException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(key);
        String encodedKey = Base64.encodeBytes(baos.toByteArray());
        return encodedKey;
    }

    public String getEncodedCertificate(Certificate certificate) throws KeyStoreException, IOException,
            CertificateEncodingException {
        String encodedCert = Base64.encodeBytes(certificate.getEncoded());
        return encodedCert;
    }

    public Key getDecodedKey(String encodedPublicKey) {
        byte[] keyBytes = Base64.decode(encodedPublicKey);
        try {
            ObjectInputStream oos = new ObjectInputStream(new ByteArrayInputStream(keyBytes));
            return (Key) oos.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Certificate getDecodedCertificate(String encodedCertificate) {
        byte[] cert = Base64.decode(encodedCertificate);
        try {
            CertificateFactory fact = CertificateFactory.getInstance("x509");
            return fact.generateCertificate(new ByteArrayInputStream(cert));
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public String saltedHmacMD5(String salt, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(data, 0, data.length);
        String md5 = new BigInteger(1, digest.digest()).toString(16);
        return md5 + salt;
    }

    public KeyHolder getKeyHolder(KeyStore keystore, String alias, char[] password) {
        KeyHolder holder = new KeyHolder();
        try {
            Key key = keystore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                holder.setPrivateKey(key);
                holder.setCertificate(keystore.getCertificate(alias));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return holder;
    }
}
