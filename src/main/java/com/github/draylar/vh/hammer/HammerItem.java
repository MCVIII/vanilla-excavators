package com.github.draylar.vh.hammer;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import java.util.Set;

public class HammerItem extends PickaxeItem
{
    private static final Set<Block> EFFECTIVE_BLOCKS = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);
    private static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(Material.GLASS, Material.ICE, Material.STONE, Material.PACKED_ICE, Material.ANVIL, Material.METAL, Material.REDSTONE_LAMP);

    public HammerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed)
    {
        super(toolMaterial, attackDamage, attackSpeed, new Item.Settings().itemGroup(ItemGroup.TOOLS));
    }

    @Override
    public boolean beforeBlockBreak(BlockState state, World world, BlockPos blockPos, PlayerEntity player)
    {
        if(world.isClient) return true;

        if(!EFFECTIVE_BLOCKS.contains(state.getBlock()) && !EFFECTIVE_MATERIALS.contains(state.getMaterial())) return true;

        Vec3d vec3d_1 = player.getCameraPosVec(1);
        Vec3d vec3d_2 = player.getRotationVec(1);
        Vec3d vec3d_3 = vec3d_1.add(vec3d_2.x * 5, vec3d_2.y * 5, vec3d_2.z * 5);
        BlockHitResult result = world.rayTrace(new RayTraceContext(vec3d_1, vec3d_3, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.ANY, player));

        // HitResult result = player.rayTrace(5, 1, true);

        if(result.getType() == HitResult.Type.BLOCK)
        {
            Direction.Axis axis = result.getSide().getAxis();

            // we can pass null because the method doesn't use the variables :)
            float strength = state.getBlock().getHardness(null, null, null);

            if (axis == Direction.Axis.Y)
            {
                attemptBreakBlock(world, blockPos.offset(Direction.NORTH), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.EAST), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.SOUTH), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.WEST), player, strength);

                attemptBreakBlock(world, blockPos.offset(Direction.NORTH).offset(Direction.EAST), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.EAST).offset(Direction.SOUTH), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.SOUTH).offset(Direction.WEST), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.WEST).offset(Direction.NORTH), player, strength);
            }

            else if (axis == Direction.Axis.Z)
            {
                attemptBreakBlock(world, blockPos.offset(Direction.WEST), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.EAST), player, strength);

                attemptBreakBlock(world, blockPos.offset(Direction.UP), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.DOWN), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.WEST).offset(Direction.UP), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.EAST).offset(Direction.UP), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.WEST).offset(Direction.DOWN), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.EAST).offset(Direction.DOWN), player, strength);
            }

            else if (axis == Direction.Axis.X)
            {
                attemptBreakBlock(world, blockPos.offset(Direction.NORTH), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.SOUTH), player, strength);

                attemptBreakBlock(world, blockPos.offset(Direction.UP), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.DOWN), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.NORTH).offset(Direction.UP), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.SOUTH).offset(Direction.UP), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.NORTH).offset(Direction.DOWN), player, strength);
                attemptBreakBlock(world, blockPos.offset(Direction.SOUTH).offset(Direction.DOWN), player, strength);
            }
        }

        return super.beforeBlockBreak(state, world, blockPos, player);
    }

    public void attemptBreakBlock(World world, BlockPos pos, PlayerEntity playerEntity, float originStrength)
    {
        if(!world.isClient)
        {
            if (EFFECTIVE_BLOCKS.contains(world.getBlockState(pos).getBlock()) || EFFECTIVE_MATERIALS.contains(world.getBlockState(pos).getMaterial()))
            {
                if(world.getBlockState(pos).getBlock().getHardness(null, null, null) <= originStrength * 2)
                {
                    if(this.isEffectiveOn(world.getBlockState(pos)))
                    {
                        BlockState state = world.getBlockState(pos);
                        world.breakBlock(pos, false);

                        if (!playerEntity.isCreative())
                        {
                            Block.dropStacks(state, world, pos, null, playerEntity, playerEntity.inventory.getMainHandStack());
                            playerEntity.inventory.getMainHandStack().applyDamage(1, world.random, null);
                        }
                    }
                }
            }
        }
    }
}
