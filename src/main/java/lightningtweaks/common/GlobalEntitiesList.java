package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import lightningtweaks.common.event.EntityHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;

/**
 * Modified version of an {@link ArrayList} of type {@link Entity}. The only
 * behavior that is changed is {@link #add(Entity)} to catch the given
 * {@link Entity} and apply this mod's logic to it.
 */
public class GlobalEntitiesList extends ArrayList<Entity> {
	private static final SplittableRandom random = new SplittableRandom();
	private final MetallicityMap metallicityMap;
	private final IWorld world;

	/**
	 * Constructs an instance of {@link GlobalEntitiesList} on an instance of
	 * {@link ServerWorld}. The given {@link ServerWorld} should be the same
	 * instance of {@link ServerWorld} that this {@link GlobalEntitiesList} is
	 * applied to. A {@link MetallicityMap} is also generated.
	 * <hr>
	 * <b>Basically, the following statements should be true:</b>
	 * <ul>
	 * <li><code>(({@link ServerWorld}) world).{@link ServerWorld#globalEntities globalEntities} == {@link GlobalEntitiesList this}</code></li>
	 * </ul>
	 *
	 * @param world the {@link ServerWorld} that this {@link GlobalEntitiesList} is
	 *              applied to
	 */
	public GlobalEntitiesList(ServerWorld world) {
		metallicityMap = new MetallicityMap(world.getRecipeManager());
		this.world = world;
	}

	/**
	 * {@inheritDoc}
	 * <hr>
	 * Beyond the normal functionality of {@link #add(Entity)}, this method also
	 * calls {@link EntityHandler#revertDifficulty(IWorld)},
	 * {@link #getBlockPos(Entity)}, and finally
	 * {@link Entity#setPosition(double, double, double) sets the position} of the
	 * given {@link Entity} to the returned {@link BlockPos}. The latter two
	 * operations only occur if {@link LTConfig#getRealisticLightning()} returns
	 * true.<br>
	 * <br>
	 * There is no check that the given {@link Entity} is an instance of
	 * {@link LightningBoltEntity} because it appears that
	 * {@link LightningBoltEntity} is the only thing ever passed into this method.
	 * If this ever ceases to be the case or another mod passes something else in, a
	 * check will be added.<br>
	 * <br>
	 */
	@Override
	public boolean add(Entity e) {
		EntityHandler.revertDifficulty(world);
		if (LTConfig.getRealisticLightning()) {
			BlockPos pos = getBlockPos(e);
			e.setPosition(pos.getX(), pos.getY(), pos.getZ());
		}
		return super.add(e);
	}

	/**
	 * TODO Finds the highest {@link Block} or {@link Block}s in the {@link IChunk}
	 * of the given {@link Entity}. This is done by accessing the {@link IChunk}'s
	 * {@link Heightmap} of {@link Type#MOTION_BLOCKING}. That {@link Type} is used
	 * to match the vanilla method
	 * {@link ServerWorld#adjustPosToNearbyEntity(BlockPos)} which used before
	 * naturally spawning {@link LightningBoltEntity LightningBoltEntities}. If
	 * multiple {@link BlockPos}es have equal scores, one is chosen randomly using
	 * {@link SplittableRandom#nextInt(int)}.
	 *
	 * @param e the {@link Entity} whose {@link IChunk} will be searched
	 * @return the {@link BlockPos} of one of the highest scored {@link Block}s in
	 *         the given {@link Entity}'s {@link IChunk} using
	 *         {@link #getScore(IChunk, BlockPos)}, according to
	 *         {@link Type#MOTION_BLOCKING}
	 */
	private BlockPos getBlockPos(Entity e) {
		IChunk chunk = world.getChunk(e.getPosition());
		Heightmap heightmap = chunk.getHeightmap(Type.MOTION_BLOCKING);
		double max = 0;
		List<BlockPos> poses = new ArrayList<>();
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				BlockPos pos = new BlockPos(x, heightmap.getHeight(x, z) - 1, z);
				double score = getScore(chunk, pos);
				if (score > max) {
					max = score;
					poses.clear();
				}
				if (score == max)
					poses.add(pos);
			}
		BlockPos pos = poses.get(random.nextInt(poses.size()));
		return chunk.getPos().getBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * TODO
	 *
	 * @param chunk TODO
	 * @param pos   TODO
	 * @return TODO
	 */
	private double getScore(IChunk chunk, BlockPos pos) {
		return pos.getY() / world.getMaxHeight() * LTConfig.getHeightWeight()
				+ metallicityMap.getOrDefault(chunk.getBlockState(pos).getBlock().asItem(), 0.0)
						* LTConfig.getMetallicityWeight();
	}
}
