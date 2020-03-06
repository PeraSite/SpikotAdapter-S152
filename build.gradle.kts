import kr.heartpattern.spikotgradle.spikot

plugins {
    id("kr.heartpattern.spikot") version "4.0.5"
}

group = "kr.heartpattern"
version = "4.0.0-SNAPSHOT"

repositories {
    maven("https://maven.heartpattern.kr/repository/maven-public/")
    mavenLocal()
}

dependencies {
    spikot("4.0.0-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.5.2-R1.1-SNAPSHOT")

    compile("org.apache.logging.log4j:log4j-api:2.13.0")
    compile("org.apache.logging.log4j:log4j-core:2.13.0")
}