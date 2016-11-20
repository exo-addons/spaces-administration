package org.exoplatform.addons.spacesadministration;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.security.MembershipEntry;

/**
 * @author Thomas Delhoménie
 */
public class SpacesAdministrationStorage {

  public final static String SETTINGS = "spacesadministrationsettings";

  private SettingService     settingService;

  public SpacesAdministrationStorage(SettingService settingService) {
    this.settingService = settingService;
  }

  /**
   * Check if the Settings node exists
   * 
   * @return The node containing the memberships
   */
  public boolean settingsEntityExists() throws Exception {
    SettingValue<?> settingValue = settingService.get(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS);
    return settingValue != null && settingValue.getValue() != null;
  }

  /**
   * Add a new membership
   * 
   * @param membership Membership to add
   * @throws RepositoryException
   */
  public void addSpaceCreationMembership(MembershipEntry membership) throws Exception {
    if (membership == null) {
      throw new IllegalArgumentException("Cannot add null membership");
    }

    String membershipToAdd = membership.getMembershipType() + ":" + membership.getGroup();

    SettingValue<?> settingValue = settingService.get(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS);

    if (settingValue == null) {
      settingValue = SettingValue.create(membershipToAdd);
    } else {
      String values = (String) settingValue.getValue();
      if (!values.contains(membershipToAdd)) {
        values += ";" + membershipToAdd;
      }
      settingValue = SettingValue.create(values);
    }
    settingService.set(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS, settingValue);
  }

  /**
   * Get JCR node storing the memberships
   * 
   * @return The node containing the memberships
   */
  public List<MembershipEntry> getSpaceCreationMemberships() throws Exception {
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();

    SettingValue<?> settingValue = settingService.get(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS);

    if (settingValue != null) {
      String values = (String) settingValue.getValue();
      String[] membershipIds = values.split(";");
      for (String membershipId : membershipIds) {
        String[] membershipParts = membershipId.split(":");
        memberships.add(new MembershipEntry(membershipParts[1], membershipParts[1]));
      }
    }
    return memberships;
  }

  /**
   * Delete a membership
   * 
   * @param membership Membership to delete
   * @throws RepositoryException
   */
  public void deleteSpaceCreationMembership(MembershipEntry membership) throws Exception {
    if (membership == null) {
      throw new IllegalArgumentException("Cannot delete null membership");
    }

    String membershipToDelete = membership.getMembershipType() + ":" + membership.getGroup();

    SettingValue<?> settingValue = settingService.get(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS);

    if (settingValue != null) {
      String values = (String) settingValue.getValue();
      if (values.contains(membershipToDelete)) {
        values = values.replace(membershipToDelete + ";", "");
        values = values.replace(";" + membershipToDelete, "");
        values = values.replace(membershipToDelete, "");

        if (values.trim().isEmpty()) {
          settingService.remove(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS);
        } else {
          settingValue = SettingValue.create(values.trim());
          settingService.set(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS, settingValue);
        }
      }
    }
  }

  /**
   * Delete the Settings JCR node
   * 
   * @throws RepositoryException
   */
  public void deleteSettingsEntity() throws RepositoryException {
    settingService.remove(Context.GLOBAL, Scope.GLOBAL.id(null), SETTINGS);
  }
}
