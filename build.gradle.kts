import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
	id("org.jetbrains.intellij") version "0.4.7"
	kotlin("jvm") version "1.3.21"
}

group   = "com.hawkstech.intellij.plugin"
version = "0.0.2"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

intellij {
	pluginName            = "IntelliJ_Decompiler_Plugin" // name of plugin jar
	downloadSources       = true                         // download IntelliJ sources while initializing Gradle build
	updateSinceUntilBuild = true                         // update plugin.xml with sinceBuild/untilBuild versions (default if not set)
	instrumentCode        = true                         // instrument java classes with nullability assertions and compile forms created by IntelliJ GUI Designer
	version               = "2018.3"                     // earliest IntelliJ release that plugin supports
	setPlugins("java-decompiler" )                       // since I need classes from that bundled plugin
}

repositories {
	jcenter()
}

tasks.withType<PatchPluginXmlTask> {
	changeNotes("""
    <p><strong>0.0.2 initial plugin 2019/03/29</strong></p>
    <ul class="simple">
    <li>Initial plugin for IntelliJ 2018.3</li>
    </ul>
	""".trimIndent())
}
