package com.github.sib_energy_craft.pipes.screen;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.13
 * @author sibmaks
 */
@Environment(EnvType.CLIENT)
public class OneSlotScreen extends HandledScreen<OneSlotScreenHandler>
        implements ScreenHandlerProvider<OneSlotScreenHandler> {
    private static final Identifier TEXTURE = Identifiers.of("textures/gui/container/one_slot.png");

    public OneSlotScreen(@NotNull OneSlotScreenHandler handler,
                         @NotNull PlayerInventory inventory,
                         @NotNull Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 131;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(@NotNull DrawContext drawContext,
                       int mouseX,
                       int mouseY,
                       float delta) {
        this.renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext drawContext,
                                  float delta,
                                  int mouseX,
                                  int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        drawContext.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}