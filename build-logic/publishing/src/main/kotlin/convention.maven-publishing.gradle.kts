plugins {
    `maven-publish`
}

publishing {
    repositories {
        mavenLocal()
        maven(url = uri("https://maven.pkg.github.com/petertrr/merge-data-class")) {
            name = "GitHubPackages"
            credentials {
                username = project.findProperty("gpr.user") as String?
                password = project.findProperty("gpr.key") as String?
            }
        }
    }
    publications {
        publications.withType<MavenPublication>().configureEach {
            this.pom {
                name.set(rootProject.name)
                url.set("https://github.com/petertrr/merge-data-class")
                licenses {
                    license {
                        name.set("MIT Licenst")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("petertrr")
                        name.set("Peter Trifanov")
                        email.set("peter.trifanov@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/petertrr/merge-data-class")
                }
            }
        }
    }
}