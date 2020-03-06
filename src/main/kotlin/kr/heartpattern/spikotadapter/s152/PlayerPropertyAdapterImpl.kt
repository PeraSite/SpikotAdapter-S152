package kr.heartpattern.spikotadapter.s152

import kr.heartpattern.spikot.adapter.Adapter
import kr.heartpattern.spikot.adapter.SupportedVersion
import kr.heartpattern.spikot.adapters.PlayerPropertyAdapter
import kr.heartpattern.spikot.misc.MutableProperty
import kr.heartpattern.spikot.misc.MutablePropertyMap
import kr.heartpattern.spikot.misc.Property
import kr.heartpattern.spikot.module.AbstractModule
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.HashMap

@Adapter
@SupportedVersion(version = "1.5.2")
class PlayerPropertyAdapterImpl : AbstractModule(), PlayerPropertyAdapter {
    private val map = HashMap<String, MutablePropertyMap>()

    override fun contains(player: Player, property: Property<*>): Boolean {
        return property in map[player.name]!!
    }

    override fun <T> get(player: Player, property: Property<T>): T {
        return map[player.name]!![property]!!
    }

    override fun <T> remove(player: Player, property: MutableProperty<T>): T? {
        val old = map[player.name]!![property]
        map[player.name]!![property] = null
        return old
    }

    override fun <T> set(player: Player, property: MutableProperty<T>, value: T?) {
        map[player.name]!![property] = value
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerJoinEvent.onPlayerJoin() {
        map[player.name] = MutablePropertyMap()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun PlayerQuitEvent.onPlayerQuit() {
        map.remove(player.name)
    }
}