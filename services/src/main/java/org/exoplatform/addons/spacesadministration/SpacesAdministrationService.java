package org.exoplatform.addons.spacesadministration;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for Spaces Administration
 */
public class SpacesAdministrationService {

  private Log log = ExoLogger.getLogger(SpacesAdministrationService.class);

  public static final String SPACES_CREATE_MEMBERSHIPS_PROPERTY = "exo.spaces.create.memberships";

  private InitParams initParams;

  private SpacesAdministrationStorage spacesAdministrationStorage;

  public SpacesAdministrationService(InitParams initParams, SpacesAdministrationStorage spacesAdministrationStorage) {
    this.initParams = initParams;
    this.spacesAdministrationStorage = spacesAdministrationStorage;
  }

  /**
   * Check if the authenticated user can create spaces
   * @return true if the authenticated user can create spaces
   */
  public boolean canCreateSpace() {
    boolean canCreateSpace = false;

    try {
      List<MembershipEntry> memberships = getSpaceCreationMemberships();

      if(memberships == null || memberships.isEmpty()) {
        // no membership - anyone can create a space
        canCreateSpace = true;
      } else {
        Identity userIdentity = ConversationState.getCurrent().getIdentity();
        for(MembershipEntry membership : memberships) {
          if(userIdentity.isMemberOf(membership)) {
            canCreateSpace = true;
            break;
          }
        }
      }
    } catch(Exception e) {
      log.error("Error while checking if the user can create spaces - Cause : " + e.getMessage(), e);
      canCreateSpace = false;
    }

    return canCreateSpace;
  }

  /**
   * Add a new membership
   * @throws Exception
   */
  public void createSettingsEntityWithDefaultValues() throws Exception {
    if(spacesAdministrationStorage.settingsEntityExists()) {
      throw new Exception("Settings Entity already exists - Cannot create it");
    }

    spacesAdministrationStorage.createSettingsEntity();

    ValueParam createMembershipInitParam = initParams.getValueParam(SPACES_CREATE_MEMBERSHIPS_PROPERTY);
    if(createMembershipInitParam != null) {
      List<MembershipEntry> membershipEntries = convertMembershipsToMemebershipEntries(createMembershipInitParam.getValue());
      for(MembershipEntry membershipEntry : membershipEntries) {
        spacesAdministrationStorage.addSpaceCreationMembership(membershipEntry);
      }
    }
  }

  /**
     * Add a new membership
     * @param membership Membership to add
     * @throws Exception
     */
  public void addSpaceCreationMembership(MembershipEntry membership) throws Exception {
    if(membership == null) {
      throw new IllegalArgumentException("Cannot add null membership");
    }

    if(!spacesAdministrationStorage.settingsEntityExists()) {
      createSettingsEntityWithDefaultValues();
    }

    spacesAdministrationStorage.addSpaceCreationMembership(membership);
  }

  /**
   * Delete a membership
   * @param membership Membership to delete
   * @throws Exception
   */
  public void deleteSpaceCreationMembership(MembershipEntry membership) throws Exception {
    if(membership == null) {
      throw new IllegalArgumentException("Cannot delete null membership");
    }

    if(!spacesAdministrationStorage.settingsEntityExists()) {
      createSettingsEntityWithDefaultValues();
    }

    spacesAdministrationStorage.deleteSpaceCreationMembership(membership);
  }

  /**
   * Get the memberships allowed to create spaces
   * @return List of the memberships allowed to create spaces
   */
  public List<MembershipEntry> getSpaceCreationMemberships() throws Exception {
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();

    if(spacesAdministrationStorage.settingsEntityExists()) {
      // Use spaces administration settings persisted entity
      memberships = spacesAdministrationStorage.getSpaceCreationMemberships();
    } else {
      // Use configuration property
      ValueParam createMembershipInitParam = initParams.getValueParam(SPACES_CREATE_MEMBERSHIPS_PROPERTY);
      if(createMembershipInitParam != null) {
        memberships = convertMembershipsToMemebershipEntries(createMembershipInitParam.getValue());
      } else {
        log.warn("exo.spaces.create.memberships is not defined - nobody can create spaces");
      }
    }

    return memberships;
  }

  /**
   * Delete the Settings JCR node
   * @throws Exception
   */
  public void deleteSettingsEntity() throws Exception {
    spacesAdministrationStorage.deleteSettingsEntity();
  }

  protected List<MembershipEntry> convertMembershipsToMemebershipEntries(String memberships) {
    List<MembershipEntry> membershipEntries = new ArrayList<MembershipEntry>();

    String[] membershipsSplitted = memberships.split(",");

    for(String membership : membershipsSplitted) {
      String[] membershipSplitted = membership.split(":");
      if(membershipSplitted.length == 2) {
        membershipEntries.add(new MembershipEntry(membershipSplitted[1], membershipSplitted[0]));
      } else {
        log.warn("Wrong format for spaces administration membership : " + membership);
      }
    }

    return membershipEntries;
  }
}
