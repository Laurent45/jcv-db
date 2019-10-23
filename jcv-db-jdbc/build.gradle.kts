import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    signing
    jacoco
    id("org.jmailen.kotlinter") version "2.1.1"
    id("org.jetbrains.dokka") version "0.9.18"
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(buildDir.resolve("dokka"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs("-Duser.language=en")
    }

    withType<DokkaTask> {
        reportUndocumented = false
    }

    artifacts {
        archives(jar)
        archives(sourcesJar)
        archives(javadocJar)
    }
}

val publicationName = "mavenJava"

publishing {
    publications {
        named<MavenPublication>(publicationName) {
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            from(components["java"])
        }
    }
}

signing {
    sign(publishing.publications[publicationName])
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api(project(":jcv-db-core"))
    implementation(group = "com.ekino.oss.jcv", name = "jcv-core", version = "${project.extra["jcv-core.version"]}")
    implementation(group = "org.skyscreamer", name = "jsonassert", version = "${project.extra["jsonassert.version"]}")

    implementation(group = "org.postgresql", name = "postgresql", version = "${project.extra["postgre.version"]}")
    implementation(group = "com.microsoft.sqlserver", name = "mssql-jdbc", version = "${project.extra["mssql.version"]}")
    implementation(group = "mysql", name = "mysql-connector-java", version = "${project.extra["mysql.version"]}")
    implementation(group = "org.springframework", name = "spring-jdbc", version = "5.2.0.RELEASE")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.10.0")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "${project.extra["junit.version"]}")
    testImplementation(group = "org.testcontainers", name = "junit-jupiter", version = "${project.extra["testcontainers.version"]}")
    testImplementation(group = "org.testcontainers", name = "testcontainers", version = "${project.extra["testcontainers.version"]}")
    testImplementation(group = "org.testcontainers", name = "postgresql", version = "${project.extra["testcontainers.version"]}")
    testImplementation(group = "org.testcontainers", name = "mysql", version = "${project.extra["testcontainers.version"]}")
    testImplementation(group = "org.testcontainers", name = "mssqlserver", version = "${project.extra["testcontainers.version"]}")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.9.1")
}