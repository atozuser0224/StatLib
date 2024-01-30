package org.gang.bossmanager2

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import dev.lone.itemsadder.api.CustomStack
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem
import io.github.rysefoxx.inventory.plugin.content.InventoryContents
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.gang.bossmanager2.ia.IaGUI
import org.gang.bossmanager2.ia.IaItems
import java.util.*


class StatAPI : JavaPlugin(),Listener {
    lateinit var invManager : InventoryManager
    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(true)) // Load with verbose output

        CommandAPICommand("test")
            .executes(CommandExecutor { sender, _ ->
                RyseInventory.builder()
                    .title(":offset_-16::blank_menu:")
                    .rows(6)
                    .fixedPageSize(5)
                    .provider(object : InventoryProvider {
                        override fun init(player: Player, contents: InventoryContents) {
                            val pagination = contents.pagination()
                            contents[45] = IntelligentItem.of(IaItems.left_arrow.itemStack) { event: InventoryClickEvent? ->
                                if (pagination.isFirst) {
                                    player.sendMessage("You are on the first page")
                                    return@of
                                }
                                val currentInventory = pagination.inventory()
                                currentInventory.open(player, pagination.previous().page())
                            }
                            val item = ItemStack(Material.GRAY_CONCRETE)
                            val meta = item.itemMeta
                            meta.setDisplayName("${pagination.page()}")
                            item.itemMeta = meta
                            contents.set(
                                22,
                                item
                            )

                            contents[53] = IntelligentItem.of(IaItems.right_arrow.itemStack) { event: InventoryClickEvent? ->
                                if (pagination.isLast()) {
                                    player.sendMessage("You are on the last page")
                                    return@of
                                }
                                val currentInventory = pagination.inventory()
                                currentInventory.open(player, pagination.next().page())
                            }
                        }
                    })
                    .build(this)
                    .openAll()
            })
            .register()
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        CommandAPI.onEnable()
        invManager = InventoryManager(this)
        invManager.invoke()
        // Register commands, listeners etc.
    }

    override fun onDisable() {
        CommandAPI.onDisable()
    }

}
