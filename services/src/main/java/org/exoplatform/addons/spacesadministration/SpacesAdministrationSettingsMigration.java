package org.exoplatform.addons.spacesadministration;

import org.exoplatform.commons.cluster.StartableClusterAware;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.MembershipEntry;

import java.util.List;

/**
 * Migration service to migrate Spaces Administration data from JCR to Settings API
 */
public class SpacesAdministrationSettingsMigration implements StartableClusterAware {
    private Log log = ExoLogger.getLogger(SpacesAdministrationSettingsMigration.class);

    private JCRSpacesAdministrationStorage jcrSpacesAdministrationStorage;

    private SettingsSpacesAdministrationStorage settingsSpacesAdministrationStorage;

    public SpacesAdministrationSettingsMigration(JCRSpacesAdministrationStorage jcrSpacesAdministrationStorage,
                                                 SettingsSpacesAdministrationStorage settingsSpacesAdministrationStorage) {
        this.jcrSpacesAdministrationStorage = jcrSpacesAdministrationStorage;
        this.settingsSpacesAdministrationStorage = settingsSpacesAdministrationStorage;
    }

    @Override
    public void start() {
        try {
            log.info("== Starting migrating Spaces Administration data");
            List<MembershipEntry> spaceCreationMemberships = jcrSpacesAdministrationStorage.getSpaceCreationMemberships();
            if(spaceCreationMemberships != null) {
                for(MembershipEntry membershipEntry : spaceCreationMemberships) {
                    log.info("   * migrating membership " + membershipEntry.toString());
                    settingsSpacesAdministrationStorage.addSpaceCreationMembership(membershipEntry);
                }
            } else {
                log.info("   * no membership to migrate");
            }

            // clean JCR data
            log.info("== Cleaning old Spaces Administration data");
            jcrSpacesAdministrationStorage.deleteSettingsEntity();

            log.info("== Spaces Administration data migration done successfully");
        } catch (Exception e) {
            log.error("Error while migrating Spaces Administration data", e);
        }
    }

    @Override
    public boolean isDone() {
        return !jcrSpacesAdministrationStorage.settingsEntityExists();
    }
}
