@Portlet
@Application(name = "SpacesAdministration")
@Bindings({
})
@Scripts({
    // jquery is required for the juzu Ajax plugin, and we use it for the portlet
    @Script(id = "jquery", value = "js/jquery-1.7.1.js"),
    @Script(id = "spacesAdministration", value = "js/spaces-administration.js", depends = "jquery")
  }
)

@Stylesheets({
    @Stylesheet(value = "/org/exoplatform/addons/spacesadministration/portlet/assets/style/spaces-administration.css", id="styleCSS")
  }
)

@Less("style/spaces-administration.less")

@Assets("*")

package org.exoplatform.addons.spacesadministration.portlet;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Scripts;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Stylesheets;
import juzu.plugin.binding.Bindings;
import juzu.plugin.less.Less;
import juzu.plugin.portlet.Portlet;
