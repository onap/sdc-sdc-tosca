/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.sdc.toscaparser.api.elements.constraints;

import org.onap.sdc.toscaparser.api.common.JToscaValidationIssue;
import org.onap.sdc.toscaparser.api.functions.Function;
import org.onap.sdc.toscaparser.api.utils.ThreadLocalsHolder;

import java.util.Arrays;
import java.util.Date;

public class GreaterOrEqual extends Constraint {
    // Constraint class for "greater_or_equal"

    // Constrains a property or parameter to a value greater than or equal
    // to ('>=') the value declared.

    protected void setValues() {

        setConstraintKey(GREATER_OR_EQUAL);

        // timestamps are loaded as Date objects
        addValidTypes(Arrays.asList("Integer", "Double", "Float", "Date"));
        //validTypes.add("datetime.date");
        //validTypes.add("datetime.time");
        //validTypes.add("datetime.datetime");

        validPropTypes.add(Schema.INTEGER);
        validPropTypes.add(Schema.FLOAT);
        validPropTypes.add(Schema.TIMESTAMP);
        validPropTypes.add(Schema.SCALAR_UNIT_SIZE);
        validPropTypes.add(Schema.SCALAR_UNIT_FREQUENCY);
        validPropTypes.add(Schema.SCALAR_UNIT_TIME);

    }

    public GreaterOrEqual(String name, String type, Object c) {
        super(name, type, c);

        if (!validTypes.contains(constraintValue.getClass().getSimpleName())) {
            ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE104", "InvalidSchemaError: The property \"greater_or_equal\" expects comparable values"));
        }
    }


    @Override
    protected boolean isValid(Object value) {
        if (Function.isFunction(value)) {
            return true;
        }

        // timestamps
        if (value instanceof Date) {
            if (constraintValue instanceof Date) {
                return !((Date) value).before((Date) constraintValue);
            }
            return false;
        }
        // all others
        Double n1 = new Double(value.toString());
        Double n2 = new Double(constraintValue.toString());
        return n1 >= n2;
    }

    protected String errMsg(Object value) {
        return String.format("The value \"%s\" of property \"%s\" must be greater or equal to \"%s\"",
                valueMsg, propertyName, constraintValueMsg);
    }
}

/*python

class GreaterOrEqual(Constraint):
"""Constraint class for "greater_or_equal"

Constrains a property or parameter to a value greater than or equal
to ('>=') the value declared.
"""

constraint_key = Constraint.GREATER_OR_EQUAL

valid_types = (int, float, datetime.date,
               datetime.time, datetime.datetime)

valid_prop_types = (Schema.INTEGER, Schema.FLOAT, Schema.TIMESTAMP,
                    Schema.SCALAR_UNIT_SIZE, Schema.SCALAR_UNIT_FREQUENCY,
                    Schema.SCALAR_UNIT_TIME)

def __init__(self, property_name, property_type, constraint):
    super(GreaterOrEqual, self).__init__(property_name, property_type,
                                         constraint)
    if not isinstance(self.constraint_value, self.valid_types):
        ThreadLocalsHolder.getCollector().appendException(
            InvalidSchemaError(message=_('The property '
                                         '"greater_or_equal" expects '
                                         'comparable values.')))

def _is_valid(self, value):
    if toscaparser.functions.is_function(value) or \
       value >= self.constraint_value:
        return True
    return False

def _err_msg(self, value):
    return (_('The value "%(pvalue)s" of property "%(pname)s" must be '
              'greater than or equal to "%(cvalue)s".') %
            dict(pname=self.property_name,
                 pvalue=self.value_msg,
                 cvalue=self.constraint_value_msg))


*/
