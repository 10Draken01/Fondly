/**
 * Precompiled [fondly.jvm.library.gradle.kts][Fondly_jvm_library_gradle] script plugin.
 *
 * @see Fondly_jvm_library_gradle
 */
public
class Fondly_jvm_libraryPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Fondly_jvm_library_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
