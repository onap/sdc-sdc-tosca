/*-
 * ============LICENSE_START=======================================================
 * sdc-tosca
 * ================================================================================
 * Copyright (C) 2017 - 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.tosca.parser.utils;

import java.util.regex.Pattern;

public class SdcToscaUtility {
	
	public final static Pattern COMPONENT_INSTANCE_NAME_DELIMETER_PATTERN = Pattern.compile("[\\.\\-]+");
	
	public static String normaliseComponentInstanceName(String name) {
		String normalizedName = name.toLowerCase();
		normalizedName = COMPONENT_INSTANCE_NAME_DELIMETER_PATTERN.matcher(normalizedName).replaceAll(" ");
		String[] split = normalizedName.split(" ");
		StringBuffer sb = new StringBuffer();
		for (String splitElement : split) {
			sb.append(splitElement);
		}
		return sb.toString();
	}
}
