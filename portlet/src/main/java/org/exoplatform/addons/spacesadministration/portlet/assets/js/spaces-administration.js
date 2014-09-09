var resourceBundle = [];

var loadResourceBundle = function() {
  var listContainer = $('#listMemberships');

  listContainer.jzAjax({
    url: 'SpacesAdministrationController.getI18n()',
    type: 'GET',
    dataType: 'json'
  }).done(function(data) {
      for(var i=0; i<data.length; i++) {
        resourceBundle[data[i].key] = data[i].value;
      }
    }).fail(function(data) {
      setMessage('error', 'Error while getting the resource bundle.');
    });
};

var addMembershipinDOM = function(membershipType, group) {
  var listContainer = $('#listMemberships');
  // add it only if it does not already exist
  if(listContainer.find('[data-membership="' + membershipType + ':' + group + '"]').length == 0) {
    listContainer.append('<li data-membership="' + membershipType + ':' + group + '">' +
      '<strong>' + membershipType + '</strong> of <strong>' + group + '</strong> group' +
      '<a data-placement="bottom" rel="tooltip" onclick="javascript:deleteMembership(this, \'' + membershipType + ':' + group + '\');" class="actionIcon" data-original-title="Delete"><i class="uiIconDelete"></i></a>' +
      '</li>');
  }
}

var loadMemberships = function() {
  var listContainer = $('#listMemberships');

  listContainer.jzAjax({
    url: 'SpacesAdministrationController.getMemberships()',
    type: 'GET',
    dataType: 'json'
  }).done(function(data) {
      // clean the list
      listContainer.html('');
      if(data.length == 0) {
        listContainer.append('<div id="noMembershipMessage">' + resourceBundle['spacesadministration.message.noMembership'] + '</div>');
      } else {
        // add the items in the HTML list
        for(var i=0; i<data.length; i++) {
          var membership = data[i].split(':');
          addMembershipinDOM(membership[0], membership[1]);
        }
      }
    }).fail(function(data) {
      setMessage('error', 'Error while getting the memberships.');
    });
};

var addMembership = function() {

  var membershipTypeInput = $('#membershipType');
  var groupInput = $('#group');
  var membershipType = membershipTypeInput.val();
  var group = groupInput.val();

  if(membershipType === '' || group === '') {
    setMessage('error', 'Membership Type and Group are required.');
    return;
  }

  membershipTypeInput.jzAjax({
    url: "SpacesAdministrationController.add()",
    type: 'POST',
    data: 'membership=' + membershipType + ':' + group
  }).done(function(data) {
      setMessage('success', 'Membership ' + membershipType + ':' + group + ' has been added succesfully.');

      // remove the No Membership message
      var noMembershipMessageContainer = $('#noMembershipMessage');
      if(noMembershipMessageContainer != null) {
        noMembershipMessageContainer.remove();
      }

      // add the item in the HTML list
      addMembershipinDOM(membershipType, group);

      membershipTypeInput.val('');
      groupInput.val('');
  }).fail(function(data) {
      setMessage('error', 'Error while adding membership ' + membershipType + ':' + group + '.');
  });

};

var deleteMembership = function(deleteButton, membershipToDelete) {
  var listItem = $(deleteButton).closest('li');
  var listContainer = listItem.closest('ul');

  listItem.jzAjax({
    url: "SpacesAdministrationController.delete()",
    type: 'POST',
    data: 'membership=' + membershipToDelete
  }).done(function(data) {
      // delete the list item
      listItem.remove();

      // add the No Membership message if the list is empty
      if(listContainer.find('li').size() == 0) {
        listContainer.append('<div id="noMembershipMessage">' + resourceBundle['spacesadministration.message.noMembership'] + '</div>');
      }

      setMessage('success', 'Membership ' + membershipToDelete + ' has been deleted succesfully.');
    }).fail(function(data) {
      setMessage('error', 'Error while deleting membership ' + membershipToDelete + '.');
    });
};

var resetToDefaults = function() {
  var listContainer = $('#listMemberships');

  listContainer.jzAjax({
    url: "SpacesAdministrationController.resetToDefaults()",
    type: 'POST',
    dataType: 'json'
  }).done(function(data) {
      // clean the list
      listContainer.html('');
      // add the items in the HTML list
      for(var i=0; i<data.length; i++) {
        var membership = data[i].split(':');
        addMembershipinDOM(membership[0], membership[1]);
      }
      setMessage('success', 'Memberships have been reset to defaults.');
    }).fail(function(data) {
      setMessage('error', 'Error while reseting the memberships.');
    });
};

var setMessage = function(type, message) {
  $('#spacesAdministrationMessage').attr('class', 'alert alert-' + type).css('display', 'block').html('<i class="uiIcon' + type.charAt(0).toUpperCase() + type.slice(1) +'"></i>' + message);
};

var cleanMessage = function() {
  $('#spacesAdministrationMessage').attr('class', '').css('display', 'block').html('');
};

$(function() {
  loadResourceBundle();
  loadMemberships();
});