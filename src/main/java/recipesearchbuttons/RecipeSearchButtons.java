package recipesearchbuttons;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@Mod(name = RecipeSearchButtons.NAME, modid = RecipeSearchButtons.MODID, version = RecipeSearchButtons.VERSION, dependencies = "required-after:jei@[4.15.0.291,)", clientSideOnly = true)
@Mod.EventBusSubscriber
public class RecipeSearchButtons {

    public static final String MODID = "recipesearchbuttons";
    public static final String NAME = "Recipe Search Button";
    public static final String VERSION = "@VERSION@";

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        boolean isUses = RSBKeyBinds.INGREDINET_USES.isPressed(player);
        if (RSBKeyBinds.INGREDIENT_RECIPE.isPressed(player) || isUses) {
            displayRecipes(player, player.getPositionEyes(0), player.getLook(0), mc.playerController.getBlockReachDistance(), true, isUses);
        }
    }

    private static void displayRecipes(@Nonnull EntityPlayer player, @Nonnull Vec3d eyes, @Nonnull Vec3d look, float reach, boolean stopOnLiquid, boolean isUses) {
        Vec3d endPoint = eyes.add(look.x * reach, look.y * reach, look.z * reach);

        World world = player.getEntityWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyes, endPoint, stopOnLiquid);
        if (rayTraceResult != null) {
            BlockPos pos = rayTraceResult.getBlockPos();

            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (!block.isAir(state, world, pos) && block != Blocks.AIR) {
                // liquids
                if (stopOnLiquid) {
                    Fluid fluid = FluidRegistry.lookupFluidForBlock(block);

                    if (fluid != null) {
                        // try the fluid stack directly
                        FluidStack fluidStack = new FluidStack(fluid, 1);
                        if (RSBJeiPlugin.showRecipesForFluidStack(fluidStack, isUses)) return;

                        // try the bucket item if it exists
                        ItemStack stack = FluidUtil.getFilledBucket(fluidStack);
                        if (!stack.isEmpty() && !RSBJeiPlugin.showRecipesForItemStack(stack, isUses)) {
                            // if nothing for the bucket, try it again with the block behind the fluid
                            displayRecipes(player, eyes, look, reach, false, isUses);
                        }
                        return;
                    }
                }

                // blocks
                ItemStack stack = block.getPickBlock(state, rayTraceResult, world, pos, player);

                // if the stack is empty with the pick block, try the constructor
                if (stack.isEmpty()) stack = new ItemStack(block, 1, block.getMetaFromState(state));

                // if the pick block was empty, do nothing
                if (stack.isEmpty()) return;

                RSBJeiPlugin.showRecipesForItemStack(stack, isUses);
            }
        }
    }
}
