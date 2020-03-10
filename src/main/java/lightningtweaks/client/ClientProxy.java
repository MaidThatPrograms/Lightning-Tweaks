package lightningtweaks.client;

import lightningtweaks.common.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.effect.EntityLightningBolt;

public class ClientProxy implements IProxy {
    @Override
    public void postInit() {
	RenderManager manager = Minecraft.getMinecraft().getRenderManager();
	manager.entityRenderMap.put(EntityLightningBolt.class, new CustomRenderLightningBolt(manager));
    }
}