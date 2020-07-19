package lightningtweaks.client;

import java.util.List;

import lightningtweaks.LightningTweaks;
import lightningtweaks.common.Resistivity;
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

@WailaPlugin
public class Plugin implements IComponentProvider, IWailaPlugin {
	public static final ResourceLocation CONFIG = new ResourceLocation(LightningTweaks.MODID, "resistivity");

	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (config.get(CONFIG))
			tooltip.add(
					new StringTextComponent("Resistivity: " + Resistivity.BLOCKS.get(accessor.getBlock()) + " Ω⋅m"));
	}

	@Override
	public void register(IRegistrar registrar) {
		registrar.addConfig(CONFIG, false);
		registrar.registerComponentProvider(this, TooltipPosition.BODY, Block.class);
	}
}
