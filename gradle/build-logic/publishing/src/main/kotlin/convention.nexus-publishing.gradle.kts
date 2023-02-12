plugins {
    id("io.github.gradle-nexus.publish-plugin")
    signing
}

signing {
    val signingKey = findProperty("signingKey") as String?
    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, property("signingPassword") as String?)
        logger.lifecycle("The following publications are getting signed: ${extensions.getByType<PublishingExtension>().publications.map { it.name }}")
        sign(*extensions.getByType<PublishingExtension>().publications.toTypedArray())
    } else {
        logger.info("Skipping signing of artifacts, because signing keys are not configured")
    }
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
