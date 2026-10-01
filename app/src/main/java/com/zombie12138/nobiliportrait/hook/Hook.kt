package com.zombie12138.nobiliportrait.hook

import android.net.Uri
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.MembersType
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.type.java.StringClass

class Hook : YukiBaseHooker() {

    private val storyPreloadParams = arrayOf("player_preload", "story_item", "preload_info")

    private val is9x: Boolean by lazy {
        val code = try {
            val ctx = appContext ?: systemContext
            ctx.packageManager.getPackageInfo(packageName, 0)?.versionCode
        } catch (t: Throwable) {
            null
        }
        code == null || code >= 9_000_000
    }

    private fun stripStoryPreload(uri: String): String {
        val queryIndex = uri.indexOf('?')
        if (queryIndex < 0) return uri
        val base = uri.substring(0, queryIndex)
        val kept = uri.substring(queryIndex + 1)
            .split('&')
            .filter { it.isNotEmpty() }
            .filterNot { pair ->
                val key = pair.substringBefore('=')
                storyPreloadParams.any { it == key }
            }
        return if (kept.isEmpty()) base else "$base?" + kept.joinToString("&")
    }

    private fun rewrite(uri: String): String? {
        if (!uri.startsWith("bilibili://story/")) return null
        val video = "bilibili://video/" + uri.substringAfter("bilibili://story/")
        return if (is9x) stripStoryPreload(video) else video
    }

    override fun onHook() {
        loggerD(msg = "9.x 兼容模式(剥离 story 预加载参数)：$is9x")

        // Legacy: rewrite BasicIndexItem.uri (used by 7.x routing). Kept as 7.x fallback; 8.x ignores it.
        "com.bilibili.pegasus.api.model.BasicIndexItem".hook {
            injectMember {
                allMembers(MembersType.CONSTRUCTOR)
                afterHook {
                    val uri = field {
                        name = "uri"
                        type = StringClass
                    }.get(instance)
                    uri.string().let {
                        if (it.isNotEmpty()) {
                            rewrite(it)?.let { rewritten ->
                                uri.set(rewritten)
                                loggerD(msg = "初始化视频信息时，竖屏转横屏成功")
                            }
                        }
                    }
                }
            }
            injectMember {
                method {
                    name = "setUri"
                    param(StringClass)
                }
                beforeHook {
                    (args[0]!! as String).let {
                        if (it.isNotEmpty()) {
                            rewrite(it)?.let { rewritten ->
                                args(0).set(rewritten)
                                loggerD(msg = "设定视频链接时，竖屏转横屏成功")
                            }
                        }
                    }
                }
            }
        }

        // 8.x 起 story URL 由 avid 经 RouteRequest.Builder -> BLRouter 构建（绕过上面的 uri 字段）。
        // ctor 签名在各版本保持不混淆，因此 hook 两个 ctor。
        "com.bilibili.lib.blrouter.RouteRequest\$Builder".hook {
            // new RouteRequest.Builder("bilibili://story/<avid>")
            injectMember {
                constructor { param(StringClass) }
                beforeHook {
                    (args[0] as? String)?.let {
                        rewrite(it)?.let { rewritten ->
                            args(0).set(rewritten)
                            loggerD(msg = "路由构建(String)时，竖屏转横屏成功：$rewritten")
                        }
                    }
                }
            }
            // new RouteRequest.Builder(Uri.parse("bilibili://story/<avid>"))
            injectMember {
                constructor { param(Uri::class.java) }
                beforeHook {
                    (args[0] as? Uri)?.toString()?.let {
                        rewrite(it)?.let { rewritten ->
                            args(0).set(Uri.parse(rewritten))
                            loggerD(msg = "路由构建(Uri)时，竖屏转横屏成功：$rewritten")
                        }
                    }
                }
            }
        }
    }
}
