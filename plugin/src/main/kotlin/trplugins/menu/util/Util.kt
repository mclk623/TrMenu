package trplugins.menu.util

import taboolib.common.ClassAppender
import taboolib.common.PrimitiveIO
import taboolib.common.TabooLib
import taboolib.common.io.runningClasses
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.ReflexClass
import taboolib.module.configuration.Configuration
import java.lang.reflect.Modifier
import java.util.function.Supplier

/**
 * @author Arasple
 * @date 2021/2/19 22:40
 */
fun Throwable.print(title: String) {
    println("§c[TrMenu] §8$title")
    println("         §8${localizedMessage}")
    stackTrace.forEach {
        println("         §8$it")
    }
}

val Boolean.trueOrNull get() = if (this) true else null

// 未来需要改进该功能
fun String.parseSimplePlaceholder(map: Map<Regex, String>): String {
    var raw = this
    map.forEach { raw = raw.replace(it.key, it.value) }
    return raw
}

// 未来需要改进该功能
fun String.parseIconId(iconId: String) = parseSimplePlaceholder(mapOf("(?i)@iconId@".toRegex() to iconId))

fun Configuration.ignoreCase(path: String) = getKeys(true).find { it.equals(path, ignoreCase = true) } ?: path


// 极其不稳定的方法, 已停用
/*

inline fun <reified T> fromClassesCollect(`super`: Class<T>) = mutableListOf<T>().also { list ->
    runningClasses.forEach { `class` ->
        if (Modifier.isAbstract(`class`.modifiers)) return@forEach
        list.add(runCatching {
            `class`.asSubclass(`super`).getConstructor().newInstance()
        }.getOrNull() ?: return@forEach)
    }
}
*/

fun <T> List<Class<*>>.fromClassesCollect(`super`: Class<T>, newInstance: Boolean = false, deep: Boolean = false) =
    toTypedArray().fromClassesCollect(`super`, newInstance, deep)

fun <T> Array<Class<*>>.fromClassesCollect(`super`: Class<T>, newInstance: Boolean = false, deep: Boolean = false): MutableList<T> =
    mutableListOf<T>().also { list ->
        this.forEach { `class` ->
            `class`.fromClassCollect(`super`, newInstance, deep).forEach { list.add(it) }
        }
    }

@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.fromClassCollect(`super`: Class<T>, newInstance: Boolean = false, deep: Boolean = false): MutableList<T> =
    mutableListOf<T>().also { list ->
        if (Modifier.isAbstract(this.modifiers)) return@also
        runCatching {
            getInstance(newInstance)!!.get() as T
        }.getOrNull().also {
            list.add(it ?: return@also)
        }

        if (deep) {
            this.classes.fromClassesCollect(`super`, deep).forEach { list.add(it) }
        }
    }



@Suppress("UNCHECKED_CAST")
fun <T> fromCompanionClassesCollect(`super`: Class<T>) = mutableListOf<T>().also { list ->
    runningClasses.forEach { `class` ->
        val instance = runCatching { `class`.getProperty<Any>("Companion", true) as T }.getOrNull() ?: return@forEach
        list.add(instance)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> fromObjectClassesCollect(`super`: Class<T>) = mutableListOf<T>().also { list ->
    runningClasses.forEach { `class` ->
        val instance = runCatching { `class`.getProperty<Any>("INSTANCE", true) as T }.getOrNull() ?: return@forEach
        list.add(instance)
    }
}

/**
 * 取该类在当前项目中被加载的任何实例
 * 例如：@Awake 自唤醒类，或是 Kotlin Companion Object、Kotlin Object 对象
 *
 * @param newInstance 若无任何已加载的实例，是否实例化
 */
fun <T> Class<T>.getInstance(newInstance: Boolean = false): Supplier<T>? {
    // 是否为自唤醒类
    try {
        val awoken = TabooLib.getAwakenedClasses()[name] as? T
        if (awoken != null) {
            return Supplier { awoken }
        }
    } catch (ex: Throwable) {
        when (ex) {
            // 忽略异常
            is ClassNotFoundException, is NoClassDefFoundError -> return null
            // 内部错误
            is InternalError -> {
                PrimitiveIO.println("Failed to get instance: $this")
                ex.printStackTrace()
                return null
            }
        }
    }
    // 反射获取实例字段
    return try {
        // 伴生类
        val instanceObj = if (simpleName == "Companion") {
            ReflexClass.of(classOf(name.substringBeforeLast('$'))).getField("Companion", findToParent = false, remap = false)
        } else {
            ReflexClass.of(this).getField("INSTANCE", findToParent = false, remap = false)
        }
        sup { instanceObj.get() as T }
    } catch (ex: Throwable) {
        when (ex) {
            // 忽略异常
            is ClassNotFoundException, is NoClassDefFoundError, is IllegalAccessError, is IncompatibleClassChangeError -> null
            // 未找到方法
            is NoSuchFieldException -> if (newInstance) sup { getDeclaredConstructor().newInstance() as T } else null
            // 初始化错误 & 内部错误
            is ExceptionInInitializerError, is InternalError -> {
                if (ex.message != "Malformed class name") {
                    PrimitiveIO.println("Failed to get instance: $this")
                    ex.printStackTrace()
                }
                null
            }
            // 其他异常
            else -> throw ex
        }
    }
}

private fun classOf(name: String): Class<*> {
    return Class.forName(name, false, ClassAppender.getClassLoader())
}

private fun <T> sup(supplier: () -> T): Supplier<T> {
    return object : Supplier<T> {

        val value by lazy(LazyThreadSafetyMode.NONE) { supplier() }

        override fun get(): T {
            return value
        }
    }
}
