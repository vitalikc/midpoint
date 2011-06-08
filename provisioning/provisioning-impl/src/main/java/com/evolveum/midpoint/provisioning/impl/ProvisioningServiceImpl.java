/*
 * Copyright (c) 2011 Evolveum
 * 
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 * 
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * Portions Copyrighted 2011 [name of copyright owner]
 */
package com.evolveum.midpoint.provisioning.impl;

import org.apache.commons.lang.NotImplementedException;

import com.evolveum.midpoint.common.result.OperationResult;
import com.evolveum.midpoint.provisioning.api.ProvisioningService;
import com.evolveum.midpoint.schema.exception.CommunicationException;
import com.evolveum.midpoint.schema.exception.ObjectAlreadyExistsException;
import com.evolveum.midpoint.schema.exception.ObjectNotFoundException;
import com.evolveum.midpoint.schema.exception.SchemaException;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ObjectContainerType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.PropertyReferenceListType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ScriptsType;
import com.evolveum.midpoint.xml.ns._public.repository.repository_1.RepositoryPortType;
import com.evolveum.midpoint.common.result.OperationResult;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ResourceObjectShadowType;

/**
 * WORK IN PROGRESS
 * 
 * There be dragons.
 * Beware the dog.
 * Do not trespass.
 * @author Radovan Semancik
 */
public class ProvisioningServiceImpl implements ProvisioningService {
	
	private ShadowCache shadowCache;
	private RepositoryWrapper repositoryService;

	public ShadowCache getShadowCache() {
		return shadowCache;
	}
	
	public void setShadowCache(ShadowCache shadowCache) {
		this.shadowCache = shadowCache;
	}
	
	/**
     * Get the value of repositoryService.
     *
     * @return the value of repositoryService
     */
    public RepositoryWrapper getRepositoryService() {
        return repositoryService;
    }

    /**
     * Set the value of repositoryService
     *
     * Expected to be injected.
     * 
     * @param repositoryService new value of repositoryService
     */
    public void setRepositoryService(RepositoryWrapper repositoryService) {
        this.repositoryService = repositoryService;
    }
	
	
	@Override
	public ObjectType getObject(String oid, PropertyReferenceListType resolve, OperationResult parentResult) throws ObjectNotFoundException, CommunicationException, SchemaException {
		
		// Result type for this operation
		OperationResult result = parentResult
				.createSubresult(ProvisioningService.class.getName()
						+ ".getObject");
		result.addParam("oid", oid);
		result.addParam("resolve", resolve);
		result.addContext(OperationResult.CONTEXT_IMPLEMENTATION_CLASS, ProvisioningServiceImpl.class);
		
		ObjectType object = null;

		try {
			object = getRepositoryService().getObject(oid, resolve, result);
		} catch (ObjectNotFoundException e) {
			result.record(e);
			throw e;
		}
		
		if (object instanceof ResourceObjectShadowType) {
			//ResourceObjectShadowType shadow = (ResourceObjectShadowType)object;
			// TODO: optimization needed: avoid multiple "gets" of the same object
			
			ResourceObjectShadowType shadow = null;			
			try {
				shadow = getShadowCache().getObject(oid, null, result);
			} catch (ObjectNotFoundException e) {
				result.record(e);
				throw e;
			} catch (CommunicationException e) {
				result.record(e);
				throw e;
			} catch (SchemaException e) {
				result.record(e);
				throw e;
			}
				
			// TODO: object resolving
			return shadow;
		} else {
			return object;
		}

	}

	@Override
	public String addObject(ObjectType object, ScriptsType scripts, OperationResult parentResult)
			throws ObjectAlreadyExistsException, SchemaException, CommunicationException {
		// TODO
		throw new NotImplementedException();
	}
	
}
