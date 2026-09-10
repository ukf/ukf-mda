# ukf-mda

## UK federation Aggregator Beans

This project contains a collection of beans supporting metadata aggregation for the UK federation, based on the [Shibboleth metadata aggregator framework](http://shibboleth.net/products/metadata-aggregator.html).

As much as makes sense of the software developed here will be contributed to the main line Shibboleth MDA codebase once mature.

Some insight into the ways we're using these components in our [production deployment](https://github.com/ukf/ukf-meta) can be found in [this blog entry](http://iay.org.uk/blog/2012/08/uk-federation-metadata-aggregation).

## Release Process

To release a new version of `ukf-mda`:

1. Update `RELEASE-NOTES.md`
2. The following bash commands can be used (remembering to appropriately replace the versions):
3. Create a new release in GitHub https://github.com/ukf/ukf-mda/releases/new
```
RELEASE_VERSION="1.0.1"
NEXT_SNAPSHOT_VERSION="1.0.2-SNAPSHOT"

# Version release commit and tag
mvn versions:set -DnewVersion="${RELEASE_VERSION}" -DgenerateBackupPoms=false
git add .
git commit -m "Set version for ${RELEASE_VERSION} release"
git tag -s -m "Tag as version ${RELEASE_VERSION}" "${RELEASE_VERSION}"

# Post-release version commit
mvn versions:set -DnewVersion="${NEXT_SNAPSHOT_VERSION}" -DgenerateBackupPoms=false
git add .
git commit -m "Set snapshot version after release"

# Perform build and release on detached HEAD
git checkout "${RELEASE_VERSION}"
mvn -Prelease clean verify

# test results

# Build it again for real
mvn -Prelease,sign clean deploy

# Commit this release to the ages
git checkout main
git push
git push origin "${RELEASE_VERSION}"
```



In order for the `deploy` step to upload the built artefact to `ukf-packages`, ensure your Maven settings (`settings.xml`) contain the following server declaration with the correct credentials for `ukf-packages`. Credentials are based on GitHub personal access tokens with the `write:packages` authorisation scope.

```
<!--
     GitHub ukf/packages.

     UKf packages repo, scope write:packages

     No expiration.
 -->
<server>
       <id>ukf-packages</id>
       <username>username</username>
       <password>access_token</password>
</server>

```

## Copyright and License

The contents of this repository are Copyright (C) the named contributors or their
employers, as appropriate.

In particular, all content authored prior to the 1st of August 2016 is
Copyright (C) 2011&mdash;2016, University of Edinburgh.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
