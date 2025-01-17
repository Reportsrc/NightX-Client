package net.aspw.client.features.module.impl.movement.speeds.ncp;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;
import net.minecraft.util.AxisAlignedBB;

/**
 * The type Ncp boost.
 */
public class NCPBoost extends SpeedMode {

    private int motionDelay;
    private float ground;

    /**
     * Instantiates a new Ncp boost.
     */
    public NCPBoost() {
        super("NCPBoost");
    }

    @Override
    public void onMotion() {
        double speed = 3.1981D;
        double offset = 4.69D;
        boolean shouldOffset = true;

        for (final Object o : mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX / offset, 0.0D, mc.thePlayer.motionZ / offset))) {
            if (o instanceof AxisAlignedBB) {
                shouldOffset = false;
                break;
            }
        }

        if (mc.thePlayer.onGround && ground < 1.0F)
            ground += 0.2F;

        if (!mc.thePlayer.onGround)
            ground = 0.0F;

        if (ground == 1.0F && shouldSpeedUp()) {
            if (!mc.thePlayer.isSprinting())
                offset += 0.8D;

            if (mc.thePlayer.moveStrafing != 0F) {
                speed -= 0.1D;
                offset += 0.5D;
            }

            if (mc.thePlayer.isInWater())
                speed -= 0.1D;

            motionDelay += 1;

            switch (motionDelay) {
                case 1:
                    mc.thePlayer.motionX *= speed;
                    mc.thePlayer.motionZ *= speed;
                    break;
                case 2:
                    mc.thePlayer.motionX /= 1.458D;
                    mc.thePlayer.motionZ /= 1.458D;
                    break;
                case 4:
                    if (shouldOffset)
                        mc.thePlayer.setPosition(mc.thePlayer.posX + mc.thePlayer.motionX / offset, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.motionZ / offset);

                    motionDelay = 0;
                    break;
            }
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }

    private boolean shouldSpeedUp() {
        return !mc.thePlayer.isInWater() && (!mc.thePlayer.isOnLadder()) && !mc.thePlayer.isSneaking() && MovementUtils.isMoving();
    }
}
