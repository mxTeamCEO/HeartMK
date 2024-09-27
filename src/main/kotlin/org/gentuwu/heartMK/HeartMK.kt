package org.gentuwu.heartMK

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class HeartMK : JavaPlugin() {
    private var lastMvCommandTime = 0L

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command is only for players.")
            return true
        }

        val itemInHand: ItemStack = sender.inventory.itemInMainHand

        when (command.name) {
            "mv" -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastMvCommandTime < 60000) {
                    sender.sendMessage("Command cooldown active. Please wait 60 seconds.")
                    return true
                }
                lastMvCommandTime = currentTime

                if (itemInHand.type.isAir) {
                    sender.sendMessage("You must be holding an item.")
                    return true
                }

                val newName = args.getOrNull(0) ?: ""
                val meta = itemInHand.itemMeta
                meta?.setDisplayName(newName)
                itemInHand.itemMeta = meta
                sender.sendMessage("Item renamed to $newName.")
            }
            "head" -> {
                if (itemInHand.type.isAir) {
                    sender.sendMessage("You must be holding an item.")
                    return true
                }

                val headSlot = sender.inventory.helmet
                if (headSlot == null || headSlot.type.isAir) {
                    sender.inventory.helmet = itemInHand
                } else {
                    if (sender.inventory.addItem(headSlot).isNotEmpty()) {
                        sender.sendMessage("Inventory is full. Dropping item.")
                        sender.world.dropItem(sender.location, headSlot)
                    }
                    sender.inventory.helmet = itemInHand
                }
            }
            else -> return false
        }
        return true
    }

    override fun onEnable() {
        // Plugin startup logic
        // Register commands here
        this.getCommand("mv")?.setExecutor(this)
        this.getCommand("head")?.setExecutor(this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}