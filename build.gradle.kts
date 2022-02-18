plugins {
    java
}

group = "net.flamgop"
version = "1.0.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:be100fa5b8")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("org.projectlombok:lombok:1.18.22")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}