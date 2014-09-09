# Spaces Administration

Define who can create spaces !

Tested on eXo 4.0+, 4.1-M2 and 4.1-RC1.

## Installation

The easiest way to install this extension is to use the addons manager provided in eXo. Go to the root of your eXo installation and execute :

    addon install spaces-administration

Then start eXo.



## User Guide

This extension allows to define who can create spaces, based on memberships. The restriction is done in the UI (no more "Add New Space" button) and at the API level.
Once installed, by default only the users in the group /platform/administrators can create space (*:/platform/administrators).
Several memberships can be defined. The user must have at least one of the memberships to be allowed to create spaces.

There are 2 ways to update the list of memberships :

- by configuration file
- via an UI

The priority order to load the list of the memberships is the following (from highest to lowest) :

- memberships defined via the UI
- memberships defined via the configuration property
- default memberships (*:/platform/administrators)

### Configuration file

A configuration property called exo.spaces.create.memberships allows to define the list of memberships, separated by comma :

- rename the default gatein/conf/exo-sample.properties file to gatein/conf/exo.properties
- add the variable exo.spaces.create.memberships with the new value. For example :

        exo.spaces.create.memberships=*:/platform/administrators,member:/platform/web-contributors

This configuration property is optional. The default value is *:/platform/administrators and it can be updated via the UI.

### UI

A new interface is available once this extension installed, through the menu item "Spaces Administration" in the left menu :

![Spaces Administration](https://raw.github.com/exo-addons/spaces-administration/master/readme-resources/spaces-administration.png)

This interface allows to add or delete memberships.

It also allows to reset the list to its default values, so to the value defined in exo.properties if it is the case or to *:/platform/administrators otherwise.


## Roadmap / Ideas

- i18n
- archive projects (new checkbox in the admin to activate it + new button to archive a space + new portlet to list the archives spaces and restore them)
- configuration of who can see all the spaces, even hidden ones