plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "pl.edu.agh"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.mongodb:mongodb-driver-reactivestreams:5.2.1")

    implementation("io.projectreactor:reactor-core")
    testImplementation("io.projectreactor:reactor-test")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.moeaframework:moeaframework:4.5")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0") //https://github.com/springdoc/springdoc-openapi/issues/2783
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.apache.commons:commons-math3:3.6.1")

    implementation("org.jfree:jfreechart:1.5.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
