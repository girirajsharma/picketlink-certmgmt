package org.picketlink.certmgmt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.enterprise.context.ApplicationScoped;

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
        // Create MessageDigest object for MD5
        MessageDigest digest = MessageDigest.getInstance("MD5");

        // Update input string in message digest
        digest.update(data, 0, data.length);

        // Converts message digest value in base 16 (hex)
        String md5 = new BigInteger(1, digest.digest()).toString(16);

        return md5 + salt;
    }
}
