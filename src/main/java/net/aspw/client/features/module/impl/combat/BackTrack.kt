package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EntityMovementEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.IntegerValue
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S0CPacketSpawnPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

@ModuleInfo(name = "BackTrack", spacedName = "Back Track", description = "", category = ModuleCategory.COMBAT)
class BackTrack : Module() {

    // This will be used as maximum possible delay. (In milliseconds)
    private val maximumDelay = IntegerValue("MaxDelay", 250, 0, 1000)

    // This will be used to set the maximum data of a player. This can be used to prevent memory leaks and lag.
    // Might be useful on servers with a lot of players or AntiCheat plugins which try to cause issues by exploiting this.
    private val maximumCachedPositions = IntegerValue("MaxCachedPositions", 10, 1, 20)

    private val backtrackedPlayer = mutableMapOf<UUID, MutableList<BacktrackData>>()

    @EventTarget
    fun onPacket(event: PacketEvent) {

        when (val packet = event.packet) {
            // Check if packet is a spawn player packet
            is S0CPacketSpawnPlayer -> {
                // Insert first backtrack data
                addBacktrackData(
                    packet.player,
                    packet.x / 32.0,
                    packet.y / 32.0,
                    packet.z / 32.0,
                    System.currentTimeMillis()
                )
            }
        }
    }

    /**
     * This event is being called when an entity moves (e.g. a player), which is being sent from the server.
     *
     * We use this to track the player movement.
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val entity = event.movedEntity

        // Check if entity is a player
        if (entity is EntityPlayer) {
            // Add new data
            addBacktrackData(entity.uniqueID, entity.posX, entity.posY, entity.posZ, System.currentTimeMillis())
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val color = Color.RED

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityPlayer) {
                GL11.glPushMatrix()
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                GL11.glEnable(GL11.GL_BLEND)
                GL11.glDisable(GL11.GL_DEPTH_TEST)

                mc.entityRenderer.disableLightmap()

                GL11.glBegin(GL11.GL_LINE_STRIP)
                RenderUtils.glColor(color)

                val renderPosX = mc.renderManager.viewerPosX
                val renderPosY = mc.renderManager.viewerPosY
                val renderPosZ = mc.renderManager.viewerPosZ

                loopThroughBacktrackData(entity) {
                    GL11.glVertex3d(entity.posX - renderPosX, entity.posY - renderPosY, entity.posZ - renderPosZ)
                    false
                }

                GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
                GL11.glEnd()
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDisable(GL11.GL_LINE_SMOOTH)
                GL11.glDisable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_TEXTURE_2D)
                GL11.glPopMatrix()
            }
        }
    }

    private fun addBacktrackData(id: UUID, x: Double, y: Double, z: Double, time: Long) {
        // Get backtrack data of player
        val backtrackData = getBacktrackData(id)

        // Check if there is already data of the player
        if (backtrackData != null) {
            // Check if there is already enough data of the player
            if (backtrackData.size >= maximumCachedPositions.get()) {
                // Remove first data
                backtrackData.removeAt(0)
            }

            // Insert new data
            backtrackData.add(BacktrackData(x, y, z, time))
        } else {
            // Create new list
            backtrackedPlayer[id] = mutableListOf(BacktrackData(x, y, z, time))
        }
    }

    private fun getBacktrackData(id: UUID) = backtrackedPlayer[id]

    /**
     * This function will loop through the backtrack data of an entity.
     */
    fun loopThroughBacktrackData(entity: Entity, action: () -> Boolean) {
        if (!state || entity !is EntityPlayer) {
            return
        }

        val backtrackDataArray = getBacktrackData(entity.uniqueID) ?: return
        val entityPosition = entity.positionVector
        val prevPosition = Triple(entity.prevPosX, entity.prevPosY, entity.prevPosZ)

        // This will loop through the backtrack data. We are using reversed() to loop through the data from the newest to the oldest.
        for (backtrackData in backtrackDataArray.reversed()) {
            entity.setPosition(backtrackData.x, backtrackData.y, backtrackData.z)
            entity.prevPosX = backtrackData.x
            entity.prevPosY = backtrackData.y
            entity.prevPosZ = backtrackData.z
            if (action()) {
                break
            }
        }

        // Reset position
        val (prevX, prevY, prevZ) = prevPosition
        entity.prevPosX = prevX
        entity.prevPosY = prevY
        entity.prevPosZ = prevZ

        entity.setPosition(entityPosition.xCoord, entityPosition.yCoord, entityPosition.zCoord)
    }

}

data class BacktrackData(val x: Double, val y: Double, val z: Double, val time: Long)