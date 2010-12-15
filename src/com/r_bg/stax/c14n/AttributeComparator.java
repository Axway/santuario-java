/*
 * Copyright 2007 The Apache Software Foundation.
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
package com.r_bg.stax.c14n;

import java.util.Comparator;

import javax.xml.stream.XMLStreamReader;

class AttributeCompartor implements Comparator {
	XMLStreamReader in;
	public AttributeCompartor(XMLStreamReader in) {
		this.in=in;
	}
	public int compare(Object arg0, Object arg1) {
		int first=((Integer)arg0).intValue();
		int second=((Integer)arg1).intValue();
		String uri1=in.getAttributeNamespace(first);
		String uri2=in.getAttributeNamespace(second);
		if (uri1==null) {			
			return (uri2!=null )? 1 : 
						in.getAttributeLocalName(first).compareTo(in.getAttributeLocalName(second));
		}
		if (uri2==null) {
			return -1;
		}			
		int result=uri1.compareTo(uri2);		
		return  (result!=0) ? result :in.getAttributeLocalName(first).compareTo(in.getAttributeLocalName(second));				
	}	
}