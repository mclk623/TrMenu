dependencies {
    compileOnly(project(":common"))
    compileOnly("ink.ptms.core:v12002:12002-minimize:mapped")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    compileOnly(fileTree("libs"))
}

taboolib { subproject = true }