package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import lightningtweaks.common.event.EntityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Modified version of an {@link ArrayList} of type {@link Entity}. The only
 * behavior that is changed is {@link #add(Entity)} to catch the given
 * {@link Entity} and apply this mod's logic to it.
 */
public class GlobalEntitiesList extends ArrayList<Entity> {
	private static final SplittableRandom random = new SplittableRandom();
	private final IWorld world;

	/**
	 * Constructs an instance of {@link GlobalEntitiesList} on an instance of
	 * {@link ServerWorld}. The given {@link ServerWorld} should be the same
	 * instance of {@link ServerWorld} that this {@link GlobalEntitiesList} is
	 * applied to.
	 * <hr>
	 * <b>Basically, the following statements should be true:</b>
	 * <ul>
	 * <li><code>world instanceof {@link ServerWorld}</code></li>
	 * <li><code>(({@link ServerWorld}) world).{@link ServerWorld#globalEntities globalEntities} == {@link GlobalEntitiesList this}</code></li>
	 * </ul>
	 *
	 * @param world the {@link ServerWorld} that this {@link GlobalEntitiesList} is
	 *              applied to
	 */
	public GlobalEntitiesList(IWorld world) {
		this.world = world;
	}

	/**
	 * {@inheritDoc}
	 * <hr>
	 * TODO
	 */
	@Override
	public boolean add(Entity e) {
		EntityHandler.revertDifficulty(world);
		if (e instanceof LightningBoltEntity) {
			if (LTConfig.getRealisticLightning()) {
				BlockPos pos = getBlockPos(e);
				e.setPosition(pos.getX(), pos.getY(), pos.getZ());
			}
			if (!LTConfig.getSpawnFire() || LTConfig.getMetallicityThreshold() <= MetallicityMap
					.get(world.getBlockState(e.getPosition().offset(Direction.DOWN)).getBlock().asItem()))
				ObfuscationReflectionHelper.setPrivateValue(LightningBoltEntity.class, (LightningBoltEntity) e, true,
						"effectOnly");
		}
		return super.add(e);
	}

	/**
	 * Finds the highest {@link Block} or {@link Block}s in the {@link IChunk} of
	 * the given {@link Entity}. This is done by accessing the {@link IChunk}'s
	 * {@link Heightmap} of {@link Type#MOTION_BLOCKING}. That {@link Type} is used
	 * to match the vanilla method
	 * {@link ServerWorld#adjustPosToNearbyEntity(BlockPos)} which used before
	 * naturally spawning {@link LightningBoltEntity LightningBoltEntities}. If
	 * multiple {@link BlockPos}es have equal heights, one is chosen randomly using
	 * {@link SplittableRandom#nextInt(int)}.
	 *
	 * @param e the {@link Entity} whose {@link IChunk} will be searched
	 * @return the {@link BlockPos} of one of the highest {@link Block}s in the
	 *         given {@link Entity}'s {@link IChunk}, according to
	 *         {@link Type#MOTION_BLOCKING}
	 */
	private BlockPos getBlockPos(Entity e) {
		IChunk chunk = world.getChunk(e.getPosition());
		Heightmap heightmap = chunk.getHeightmap(Type.MOTION_BLOCKING);
		int max = 0;
		List<BlockPos> poses = new ArrayList<>();
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				int height = heightmap.getHeight(x, z);
				if (height > max) {
					max = height;
					poses.clear();
				}
				if (height == max)
					poses.add(new BlockPos(x, height, z));
			}
		BlockPos pos = poses.get(random.nextInt(poses.size()));
		return chunk.getPos().getBlock(pos.getX(), pos.getY(), pos.getZ());
	}
}
