/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.web.component.wizard;

import com.evolveum.midpoint.xml.ns._public.common.common_3.MappingType;

/**
 *  @author shood
 * */
public class WizardUtil {

    public static MappingType createEmptyMapping(){
        MappingType mapping = new MappingType();
        mapping.setAuthoritative(true);

        return mapping;
    }

    public static boolean isEmptyMapping(MappingType toCompare){
        return createEmptyMapping().equals(toCompare);

    }
}
