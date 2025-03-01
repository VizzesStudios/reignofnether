package com.solegendary.reignofnether.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobRenderer.class)
public abstract class MobRendererMixin<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public MobRendererMixin(EntityRendererProvider.Context pContext, M pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Shadow
    private <E extends Entity> void renderLeash(
        T pEntityLiving,
        float pPartialTicks,
        PoseStack pMatrixStack,
        MultiBufferSource pBuffer,
        E pLeashHolder
    ) {
    }

    @Inject(
        method = "render(Lnet/minecraft/world/entity/Mob;FFLcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void render(
        T pEntity,
        float pEntityYaw,
        float pPartialTicks,
        PoseStack pMatrixStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        CallbackInfo ci
    ) {
        ci.cancel();

        try {
            super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
            Entity $$6 = pEntity.getLeashHolder();
            if ($$6 != null) {
                this.renderLeash(pEntity, pPartialTicks, pMatrixStack, pBuffer, $$6);
            }
        } catch (ClassCastException e) {
            pMatrixStack.popPose();
            ReignOfNether.LOGGER.error("Caught ClassCastException (mixin): " + e);
        }
    }
}