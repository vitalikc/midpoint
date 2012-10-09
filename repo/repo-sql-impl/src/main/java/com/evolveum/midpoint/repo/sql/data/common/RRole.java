/*
 * Copyright (c) 2012 Evolveum
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
 *
 * Portions Copyrighted 2012 [name of copyright owner]
 */

package com.evolveum.midpoint.repo.sql.data.common;

import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.repo.sql.util.ContainerIdGenerator;
import com.evolveum.midpoint.repo.sql.util.DtoTranslationException;
import com.evolveum.midpoint.repo.sql.query.QueryAttribute;
import com.evolveum.midpoint.xml.ns._public.common.common_2.AssignmentType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ExclusionType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.RoleType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
@ForeignKey(name = "fk_role")
public class RRole extends RObject {

	@QueryAttribute(polyString = true)
	private RPolyString name;
	private Set<RAssignment> assignments;
	private Set<RExclusion> exclusions;
	private Set<RObjectReference> approverRefs;

	@OneToMany(mappedBy = "owner", orphanRemoval = true)
	@ForeignKey(name = "none")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	public Set<RAssignment> getAssignments() {
		if (assignments == null) {
			assignments = new HashSet<RAssignment>();
		}
		return assignments;
	}

	@OneToMany(mappedBy = "owner", orphanRemoval = true)
	@ForeignKey(name = "none")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	public Set<RExclusion> getExclusions() {
		if (exclusions == null) {
			exclusions = new HashSet<RExclusion>();
		}
		return exclusions;
	}

	@Index(name = "iRoleName")
	@Column(name = "objectName", unique = true)
	public RPolyString getName() {
		return name;
	}

	@OneToMany(mappedBy = "owner", orphanRemoval = true)
	@ForeignKey(name = "none")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	public Set<RObjectReference> getApproverRefs() {
		if (approverRefs == null){
			approverRefs = new HashSet<RObjectReference>();
		}
		return approverRefs;
	}

	public void setApproverRefs(Set<RObjectReference> approverRefs) {
		this.approverRefs = approverRefs;
	}

	public void setName(RPolyString name) {
		this.name = name;
	}

	public void setExclusions(Set<RExclusion> exclusions) {
		this.exclusions = exclusions;
	}

	public void setAssignments(Set<RAssignment> assignments) {
		this.assignments = assignments;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		RRole rRole = (RRole) o;

		if (name != null ? !name.equals(rRole.name) : rRole.name != null)
			return false;
		if (assignments != null ? !assignments.equals(rRole.assignments) : rRole.assignments != null)
			return false;
		if (exclusions != null ? !exclusions.equals(rRole.exclusions) : rRole.exclusions != null)
			return false;
		if (approverRefs != null ? !approverRefs.equals(rRole.approverRefs) : rRole.approverRefs != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	public static void copyToJAXB(RRole repo, RoleType jaxb, PrismContext prismContext) throws DtoTranslationException {
		RObject.copyToJAXB(repo, jaxb, prismContext);

		jaxb.setName(RPolyString.copyToJAXB(repo.getName()));
		if (repo.getAssignments() != null) {
			for (RAssignment rAssignment : repo.getAssignments()) {
				jaxb.getAssignment().add(rAssignment.toJAXB(prismContext));
			}
		}
		if (repo.getExclusions() != null) {
			for (RExclusion rExclusion : repo.getExclusions()) {
				jaxb.getExclusion().add(rExclusion.toJAXB(prismContext));
			}
		}

		if (repo.getApproverRefs() != null) {
			for (RObjectReference repoRef : repo.getApproverRefs()) {
				jaxb.getApproverRef().add(repoRef.toJAXB(prismContext));
			}
		}
	}

	public static void copyFromJAXB(RoleType jaxb, RRole repo, PrismContext prismContext)
			throws DtoTranslationException {
		RObject.copyFromJAXB(jaxb, repo, prismContext);

		repo.setName(RPolyString.copyFromJAXB(jaxb.getName()));
		if (jaxb.getAssignment() != null && !jaxb.getAssignment().isEmpty()) {
			repo.setAssignments(new HashSet<RAssignment>());
		}

		ContainerIdGenerator gen = new ContainerIdGenerator();
		for (AssignmentType assignment : jaxb.getAssignment()) {
			RAssignment rAssignment = new RAssignment();
			rAssignment.setOwner(repo);

			RAssignment.copyFromJAXB(assignment, rAssignment, jaxb, prismContext);
			gen.generate(null, rAssignment);

			repo.getAssignments().add(rAssignment);
		}

		if (jaxb.getExclusion() != null && !jaxb.getExclusion().isEmpty()) {
			repo.setExclusions(new HashSet<RExclusion>());
		}
		for (ExclusionType exclusion : jaxb.getExclusion()) {
			RExclusion rExclusion = new RExclusion();
			rExclusion.setOwner(repo);

			RExclusion.copyFromJAXB(exclusion, rExclusion, jaxb, prismContext);
			gen.generate(null, rExclusion);

			repo.getExclusions().add(rExclusion);
		}

		for (ObjectReferenceType accountRef : jaxb.getApproverRef()) {
			RObjectReference ref = RUtil.jaxbRefToRepo(accountRef, repo, prismContext);
			if (ref != null) {
				repo.getApproverRefs().add(ref);
			}
		}
	}

	@Override
	public RoleType toJAXB(PrismContext prismContext) throws DtoTranslationException {
		RoleType object = new RoleType();
		RRole.copyToJAXB(this, object, prismContext);
		RUtil.revive(object, prismContext);
		return object;
	}
}
