package org.exoplatform.addons.spacesadministration;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.security.MembershipEntry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;

import java.util.Arrays;

/**
 * @author Thomas Delhoménie
 */
public class SpacesAdministrationServiceTest {

  @Test
  public void shouldUseConfigurationValueWhenSettingsEntityDoesNotExist() throws Exception {
    SpacesAdministrationStorage spacesAdministrationStorage = BDDMockito.mock(SpacesAdministrationStorage.class);

    BDDMockito.given(spacesAdministrationStorage.settingsEntityExists()).willReturn(false);

    InitParams initParams = new InitParams();
    ValueParam valueParam = new ValueParam();
    valueParam.setName(SpacesAdministrationService.SPACES_CREATE_MEMBERSHIPS_PROPERTY);
    valueParam.setValue("member:/platform/users");
    initParams.addParam(valueParam);

    SpacesAdministrationService spacesAdministrationService = new SpacesAdministrationService(initParams, spacesAdministrationStorage);
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().size() == 1);
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().get(0).equals(new MembershipEntry("/platform/users", "member")));
  }

  @Test
  public void shouldUseConfigurationValueWithTwomembershipsWhenSettingsEntityDoesNotExist() throws Exception {
    SpacesAdministrationStorage spacesAdministrationStorage = BDDMockito.mock(SpacesAdministrationStorage.class);

    BDDMockito.given(spacesAdministrationStorage.settingsEntityExists()).willReturn(false);

    InitParams initParams = new InitParams();
    ValueParam valueParam = new ValueParam();
    valueParam.setName(SpacesAdministrationService.SPACES_CREATE_MEMBERSHIPS_PROPERTY);
    valueParam.setValue("member:/platform/users,*:/platform/administrators");
    initParams.addParam(valueParam);

    SpacesAdministrationService spacesAdministrationService = new SpacesAdministrationService(initParams, spacesAdministrationStorage);
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().size() == 2);
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().contains(new MembershipEntry("/platform/users", "member")));
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().contains(new MembershipEntry("/platform/administrators", "*")));
  }

  @Test
  public void shouldUseSettingsEntityWhenSettingsEntityExists() throws Exception {
    SpacesAdministrationStorage spacesAdministrationStorage = BDDMockito.mock(SpacesAdministrationStorage.class);

    BDDMockito.given(spacesAdministrationStorage.settingsEntityExists()).willReturn(true);
    BDDMockito.given(spacesAdministrationStorage.getSpaceCreationMemberships()).willReturn(Arrays.asList(new MembershipEntry("/platform/users", "member"), new MembershipEntry("/platform/administrators", "*")));

    InitParams initParams = new InitParams();
    ValueParam valueParam = new ValueParam();
    valueParam.setName(SpacesAdministrationService.SPACES_CREATE_MEMBERSHIPS_PROPERTY);
    valueParam.setValue("editor:/platform/users");
    initParams.addParam(valueParam);

    SpacesAdministrationService spacesAdministrationService = new SpacesAdministrationService(initParams, spacesAdministrationStorage);
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().size() == 2);
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().contains(new MembershipEntry("/platform/users", "member")));
    Assert.assertTrue(spacesAdministrationService.getSpaceCreationMemberships().contains(new MembershipEntry("/platform/administrators", "*")));
    Assert.assertFalse(spacesAdministrationService.getSpaceCreationMemberships().contains(new MembershipEntry("/platform/users", "editor")));
  }
}
