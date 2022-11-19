package net.ccbluex.liquidbounce.features.module.modules.targets

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils

@ModuleInfo(name = "TargetPlayers", category = ModuleCategory.TARGETS, array = false)
class TargetPlayers : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetPlayer = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetPlayer = false
    }

    init {
        state = true
    }
}