JAVA Url Encrypter
===================


GreenScreens Web 5250 Terminal URL address uses custom encryption based on RSA algorithm generated at GreenScreens Terminal Service to protect URL parameters like auto sign-on data.  

Java URL Encrypter is an integration supporting library to create encrypted web terminal access URL.

**NOTE:** 
If URL encryption protection is configured through administration console, browser fingerprint must be used to enable access to web terminal.

Include fingerprint lib (http://localhost:9080/lite/lib/client.min.js) inside web page and fingerprint.js then call getFingerprint().

Example:

To request access URL from Java Servlet (fingerprint is optional and depends on URl sharing protection)
   
    http://localhost:9080/ServletExample?fp=12342343   
 
----------
&copy; Green Screens Ltd. 2015., 2016.