package recipesearchbuttons;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IRecipesGui;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class RSBJeiPlugin implements IModPlugin {

    private static final Style RED_STYLE = new Style().setColor(TextFormatting.RED);

    private static IJeiRuntime jeiRuntime;

    public RSBJeiPlugin() {

    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        RSBJeiPlugin.jeiRuntime = jeiRuntime;
    }

    public static boolean showRecipesForItemStack(@Nonnull ItemStack stack, boolean isUses) {
        jeiRuntime.getRecipesGui().show(jeiRuntime.getRecipeRegistry().createFocus(isUses ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT, stack));
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof IRecipesGui)) {
            mc.player.sendStatusMessage(new TextComponentTranslation(isUses ? "rsb.message.no_uses.found" : "rsb.message.no_recipes_found").setStyle(RED_STYLE), true);
            return false;
        }
        return true;
    }

    public static boolean showRecipesForFluidStack(@Nonnull FluidStack stack, boolean isUses) {
        jeiRuntime.getRecipesGui().show(jeiRuntime.getRecipeRegistry().createFocus(isUses ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT, stack));
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof IRecipesGui)) {
            mc.player.sendStatusMessage(new TextComponentTranslation(isUses ? "rsb.message.no_uses.found" : "rsb.message.no_recipes_found").setStyle(RED_STYLE), true);
            return false;
        }
        return true;
    }
}

