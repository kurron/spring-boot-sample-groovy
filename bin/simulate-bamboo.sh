#!/bin/bash

./gradlew -PpublishArtifacts=true -PrunIntegrationTests=true -PpublishToDockerRegistry=true -PrunAcceptanceTests=true -Dspring.profiles.active=test -Pmajor=0 -Pminor=0 -Ppatch=0 -Pbranch=master -PansiblePlaybookPath=/usr/local/bin/ansible-playbook -PansibleSshUser=vagrant -PansibleSshPassword=vagrant --stacktrace