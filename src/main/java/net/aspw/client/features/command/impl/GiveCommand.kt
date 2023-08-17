package net.aspw.client.features.command.impl

import net.aspw.client.features.command.Command
import net.aspw.client.util.item.ItemUtils
import net.aspw.client.util.misc.StringUtils
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction

class GiveCommand : Command("give", arrayOf("item", "i", "get")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (mc.playerController.isNotCreative) {
            chat("§c§lError: §3You need to be in creative mode.")
            return
        }

        if (args.size > 1) {
            val itemStack = ItemUtils.createItem(StringUtils.toCompleteString(args, 1))

            if (itemStack == null) {
                chatSyntaxError()
                return
            }

            var emptySlot = -1

            for (i in 36..44) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).stack == null) {
                    emptySlot = i
                    break
                }
            }

            if (emptySlot == -1) {
                for (i in 9..44) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).stack == null) {
                        emptySlot = i
                        break
                    }
                }
            }

            if (emptySlot != -1) {
                mc.netHandler.addToSendQueue(C10PacketCreativeInventoryAction(emptySlot, itemStack))
                chat("§7Given [§8${itemStack.displayName}§7] * §8${itemStack.stackSize}§7 to §8${mc.getSession().username}§7.")
            } else
                chat("Your inventory is full.")
            return
        }

        chatSyntax("give <item> [amount] [data] [datatag]")
    }
}