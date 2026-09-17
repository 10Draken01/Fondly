/**
 * Precompiled [fondly.hilt.gradle.kts][Fondly_hilt_gradle] script plugin.
 *
 * @see Fondly_hilt_gradle
 */
public
class Fondly_hiltPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Fondly_hilt_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
