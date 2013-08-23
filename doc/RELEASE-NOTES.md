# Release Notes for `ukf-mda`

## Version 0.7.2

* Implemented `IdPDisplayNameDuplicateDetectingStage`.  As well as resolving a problem with our previous XSLT implementation of this functionality, which was buggy in the presence of entities with the same display name in multiple languages, it also resolves:
	* Bugzilla 803: consider `mdui:DisplayName` when looking for duplicate IdP display names
	* Bugzilla 933: `check_aggregate` should have a case insensitive check for duplicate `OrganizationDisplayName`
	* (unlisted) ignore leading and trailing whitespace for IdP display name comparisons

