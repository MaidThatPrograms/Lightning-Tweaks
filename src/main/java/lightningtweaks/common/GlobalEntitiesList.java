package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import lightningtweaks.common.event.EntityHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
			if (!LTConfig.getSpawnFire() || LTConfig.getMetallicityThreshold() <= getMetallicity(e.getPosition()))
				ObfuscationReflectionHelper.setPrivateValue(LightningBoltEntity.class, (LightningBoltEntity) e, true,
						"effectOnly");
		}
		return super.add(e);
	}

	/**
	 * TODO
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
		ChunkPos chunkPos = chunk.getPos();
		double max = 0;
		List<BlockPos> poses = new ArrayList<>();
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				BlockPos pos = chunkPos.getBlock(x, heightmap.getHeight(x, z), z);
				double score = getScore(pos);
				if (score > max) {
					max = score;
					poses.clear();
				}
				if (score == max)
					poses.add(pos);
			}
		return poses.get(random.nextInt(poses.size()));
	}

	/**
	 * TODO
	 *
	 * @param pos TODO
	 * @return TODO
	 */
	private double getMetallicity(BlockPos pos) {
		return metallicityMap.getOrDefault(world.getBlockState(pos.offset(Direction.DOWN)).getBlock().asItem(), 0.0);
	}

	/**
	 * TODO
	 *
	 * @param pos TODO
	 * @return TODO
	 */
	private double getScore(BlockPos pos) {
		return (double) pos.getY() / world.getMaxHeight() * LTConfig.getHeightWeight()
				+ getMetallicity(pos) * LTConfig.getMetallicityWeight();
	}
}
