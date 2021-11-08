/*
 * Rysm, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.qalcyo.rysm.eight.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.ResourcePackListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.eight.hooks.GuiScreenResourcePacksHookKt;

import java.util.List;
import java.util.Objects;

@Mixin(GuiScreenResourcePacks.class)
public class GuiScreenResourcePacksMixin {
    @Shadow private GuiResourcePackAvailable availableResourcePacksList;
    @Shadow private List<ResourcePackListEntry> availableResourcePacks;
    float f = (float) (-1072689136 >> 24 & 255) / 255.0F;
    float f1 = (float) (-1072689136 >> 16 & 255) / 255.0F;
    float f2 = (float) (-1072689136 >> 8 & 255) / 255.0F;
    float f3 = (float) (-1072689136 & 255) / 255.0F;
    float f4 = (float) (-804253680 >> 24 & 255) / 255.0F;
    float f5 = (float) (-804253680 >> 16 & 255) / 255.0F;
    float f6 = (float) (-804253680 >> 8 & 255) / 255.0F;
    float f7 = (float) (-804253680 & 255) / 255.0F;

    @Inject(method = "initGui", at = @At("HEAD"))
    private void addInputField(CallbackInfo ci) {
        GuiScreenResourcePacksHookKt.addInputField();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void handleInputMouse(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        Objects.requireNonNull(GuiScreenResourcePacksHookKt.getInputField()).mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V"))
    private void renderInputField(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiTextField inputField = Objects.requireNonNull(GuiScreenResourcePacksHookKt.getInputField());
        if (!inputField.getText().equalsIgnoreCase(GuiScreenResourcePacksHookKt.getPrevText())) {
            ScaledResolution sr = Objects.requireNonNull(GuiScreenResourcePacksHookKt.getSr());
            GuiScreenResourcePacksHookKt.setPrevText(inputField.getText());
            this.availableResourcePacksList = new GuiResourcePackAvailable(Minecraft.getMinecraft(), 200, sr.getScaledHeight(), GuiScreenResourcePacksHookKt.filterPacks(availableResourcePacks, GuiScreenResourcePacksHookKt.getPrevText()));
            this.availableResourcePacksList.setSlotXBoundsFromLeft(sr.getScaledWidth() / 2 - 4 - 200);
            this.availableResourcePacksList.registerScrollButtons(7, 8);
        }
        inputField.drawTextBox();
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreenResourcePacks;drawBackground(I)V"))
    private void redirectBackground(GuiScreenResourcePacks instance, int i) {
        if (!RysmConfig.INSTANCE.getTransparentPackGUI() || Minecraft.getMinecraft().theWorld == null) {
            instance.drawBackground(i);
        } else {
            drawGradientRect(instance);
        }
    }

    protected void drawGradientRect(GuiScreenResourcePacks instance) {
        float zLevel = ((GuiAccessor) instance).getZLevel();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(instance.width, 0, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(0, 0, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(0, instance.height, zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(instance.width, instance.height, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}