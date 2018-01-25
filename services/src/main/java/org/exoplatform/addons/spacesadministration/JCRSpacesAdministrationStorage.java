package org.exoplatform.addons.spacesadministration;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.MembershipEntry;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;

/**
 * Storage implementation which stores Spaces Administration data in JCR
 */
public class JCRSpacesAdministrationStorage {

  private Log log = ExoLogger.getLogger(JCRSpacesAdministrationStorage.class);

  public final static String SETTINGS_NODE_WORKSPACE = "social";
  public final static String SETTINGS_NODE_PATH = "exo:applications/spacesadministration";
  public final static String SETTINGS_NODE_CREATE_MEMBERSHIP_PROPERTY = "soc:createMemberships";


  /**
   * Check if the Settings node exists
   * @return The node containing the memberships
   */
  public boolean settingsEntityExists() {
    Session session = null;
    try {
      session = getSession();
      return session.getRootNode().hasNode(SETTINGS_NODE_PATH);
    } catch (RepositoryException e) {
      log.error("Error while checking spaces administration settings node existence - Cause : " + e.getMessage(), e);
      return false;
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * Create the Settings JCR node
   * @return The newly created Settings node
   * @throws RepositoryException
   */
  protected void createSettingsEntity() throws Exception {
    Session session = null;
    try {
      session = getSession();
      Node settingsNode = session.getRootNode().addNode(SETTINGS_NODE_PATH);
      settingsNode.setProperty(SETTINGS_NODE_CREATE_MEMBERSHIP_PROPERTY, "");

      session.save();
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * Add a new membership
   * @param membership Membership to add
   * @throws RepositoryException
   */
  public void addSpaceCreationMembership(MembershipEntry membership) throws Exception {
    if(membership == null) {
      throw new IllegalArgumentException("Cannot add null membership");
    }

    String membershipToAdd = membership.getMembershipType() + ":" + membership.getGroup();

    Session session = null;
    try {
      session = getSession();
      Node settingsNode;
      if(session.getRootNode().hasNode(SETTINGS_NODE_PATH)) {
        settingsNode = session.getRootNode().getNode(SETTINGS_NODE_PATH);
      } else {
        throw new Exception("Spaces Administration Settings Node does not exist - Cannot add the membership " + membershipToAdd);
      }

      String newMembership = membership.getMembershipType() + ":" + membership.getGroup();

      String currentCreateSpaceMemberships = settingsNode.getProperty(SETTINGS_NODE_CREATE_MEMBERSHIP_PROPERTY).getString();
      String newCreateSpaceMemberships = currentCreateSpaceMemberships;
      if(!currentCreateSpaceMemberships.isEmpty()) {
        // check if the membership already exists
        String[] currentCreateSpaceMembershipsArray = currentCreateSpaceMemberships.split(",");
        for(String currentCreateSpaceMembership : currentCreateSpaceMembershipsArray) {
          if(currentCreateSpaceMembership.equals(newMembership)) {
            // no need to add it, it is already there
            return;
          }
        }

        newCreateSpaceMemberships += ",";
      }
      newCreateSpaceMemberships += newMembership;
      settingsNode.setProperty(SETTINGS_NODE_CREATE_MEMBERSHIP_PROPERTY, newCreateSpaceMemberships);
      settingsNode.save();
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * Get JCR node storing the memberships
   * @return The node containing the memberships
   */
  public List<MembershipEntry> getSpaceCreationMemberships() throws Exception {
    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();

    Session session = null;
    try {
      session = getSession();
      if(session.getRootNode().hasNode(SETTINGS_NODE_PATH)) {
        Node settingsNode = session.getRootNode().getNode(SETTINGS_NODE_PATH);
        String createSpaceMemberships = settingsNode.getProperty(SETTINGS_NODE_CREATE_MEMBERSHIP_PROPERTY).getString();
        if(createSpaceMemberships != null && !createSpaceMemberships.trim().isEmpty()) {
          String[] createSpaceMembershipsSplitted = createSpaceMemberships.split(",");

          for(String createMembership : createSpaceMembershipsSplitted) {
            String[] createSpaceMembershipSplitted = createMembership.split(":");
            if(createSpaceMembershipSplitted.length == 2) {
              memberships.add(new MembershipEntry(createSpaceMembershipSplitted[1], createSpaceMembershipSplitted[0]));
            } else {
              log.warn("Wrong format for spaces administration membership : " + createMembership);
            }
          }
        }
        return memberships;
      } else {
        return null;
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * Delete a membership
   * @param membership Membership to delete
   * @throws RepositoryException
   */
  public void deleteSpaceCreationMembership(MembershipEntry membership) throws Exception {
    if(membership == null) {
      throw new IllegalArgumentException("Cannot delete null membership");
    }

    String membershipToDelete = membership.getMembershipType() + ":" + membership.getGroup();

    Session session = null;
    try {
      session = getSession();
      Node settingsNode;
      if(session.getRootNode().hasNode(SETTINGS_NODE_PATH)) {
        settingsNode = session.getRootNode().getNode(SETTINGS_NODE_PATH);
      } else {
        throw new Exception("Spaces Administration Settings Node does not exist - Cannot delete the membership " + membershipToDelete);
      }

      String currentCreateSpaceMemberships = settingsNode.getProperty(SETTINGS_NODE_CREATE_MEMBERSHIP_PROPERTY).getString();
      String[] currentCreateSpaceMembershipsArray = currentCreateSpaceMemberships.split(",");
      StringBuilder newCreateSpaceMemberships = new StringBuilder();
      for(String currentCreateSpaceMembership : currentCreateSpaceMembershipsArray) {
        if(!currentCreateSpaceMembership.equals(membershipToDelete)) {
          newCreateSpaceMemberships.append(currentCreateSpaceMembership + ",");
        }
      }
      if(newCreateSpaceMemberships.length() > 0) {
        newCreateSpaceMemberships.deleteCharAt(newCreateSpaceMemberships.length() - 1);
      }

      settingsNode.setProperty(SETTINGS_NODE_CREATE_MEMBERSHIP_PROPERTY, newCreateSpaceMemberships.toString());
      settingsNode.save();
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * Delete the Settings JCR node
   * @throws RepositoryException
   */
  public void deleteSettingsEntity() throws RepositoryException {
    Session session = null;
    try {
      session = getSession();
      if(session.getRootNode().hasNode(SETTINGS_NODE_PATH)) {
        Node settingsNode = session.getRootNode().getNode(SETTINGS_NODE_PATH);

        settingsNode.remove();
        session.save();
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * Get a JCR Session
   * @return A JCR Session
   * @throws javax.jcr.RepositoryException
   */
  public Session getSession() throws RepositoryException {
    RepositoryService repositoryService = (RepositoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RepositoryService.class);
    SessionProviderService sessionProviderService = (SessionProviderService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SessionProviderService.class);

    ManageableRepository repository = repositoryService.getCurrentRepository();
    return sessionProviderService.getSystemSessionProvider(null).getSession(SETTINGS_NODE_WORKSPACE, repository);
  }
}
