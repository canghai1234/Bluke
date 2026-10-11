package dev.arnv.bluke

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.core.content.edit
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

private const val APP_PREFERENCES = "app_prefs"

enum class AppLanguageOption(
    val preferenceValue: String,
    val localeTag: String?,
    val label: String,
    val description: String,
) {
    SYSTEM("system", null, "跟随系统", "使用手机当前的语言"),
    CHINESE("zh-CN", "zh-CN", "简体中文", "使用简体中文界面"),
    ENGLISH("en", "en", "English", "Use the English interface"),
    ;

    companion object {
        fun fromPreference(value: String?): AppLanguageOption =
            entries.firstOrNull { it.preferenceValue == value } ?: CHINESE
    }
}

object AppLanguageManager {
    const val LANGUAGE_PREFERENCE_KEY = "app_language"

    @Volatile
    private var currentLanguage: AppLanguageOption = AppLanguageOption.CHINESE
    private val activeActivities = CopyOnWriteArrayList<WeakReference<Activity>>()

    fun initialize(context: Context) {
        currentLanguage = selected(context)
        applyPlatformLocale(context, currentLanguage)
    }

    fun selected(context: Context): AppLanguageOption {
        val value = context
            .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(LANGUAGE_PREFERENCE_KEY, AppLanguageOption.CHINESE.preferenceValue)
        return AppLanguageOption.fromPreference(value)
    }

    fun current(): AppLanguageOption = currentLanguage

    fun isEnglish(): Boolean {
        if (currentLanguage == AppLanguageOption.ENGLISH) return true
        if (currentLanguage != AppLanguageOption.SYSTEM) return false
        return LocaleList.getDefault().get(0)?.language == Locale.ENGLISH.language
    }

    fun select(context: Context, option: AppLanguageOption) {
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).edit {
            putString(LANGUAGE_PREFERENCE_KEY, option.preferenceValue)
        }
        currentLanguage = option
        applyPlatformLocale(context, option)
        recreateActiveActivities()
    }

    fun wrap(context: Context): Context {
        val option = selected(context)
        val localeTag = option.localeTag ?: return context
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(Locale.forLanguageTag(localeTag))
        return context.createConfigurationContext(configuration)
    }

    fun register(activity: Activity) {
        activeActivities.removeAll { it.get() == null || it.get() === activity }
        activeActivities += WeakReference(activity)
    }

    fun unregister(activity: Activity) {
        activeActivities.removeAll { it.get() == null || it.get() === activity }
    }

    private fun applyPlatformLocale(context: Context, option: AppLanguageOption) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val localeManager = context.getSystemService(LocaleManager::class.java) ?: return
        localeManager.applicationLocales = option.localeTag?.let(LocaleList::forLanguageTags)
            ?: LocaleList.getEmptyLocaleList()
    }

    private fun recreateActiveActivities() {
        activeActivities.removeAll { it.get() == null }
        activeActivities.forEach { reference ->
            val activity = reference.get() ?: return@forEach
            if (!activity.isFinishing && !activity.isDestroyed) {
                activity.runOnUiThread { activity.recreate() }
            }
        }
    }
}

fun appText(source: String): String {
    if (!AppLanguageManager.isEnglish()) return source
    EnglishTextCatalog[source]?.let { return it }

    // A few UI messages append runtime values (file names, timers, or counts).
    // Translating the longest known prefix keeps those messages localized too.
    return EnglishTextCatalog.entries
        .asSequence()
        .filter { (key, value) -> key.length >= 2 && key != source && value != key && source.startsWith(key) }
        .maxByOrNull { it.key.length }
        ?.let { (key, value) -> value + source.removePrefix(key) }
        ?: source
}

open class LocalizedActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLanguageManager.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLanguageManager.register(this)
    }

    override fun onDestroy() {
        AppLanguageManager.unregister(this)
        super.onDestroy()
    }
}
