package lightningtweaks.client;

import java.util.List;

import lightningtweaks.LightningTweaks;
import lightningtweaks.common.MetallicityMap;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * TODO
 */
@WailaPlugin
public class MetallicityPlugin implements IComponentProvider, IWailaPlugin {
	private static final ResourceLocation enabled = new ResourceLocation(LightningTweaks.MODID, "metallicity");

	/**
	 * TODO
	 */
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (config.get(enabled))
			tooltip.add(new StringTextComponent(
					"Metallicity: " + Math.round(100 * MetallicityMap.get(accessor.getBlock().asItem())) + '%'));
	}

	/**
	 * TODO
	 */
	public void register(IRegistrar registrar) {
		registrar.addConfig(enabled, false);
		registrar.registerComponentProvider(this, TooltipPosition.BODY, Block.class);
	}
}
