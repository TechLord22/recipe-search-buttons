package recipesearchbuttons;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.MutablePair;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.util.WeakHashMap;

public enum RSBKeyBinds {
    INGREDIENT_RECIPE("rsb.keybind.find_recipe", KeyConflictContext.IN_GAME, Keyboard.KEY_NUMPAD1),
    INGREDINET_USES("rsb.keybind.find_uses", KeyConflictContext.IN_GAME, Keyboard.KEY_NUMPAD2);

    public static void init() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(RSBKeyBinds.class);
        }
    }

    @SideOnly(Side.CLIENT)
    private KeyBinding keybinding;

    private final WeakHashMap<EntityPlayerMP, MutablePair<Boolean, Boolean>> mapping = new WeakHashMap<>();

    RSBKeyBinds(String unlocalizedName, KeyConflictContext conflictContext, int button) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.keybinding = new KeyBinding(unlocalizedName, conflictContext, button, "rsb.controls.category");
            ClientRegistry.registerKeyBinding(this.keybinding);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isPressed() {
        return this.keybinding.isPressed();
    }

    @SideOnly(Side.CLIENT)
    public boolean isKeyDown() {
        return this.keybinding.isKeyDown();
    }

    public void update(boolean pressed, boolean keyDown, @Nonnull EntityPlayerMP player) {
        MutablePair<Boolean, Boolean> pair = this.mapping.get(player);
        if (pair == null) {
            this.mapping.put(player, MutablePair.of(pressed, keyDown));
        } else {
            pair.left = pressed;
            pair.right = keyDown;
        }
    }

    public boolean isPressed(@Nonnull EntityPlayer player) {
        if (player.world.isRemote) {
            return isPressed();
        } else {
            MutablePair<Boolean, Boolean> pair = this.mapping.get((EntityPlayerMP) player);
            return pair != null && pair.left;
        }
    }

    public boolean isKeyDown(@Nonnull EntityPlayer player) {
        if (player.world.isRemote) {
            return isKeyDown();
        } else {
            MutablePair<Boolean, Boolean> pair = this.mapping.get((EntityPlayerMP) player);
            return pair != null && pair.right;
        }
    }
}
