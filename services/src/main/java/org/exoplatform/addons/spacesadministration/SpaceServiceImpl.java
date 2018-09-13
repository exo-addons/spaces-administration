package org.exoplatform.addons.spacesadministration;

import org.exoplatform.commons.api.notification.service.WebNotificationService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.IdentityStorage;
import org.exoplatform.social.core.storage.api.SpaceStorage;

/**
 * @author Thomas Delhoménie
 */
public class SpaceServiceImpl extends org.exoplatform.social.core.space.impl.SpaceServiceImpl {

  private Log log = ExoLogger.getLogger(SpaceServiceImpl.class);

  private SpacesAdministrationService spacesAdministrationService;

  public SpaceServiceImpl(InitParams params, SpaceStorage spaceStorage, IdentityStorage identityStorage, UserACL userACL, IdentityRegistry identityRegistry, WebNotificationService webNotificationService) throws Exception {
    super(params, spaceStorage, identityStorage, userACL, identityRegistry, webNotificationService);

    spacesAdministrationService = ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpacesAdministrationService.class);
  }

  @Override
  public Space createSpace(Space space, String creator, String invitedGroupId) {
    if(!spacesAdministrationService.canCreateSpace()) {
      // need to throw a RuntimeException since the default method does not throw any exception
      log.warn("User does not have permissions to create a space.");
      throw new RuntimeException("User does not have permissions to create a space.");
    }

    return super.createSpace(space, creator, invitedGroupId);
  }
}
