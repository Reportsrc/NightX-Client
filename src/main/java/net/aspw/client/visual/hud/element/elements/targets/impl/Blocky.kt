package net.aspw.client.visual.hud.element.elements.targets.impl

import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.elements.TargetHud
import net.aspw.client.visual.hud.element.elements.targets.TargetStyle
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs

class Blocky(inst: TargetHud) : TargetStyle("Blocky", inst, true) {
    private var lastTarget: EntityPlayer? = null
    val font = Fonts.minecraftFont

    override fun drawTarget(entity: EntityPlayer) {
        val healthString = "${decimalFormat2.format(entity.health)}"

        if (entity != lastTarget || easingHealth < 0 || easingHealth > entity.maxHealth ||
            abs(easingHealth - entity.health) < 0.01
        ) {
            easingHealth = entity.health
        }
        (38 + font.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        updateAnim(entity.health)

        // Draw rect box
        RenderUtils.drawRect(6F, -1F, 130F, 32F, targetHudInstance.bgColor.rgb)

        // Health bar
        val barLength = 69F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(
            37F,
            26F,
            58F + 69F,
            30F,
            getColor(RenderUtils.skyRainbow(0, 0.6f, 1f)).darker().rgb
        )
        RenderUtils.drawRect(
            37F,
            26F,
            58F + barLength,
            30F,
            getColor(RenderUtils.skyRainbow(0, 0.6f, 1f)).rgb
        )

        updateAnim(entity.health)

        // Name
        Fonts.fontPixel.drawString(entity.name, 37F, 4F, getColor(-1).rgb)

        // HP
        GL11.glPushMatrix()
        GL11.glScalef(1F, 1F, 1F)
        Fonts.fontPixel.drawString(healthString + "HP", 96F, 16.2F, Color(255, 255, 255).rgb)
        GL11.glPopMatrix()

        GlStateManager.resetColor()
        RenderUtils.drawEntityOnScreen(22, 30, 15, entity)

        lastTarget = entity
    }

    override fun handleBlur(entity: EntityPlayer) {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(6F, -1F, 130F, 32F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun handleShadowCut(entity: EntityPlayer) = handleBlur(entity)

    override fun handleShadow(entity: EntityPlayer) {
        RenderUtils.newDrawRect(6F, -1F, 130F, 32F, shadowOpaque.rgb)
    }

    override fun getBorder(entity: EntityPlayer?): Border {
        entity ?: return Border(0F, 0F, 118F, 32F)
        return Border(6F, -1F, 130F, 32F)
    }
}