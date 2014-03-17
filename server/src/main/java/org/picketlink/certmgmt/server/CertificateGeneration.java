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
package org.picketlink.certmgmt.server;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;

/**
 * Utility class using BouncyCastle to deal with {@link Certificate} operations
 *
 * @author anil saldhana
 * @since Aug 16, 2012
 */
public class CertificateGeneration {

    private static SecureRandom random = new SecureRandom();

    static {
        SecurityActions.addProvider(new BouncyCastleProvider());
    };

    /**
     * Create a X509 V1 {@link Certificate}
     *
     * @param pair {@link KeyPair}
     * @param numberOfDays Number of days the certificate will be valid
     * @param DN The DN of the subject
     * @return
     * @throws CertificateException
     */
    public Certificate createX509V1Certificate(KeyPair pair, int numberOfDays, String DN) throws CertificateException {
        try {
            AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
            AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

            AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory.createKey(pair.getPrivate().getEncoded());
            SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded());

            ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyAsymKeyParam);

            Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            Date endDate = new Date(System.currentTimeMillis() + numberOfDays * 24 * 60 * 60 * 1000);

            X500Name name = new X500Name(DN);

            BigInteger serialNum = createSerialNumber();
            X509v1CertificateBuilder v1CertGen = new X509v1CertificateBuilder(name, serialNum, startDate, endDate, name,
                    subPubKeyInfo);

            X509CertificateHolder certificateHolder = v1CertGen.build(sigGen);
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateHolder);
        } catch (CertificateException e1) {
            throw e1;
        } catch (Exception e) {
            throw new CertificateException(e);
        }
    }

    /**
     * Create a certificate signing request
     *
     * @throws CertificateException
     */
    public byte[] createCSR(String dn, KeyPair keyPair) throws CertificateException {
        X500Name name = new X500Name(dn);
        try {

            AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
            AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

            AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());

            ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyAsymKeyParam);

            SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
            PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(name, subPubKeyInfo);
            PKCS10CertificationRequest csr = builder.build(sigGen);
            return csr.getEncoded();
        } catch (Exception e) {
            throw new CertificateException(e);
        }
    }

    /**
     * Get the CSR as a PEM formatted String
     *
     * @param csrEncoded
     * @return
     * @throws IOException
     */
    public String getPEM(byte[] csrEncoded) throws IOException {
        String type = "CERTIFICATE REQUEST";

        PemObject pemObject = new PemObject(type, csrEncoded);

        StringWriter str = new StringWriter();
        PEMWriter pemWriter = new PEMWriter(str);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        str.close();
        return str.toString();
    }

    /**
     * Generate a Key Pair
     *
     * @param algo (RSA, DSA etc)
     * @return
     * @throws GeneralSecurityException
     */
    public KeyPair generateKeyPair(String algo) throws GeneralSecurityException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algo);
        return kpg.genKeyPair();
    }

    /**
     * Create a random serial number
     *
     * @return
     * @throws GeneralSecurityException
     */
    public BigInteger createSerialNumber() throws GeneralSecurityException {
        BigInteger bi = new BigInteger(4, random);
        return bi;
    }
}
