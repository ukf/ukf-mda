# Release Notes for `ukf-mda`

## Version 0.9.0 ##

* Rebased on Shibboleth MDA 0.9.0 and related components.
* Shibboleth `Resource` replaced by Spring `Resource` throughout.
* No longer requires endorsed JAXP libraries.

## Version 0.8.8 ##

* Added `NamespacesStrippingStage`.

## Version 0.8.7 ##

* Added `SAMLStringElementCheckingStage`.

## Version 0.8.6 ##

* Added a general `Container` framework to help with construction of properly formatted hierarchical XML.
* Added `EntityAttributeAddingStage`.

## Version 0.8.5 ##

* Issue #16: array bounds exception thrown by `IPHintValidationStage`.

## Version 0.8.4 ##

* Added `EntityAttributeFilteringStage` and associated matcher classes:
	* `EntityCategoryMatcher`
	* `EntityCategorySupportMatcher`
	* `MultiPredicateMatcher`
	* `RegistrationAuthorityMatcher`

## Version 0.8.3 ##

* Issue #2: duplicate ODN detector should allow setting naming strategy for clashing entity
* Issue #7: duplicate ODN detector can be fooled by inconsistent case
* Issue #9: allow blank lines in blacklist files
* Issue #10: shorten class names on X.509 validators
* Improved error status messages from `X509RSAOpenSSLBlacklistValidator`.

## Version 0.8.2

* `Validator` beans are now identifiable, initializable, destructable components, in the same way that `Stage`s are.
* Added `X509CertificateConsistentNameValidator` to check for consistency between an embedded certificate's subject `CN` and any DNS Subject Alternative Names. A bean property controls whether an `ErrorStatus` or `WarningStatus` is applied in the case of failure.
* Added the `X509CertificateRSAExponentValidator`, which validates the (public) exponent in an RSA public key. Value boundaries can be set for warning and error conditions.
* Added the `X509CertificateRSAOpenSSLBlacklistValidator`, which validates the modulus in an RSA public key against OpenSSL format blacklists to detect Debian weak keys.

## Version 0.8.1

* Refactored the DOM visitor framework so that the basic traversal case is separated from the more specific case where we want to visit one of a set of `QName`s.
* Refactored the `IPHint` validator, which had its own private first cut on such a framework so that it uses this new system instead.
* Introduced a new system of validators to be attached to traversals.
* Added `X509CertificateValidationStage` which makes use of the new  traversal and validator frameworks. This allows a list of validators to be applied to each X.509 certificate in each item.
* Added the `X509CertificateRSAKeyLengthValidator`, which validates the RSA public key in an X.509 certificate. Length boundaries can be set for warning and error conditions. This allows us to resolve Bugzillas 758 and 831.

## Version 0.8.0 ##

* Rebase on Shibboleth MDA version 0.8.0.

## Version 0.7.4 ##

* Move to Java 7.  Take advantage of Java 7 type inference.
* Introduce an initial DOM visitor framework on which to base new types of stage.
* \[Issue #1\]: Use `Collection` instead of `Set` in API for ignored registrars in UK naming strategy.
* \[Issue #4\]: implement `ElementWhitespaceTrimmingStage`.
* \[Issue #6\]: reorganize test data.

## Version 0.7.3 ##

* Implemented a `RegistrationAuthority` item metadata object, wrapping a `String` representing the `registrationAuthority` attribute of the `mdrpi:RegistrationInfo` element from the MDRPI specification.
* Implemented a `RegistrationAuthorityPopulationStage` stage to populate an entity's `RegistrationAuthority` item metadata.
* Extended the `UKItemIdentificationStrategy` to incorporate an item's `RegistrationAuthority` metadata.  Facilities are included to allow a set of registrar URIs to be ignored entirely (useful for "ours"), and to allow the mapping of registrar URIs to more readable display names.

## Version 0.7.2

* Implemented `IdPDisplayNameDuplicateDetectingStage`.  As well as resolving a problem with our previous XSLT implementation of this functionality, which was buggy in the presence of entities with the same display name in multiple languages, it also resolves:
	* Bugzilla 803: consider `mdui:DisplayName` when looking for duplicate IdP display names
	* Bugzilla 933: `check_aggregate` should have a case insensitive check for duplicate `OrganizationDisplayName`
	* (unlisted) ignore leading and trailing whitespace for IdP display name comparisons

