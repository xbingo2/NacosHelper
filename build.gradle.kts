plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.7.0"
}

group = "com.xbingo"
version = "1.0.7"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.redhogs.cronparser", "cron-parser-core", "3.5") {
        exclude("org.slf4j", "slf4j-api")
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

	plugins.set(listOf("java", "java-i18n", "properties", "yaml"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("233.*")
        changeNotes.set("""       
      <B>1.5</B> 新增base64加密解密、json格式化、json压缩、时间戳日期转换<br>      
	  <B>1.6</B> 新增cron说明提示<br>
      <B>1.7</B> 新增model/vo/dto类属性生成json字符串<br>
      """)
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}