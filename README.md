PicketLink Certificate Management System
========================================

This project contains the PicketLink Certificate Management System.


The Modules are:
---------------

server module
This module contains the core classes for the system.

client module
This module contains all the css, js and web related classes and scripts

ear module
This module contains the packaging of the system as an EAR.


Building:
--------

mvn clean package


Deploying to JBoss EAP 6.2:
-------------------------
mvn clean install


Additional Information:
----------------------
You can download JBoss EAP 6 from http://www.jboss.org for free developer version.

Short Primer on File Formats:
---------------------------
Picked up from: http://serverfault.com/questions/9708/what-is-a-pem-file-and-how-does-it-differ-from-other-openssl-generated-key-file


     .csr This is a Certificate Signing Request. Some applications can generate these for submission to certificate-authorities. It includes some/all of the key details of the requested certificate such as subject, organization, state, whatnot, as well as the public key of the certificate to get signed. These get signed by the CA and a certificate is returned. The returned certificate is the public certificate, which itself can be in a couple of formats.

     .pem Defined in RFC's 1421 through 1424, this is a container format that may include just the public certificate (such as with Apache installs, and CA certificate files /etc/ssl/certs), or may include an entire certificate chain including public key, private key, and root certificates. The name is from Privacy Enhanced Email, a failed method for secure email but the container format it used lives on.
     
      .key This is a PEM formatted file containing just the private-key of a specific certificate. In Apache installs, this frequently resides in /etc/ssl/private. The rights on this directory and the certificates is very important, and some programs will refuse to load these certificates if they are set wrong.

     .pkcs12 .pfx .p12 Originally defined by RSA in the Public-Key Cryptography Standards, the "12" variant was enhanced by Microsoft. This is a passworded container format that contains both public and private certificate pairs. Unlike .pem files, this container is fully encrypted. Every time I get one I have to google to remember the openssl-fu required to break it into .key and .pem files.

A few other formats that show up from time to time:

     .der A way to encode ASN.1 syntax, a .pem file is just a Base64 encoded .der file. OpenSSL can convert these to .pem. Windows sees these as Certificate files. I've only ever run into them in the wild with Novell's eDirectory certificate authority.
     .cert .cer A .pem formatted file with a different extension, one that is recognized by Windows Explorer as a certificate, which .pem is not.
     .crl A certificate revocation list. Certificate Authorities produce these as a way to de-authorize certificates before expiration.

In summary, there are three different ways to present certificates and their components:

     PEM Governed by RFCs, it's used preferentially by open-source software. It can have a variety of extensions (.pem, .key, .cer, .cert, more)

     PKCS12 A private standard that provides enhanced security versus the plain-text PEM format. It's used preferentially by Windows systems, and can be freely converted to PEM format through use of openssl.

     DER The parent format of PEM. It's useful to think of it as a binary version of the base64-encoded PEM file. Not routinely used by anything in common usage.
