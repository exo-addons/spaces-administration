package org.exoplatform.addons.spacesadministration.portlet;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.plugin.ajax.Ajax;
import juzu.template.Template;
import org.exoplatform.addons.spacesadministration.SpacesAdministrationService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.security.MembershipEntry;

import javax.inject.Inject;
import java.util.*;

public class SpacesAdministrationController {
  private static Log log = ExoLogger.getLogger(SpacesAdministrationController.class);

  @Inject
  @Path("index.gtmpl")
  Template indexTmpl;

  private SpacesAdministrationService spacesAdministrationService;
  private OrganizationService organizationService;

  public SpacesAdministrationController() {
    spacesAdministrationService = (SpacesAdministrationService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpacesAdministrationService.class);
    organizationService = (OrganizationService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(OrganizationService.class);
  }

  @View
  public Response.Render index() {
    Map<String, Object> parameters = new HashMap<String, Object>();

    try {
      // all membership types
      List<MembershipType> membershipTypes = new ArrayList<MembershipType>(organizationService.getMembershipTypeHandler().findMembershipTypes());
      Collections.sort(membershipTypes, new Comparator<MembershipType>() {
        @Override
        public int compare(MembershipType membershipType1, MembershipType membershipType2) {
          return membershipType1.getName().compareTo(membershipType2.getName());
        }
      });
      parameters.put("membershipTypes", membershipTypes);

      // all groups
      List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
      Collections.sort(groups, new Comparator<Group>() {
        @Override
        public int compare(Group group1, Group group2) {
          return group1.getId().compareTo(group2.getId());
        }
      });
      parameters.put("groups", groups);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return indexTmpl.ok(parameters);
  }

  @Ajax
  @Resource
  public Response getMemberships() {
    List<MembershipEntry> createMemberships = spacesAdministrationService.getSpaceCreationMemberships();

    StringBuilder jsonMemberships = new StringBuilder("[");
    if(createMemberships != null && !createMemberships.isEmpty()) {
      for(MembershipEntry membershipEntry : createMemberships) {
        jsonMemberships.append("\"").append(membershipEntry.getMembershipType()).append(":").append(membershipEntry.getGroup()).append("\",");
      }
      jsonMemberships.deleteCharAt(jsonMemberships.length()-1);
    }
    jsonMemberships.append("]");

    return Response.ok(jsonMemberships.toString());
  }

  @Ajax
  @Resource
  public Response resetToDefaults() {
    try {
      spacesAdministrationService.deleteSettingsEntity();
    } catch (Exception e) {
      log.error("Error while reseting the memberships to default values for spaces administration - Cause : " + e.getMessage(), e);
      return Response.error("Error while reseting memberships to default values");
    }

    return getMemberships();
  }

  @Ajax
  @Resource
  public Response add(String membership) {
    if(membership != null) {
      String[] membershipArray = membership.split(":");
      MembershipEntry membershipEntry = new MembershipEntry(membershipArray[1], membershipArray[0]);
      try {
        spacesAdministrationService.addSpaceCreationMembership(membershipEntry);
      } catch (Exception e) {
        log.error("Error while adding the membership " + membership + " for spaces administration - Cause : " + e.getMessage(), e);
        return Response.error("Error while adding the membership " + membership + " for spaces administration");
      }
    }

    return Response.ok("");
  }

  @Ajax
  @Resource
  public Response delete(String membership) {
    if(membership != null) {
      String[] membershipArray = membership.split(":");
      MembershipEntry membershipEntry = new MembershipEntry(membershipArray[1], membershipArray[0]);
      try {
        spacesAdministrationService.deleteSpaceCreationMembership(membershipEntry);
      } catch (Exception e) {
        log.error("Error while deleting the membership " + membership + " for spaces administration - Cause : " + e.getMessage(), e);
        return Response.error("Error while deleting the membership " + membership + " for spaces administration");
      }
    }

    return Response.ok("");
  }

  /*
  @Ajax
  @Resource
  public Response.Content save(String[] memberships) {
    List<MembershipEntry> membershipEntries = new ArrayList<MembershipEntry>();
    if(memberships != null) {
      for(String membership: memberships) {
        String[] membershipArray = membership.split(":");
        membershipEntries.add(new MembershipEntry(membershipArray[1], membershipArray[0]));
      }
    }
    try {
      spacesAdministration.updateCreateMembership(membershipEntries);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }

    return Response.ok("");
  }
  */
}
