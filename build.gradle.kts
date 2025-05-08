plugins {
    kotlin("jvm") version "2.1.10"
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.insert-koin:koin-core:4.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    testImplementation(kotlin("test"))
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    //Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")
    //MongoDB
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")


}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
kotlin {
    jvmToolchain(22)
}
val filteredCoverage = fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
    include("**/logic/usecase/**")
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(filteredCoverage)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}
tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(filteredCoverage)
    violationRules {
        rule {
            limit {
                counter = "CLASS"
                value = "COVEREDRATIO"
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}