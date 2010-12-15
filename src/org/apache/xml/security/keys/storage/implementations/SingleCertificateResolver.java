
/*
 * Copyright 1999-2010 The Apache Software Foundation.
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
package org.apache.xml.security.keys.storage.implementations;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.xml.security.keys.storage.StorageResolverSpi;

/**
 * This {@link StorageResolverSpi} makes a single {@link X509Certificate}
 * available to the {@link org.apache.xml.security.keys.storage.StorageResolver}.
 *
 * @author $Author$
 */
public class SingleCertificateResolver extends StorageResolverSpi {

   /** Field _certificate */
   X509Certificate _certificate = null;

   /**
    * @param x509cert the single {@link X509Certificate}
    */
   public SingleCertificateResolver(X509Certificate x509cert) {
      this._certificate = x509cert;
   }

   /** @inheritDoc */
   public Iterator getIterator() {
      return new InternalIterator(this._certificate);
   }

   /**
    * Class InternalIterator
    *
    * @author $Author$
    * @version $Revision$
    */
   static class InternalIterator implements Iterator {

      /** Field _alreadyReturned */
      boolean _alreadyReturned = false;

      /** Field _certificate */
      X509Certificate _certificate = null;

      /**
       * Constructor InternalIterator
       *
       * @param x509cert
       */
      public InternalIterator(X509Certificate x509cert) {
         this._certificate = x509cert;
      }

      /** @inheritDoc */
      public boolean hasNext() {
         return (!this._alreadyReturned);
      }

      /** @inheritDoc */
      public Object next() {
         if (this._alreadyReturned) {
             throw new NoSuchElementException();
         }
         this._alreadyReturned = true;
         return this._certificate;
      }

      /**
       * Method remove
       *
       */
      public void remove() {
         throw new UnsupportedOperationException(
            "Can't remove keys from KeyStore");
      }
   }
}
