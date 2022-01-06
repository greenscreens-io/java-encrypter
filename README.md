JAVA Url Encrypter
===================

![GitHub release (latest by date)](https://img.shields.io/github/v/release/greenscreens-io/java-encrypter?style=plastic)
![GitHub](https://img.shields.io/github/license/greenscreens-io/java-encrypter?style=plastic)

![Compile](https://github.com/greenscreens-io/java-encrypter/actions/workflows/maven.yml/badge.svg?branch=master) 

Green Screens Web 5250 Terminal URL address uses custom encryption based on RSA algorithm generated at Green Screens Terminal Service to protect URL parameters like auto sign-on data.

Java URL Encrypter is an integration library to create encrypted web terminal access URL.

**NOTE:** 
When plain mode used, make sure to set client IP address,

If URL encryption protection is configured through administration console, browser fingerprint must be used to enable access to web terminal.

Include fingerprint lib (http://localhost:9080/lite/lib/client.min.js) inside web page and fingerprint.js then call getFingerprint().

Example:

To request access URL from Java Servlet (fingerprint is optional and depends on URl sharing protection)
   
    http://localhost:9080/ServletExample?fp=12342343   
 
----------
&copy; Green Screens Ltd. 2015. - 2021.