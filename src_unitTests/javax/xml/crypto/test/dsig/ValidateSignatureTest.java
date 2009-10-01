/*
 * Copyright 2006-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.security.Security;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import junit.framework.*;

import javax.xml.crypto.test.KeySelectors;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

/**
 * This is a testcase that validates various signatures
 *
 * @author Sean Mullan
 */
public class ValidateSignatureTest extends TestCase {

    private SignatureValidator validator;
    private File dir;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public ValidateSignatureTest(String name) {
        super(name);
	String fs = System.getProperty("file.separator");
	String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	
	dir = new File(base + fs + "data" + fs 
	    + "javax" + fs + "xml" + fs + "crypto", "dsig");
	validator = new SignatureValidator(dir);
    }

    /** 
     * Validates a signature that references an element with an ID attribute. 
     * The element's ID needs to be registered so that it can be found.
     */
    public void test_signature_with_ID() throws Exception {
        String file = "envelopingSignature.xml";

	DOMValidateContext vc = validator.getValidateContext
	    (file, new KeySelectors.KeyValueKeySelector());
	Document doc = vc.getNode().getOwnerDocument();
	NodeList nl = doc.getElementsByTagName("Assertion");
	vc.setIdAttributeNS((Element) nl.item(0), null, "AssertionID");
	boolean coreValidity = validator.validate(vc);
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature_external_c14n_xmlattrs() throws Exception {
	String file = "signature-external-c14n-xmlatrs.xml";

	boolean coreValidity = validator.validate(file, 
	    new KeySelectors.SecretKeySelector("secret".getBytes("ASCII")));
	assertTrue("Signature failed core validation", coreValidity);
    }

    /**
     * This test checks that the signature is verified before the references.
     */
    public void test_invalid_signature() throws Exception {
        InvalidURIDereferencer ud = new InvalidURIDereferencer();

        boolean coreValidity = validator.validate("invalid-signature.xml", 
	    new KeySelectors.KeyValueKeySelector(), ud);
	assertFalse("Invalid signature should fail!", coreValidity);
	assertTrue("References validated before signature", ud.dereferenced);
    }

    public void test_signature_enveloping_hmac_sha1_trunclen_0() throws Exception {
        try {
            boolean coreValidity = validator.validate
                ("signature-enveloping-hmac-sha1-trunclen-0-attack.xml", 
                new KeySelectors.SecretKeySelector("secret".getBytes("ASCII")));
            fail("Expected HMACOutputLength exception");
        } catch (XMLSignatureException xse) {
            System.out.println(xse.getMessage());
            // pass
        }
    }

    public void test_signature_enveloping_hmac_sha1_trunclen_8() throws Exception {
   
        try {
            boolean coreValidity = validator.validate
                ("signature-enveloping-hmac-sha1-trunclen-8-attack.xml", 
                new KeySelectors.SecretKeySelector("secret".getBytes("ASCII")));
            fail("Expected HMACOutputLength exception");
        } catch (XMLSignatureException xse) {
            System.out.println(xse.getMessage());
            // pass
        }
    }

    // Bug 47761: validates an xml signature containing a reference with
    // xmlns:xml attributes. C14n should not emit these attributes.
    public void test_signature_exclc14n_xmlnamespace() throws Exception {
	String file = "demo.signed.xml";
	boolean coreValidity = validator.validate(file, 
	    new KeySelectors.RawX509KeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }

    /**
     * Set flag if called.
     */
    static class InvalidURIDereferencer implements URIDereferencer {
        boolean dereferenced = false;
        private URIDereferencer ud =
            XMLSignatureFactory.getInstance().getURIDereferencer();

        public Data dereference(final URIReference ref, XMLCryptoContext ctx)
        throws URIReferenceException {
            dereferenced = true;

            // fallback
            return ud.dereference(ref, ctx);
        }
    }
    
    public static void main(String[] args) throws Exception {
        ValidateSignatureTest vst = new ValidateSignatureTest("");
        vst.test_signature_with_ID();
        vst.test_signature_external_c14n_xmlattrs();
    }
}
