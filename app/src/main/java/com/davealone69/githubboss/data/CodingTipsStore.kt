package com.davealone69.githubboss.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Local coding tips memory (not ML training).
 * You save tips from builds / Termux / mistakes; Help coach can surface them.
 */
class CodingTipsStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    data class Tip(
        val id: String,
        val title: String,
        val body: String,
        val tags: String = "",
        val createdAt: Long = System.currentTimeMillis()
    )

    fun all(): List<Tip> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        Tip(
                            id = o.optString("id"),
                            title = o.optString("title"),
                            body = o.optString("body"),
                            tags = o.optString("tags"),
                            createdAt = o.optLong("createdAt")
                        )
                    )
                }
            }.sortedByDescending { it.createdAt }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun add(title: String, body: String, tags: String = ""): Tip {
        val tip = Tip(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            body = body.trim(),
            tags = tags.trim()
        )
        val next = listOf(tip) + all()
        save(next)
        return tip
    }

    fun remove(id: String) {
        save(all().filterNot { it.id == id })
    }

    fun clear() {
        prefs.edit().remove(KEY).apply()
    }

    /** Text block injected into Help answers / future prompts. */
    fun asContextBlock(limit: Int = 8): String {
        val tips = all().take(limit)
        if (tips.isEmpty()) return ""
        return buildString {
            appendLine("Your saved coding tips:")
            tips.forEach { t ->
                appendLine("- ${t.title}: ${t.body}")
            }
        }.trim()
    }

    fun search(query: String): List<Tip> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return all()
        return all().filter {
            it.title.lowercase().contains(q) ||
                it.body.lowercase().contains(q) ||
                it.tags.lowercase().contains(q)
        }
    }

    private fun save(tips: List<Tip>) {
        val arr = JSONArray()
        tips.forEach { t ->
            arr.put(
                JSONObject()
                    .put("id", t.id)
                    .put("title", t.title)
                    .put("body", t.body)
                    .put("tags", t.tags)
                    .put("createdAt", t.createdAt)
            )
        }
        prefs.edit().putString(KEY, arr.toString()).apply()
    }

    companion object {
        private const val PREFS = "coding_tips_prefs"
        private const val KEY = "tips_json"
    }
}
