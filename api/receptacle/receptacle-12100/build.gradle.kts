dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":api:receptacle:receptacle-common"))
    compileOnly("ink.ptms.core:v12101:12101:mapped")
    compileOnly(fileTree("libs"))
}

taboolib { subproject = true }