package com.github.sib_energy_craft.screen;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
@Environment(EnvType.CLIENT)
public class Container9x1Screen extends HandledScreen<GenericContainerScreenHandler>
        implements ScreenHandlerProvider<GenericContainerScreenHandler> {
    private static final Identifier TEXTURE = Identifiers.of("textures/gui/container/generic_63.png");
    private final int rows;

    public Container9x1Screen(@NotNull GenericContainerScreenHandler handler,
                              @NotNull PlayerInventory inventory,
                              @NotNull Text title) {
        super(handler, inventory, title);
        this.passEvents = false;
        this.rows = handler.getRows();
        this.backgroundHeight = 114 + this.rows * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(@NotNull MatrixStack matrices,
                       int mouseX,
                       int mouseY,
                       float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(@NotNull MatrixStack matrices,
                                  float delta,
                                  int mouseX,
                                  int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        drawTexture(matrices, i, j + this.rows * 18, 0, 126, this.backgroundWidth, 114);
    }
}