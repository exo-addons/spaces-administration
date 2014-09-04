@Portlet
@Application(name = "SpacesAdministration")
@Bindings({
})
@Assets(
  scripts = {
    // jquery is required for the juzu Ajax plugin, and we use it for the portlet
    @Script(id = "jquery", src = "js/jquery-1.7.1.js"),
    @Script(id = "spacesAdministration", src = "js/spaces-administration.js")
  },
  stylesheets = {
    @Stylesheet(src = "style/spaces-administration.css", location = AssetLocation.APPLICATION)
  }

)
@Less("style/spaces-administration.less")

package org.exoplatform.addons.spacesadministration.portlet;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.binding.Bindings;
import juzu.plugin.less.Less;
import juzu.plugin.portlet.Portlet;
