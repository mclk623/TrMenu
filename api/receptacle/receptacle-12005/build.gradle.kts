dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":api:receptacle:receptacle-common"))
    compileOnly(project(":api:receptacle:receptacle-common"))
    compileOnly("ink.ptms.core:v12005:12005:mapped")
    compileOnly(fileTree("libs"))
}

taboolib { subproject = true }