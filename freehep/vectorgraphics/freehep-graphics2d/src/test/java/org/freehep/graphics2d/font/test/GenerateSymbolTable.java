/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.freehep.graphics2d.font.test;

import org.freehep.graphics2d.font.CharTable;
import org.freehep.graphics2d.font.Symbol;
import org.freehep.graphics2d.font.Zapfdingbats;

/**
 *
 * @author Mark Donszelmann (Mark.Donszelmann@gmail.com)
 * @version $Id$
 */
public class GenerateSymbolTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CharTable table = new Zapfdingbats();
		
		for (int i=0; i<256; i++) {
			String name = table.toName(i);
			System.err.println((name == null ?  "notdef" : "\""+name+"\"")+", ");
		}
	}

}
