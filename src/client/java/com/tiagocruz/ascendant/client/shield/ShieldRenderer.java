package com.tiagocruz.ascendant.client.shield;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * Escudo de Energia — esfera 3D real desenhada no pipeline de render do Minecraft.
 *
 * Visual:
 *  - Camada 1: esfera sólida muito translúcida (volume azul)
 *  - Camada 2: 9 paralelos animados (pulsam individualmente)
 *  - Camada 3: 12 meridianos animados
 *
 * Tudo desenhado com TRIANGLES + POSITION_COLOR, sem necessidade de shader de linhas.
 */
public class ShieldRenderer {

    private static int   remainingTicks = 0;
    private static float animTick       = 0f;

    // ── API pública ───────────────────────────────────────────────────────────

    public static void activate(int ticks) {
        remainingTicks = ticks;
        animTick       = 0f;
    }

    public static void deactivate() { remainingTicks = 0; }
    public static boolean isActive() { return remainingTicks > 0; }

    /** Chamado em ClientTickEvents — decrementa o contador e avança a animação. */
    public static void tick() {
        if (remainingTicks > 0) remainingTicks--;
        animTick++;
    }

    /**
     * Registar o render hook. Chamar UMA VEZ em AscendantClient.onInitializeClient().
     * Não precisa de ser chamado no tick.
     */
    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(ShieldRenderer::render);
    }

    // ── Render principal ──────────────────────────────────────────────────────

    private static void render(WorldRenderContext ctx) {
        if (!isActive()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Vec3 cam = ctx.camera().getPosition();
        // Centro da esfera — posição interpolada para movimento suave
        float pt = ctx.tickCounter().getGameTimeDeltaPartialTick(true);
        double px = net.minecraft.util.Mth.lerp(pt, mc.player.xOld, mc.player.getX()) - cam.x;
        double py = net.minecraft.util.Mth.lerp(pt, mc.player.yOld, mc.player.getY()) + 0.95 - cam.y;
        double pz = net.minecraft.util.Mth.lerp(pt, mc.player.zOld, mc.player.getZ()) - cam.z;

        // Raio com pulso suave — suficientemente grande para envolver o player (1.8 blocos)
        float pulse = 1f + 0.04f * sin(animTick * 0.12f);
        float radius = 1.35f * pulse;

        // Fade-out nos últimos 10 ticks
        float alpha = remainingTicks < 10 ? remainingTicks / 10f : 1.0f;

        PoseStack ps = ctx.matrixStack();
        ps.pushPose();
        ps.translate(px, py, pz);
        Matrix4f mat = ps.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);

        // 1. Volume interior (muito translúcido)
        drawFilledSphere(mat, radius * 0.97f, alpha * 0.14f);

        // 2. Grelha animada — paralelos
        drawLatitudeLines(mat, radius, alpha);

        // 3. Grelha animada — meridianos
        drawLongitudeLines(mat, radius, alpha);

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        ps.popPose();
    }

    // ── Esfera preenchida (volume) ────────────────────────────────────────────

    private static void drawFilledSphere(Matrix4f mat, float r, float alpha) {
        int stacks = 18, slices = 24;
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < stacks; i++) {
            float phi1 = (float) Math.PI * i / stacks;
            float phi2 = (float) Math.PI * (i + 1) / stacks;
            for (int j = 0; j < slices; j++) {
                float t1 = 2f * (float) Math.PI * j / slices;
                float t2 = 2f * (float) Math.PI * (j + 1) / slices;

                float x1 = r * sin(phi1) * cos(t1), y1 = r * cos(phi1), z1 = r * sin(phi1) * sin(t1);
                float x2 = r * sin(phi1) * cos(t2), y2 = y1,             z2 = r * sin(phi1) * sin(t2);
                float x3 = r * sin(phi2) * cos(t1), y3 = r * cos(phi2), z3 = r * sin(phi2) * sin(t1);
                float x4 = r * sin(phi2) * cos(t2), y4 = y3,             z4 = r * sin(phi2) * sin(t2);

                float cr = 0.15f, cg = 0.55f, cb = 1.0f;
                buf.addVertex(mat, x1, y1, z1).setColor(cr, cg, cb, alpha);
                buf.addVertex(mat, x2, y2, z2).setColor(cr, cg, cb, alpha);
                buf.addVertex(mat, x3, y3, z3).setColor(cr, cg, cb, alpha);

                buf.addVertex(mat, x2, y2, z2).setColor(cr, cg, cb, alpha);
                buf.addVertex(mat, x4, y4, z4).setColor(cr, cg, cb, alpha);
                buf.addVertex(mat, x3, y3, z3).setColor(cr, cg, cb, alpha);
            }
        }
        BufferUploader.drawWithShader(buf.buildOrThrow());
    }

    // ── Paralelos (linhas horizontais) ─────────────────────────────────────────
    // Cada paralelo é desenhado como uma banda fina de triângulos.

    private static void drawLatitudeLines(Matrix4f mat, float r, float alpha) {
        int div  = 48; // segmentos por círculo
        float hw = 0.013f; // meia-largura da banda

        for (int ring = 1; ring < 10; ring++) {
            float phi   = (float) Math.PI * ring / 10f;
            float yr    = r * cos(phi);
            float xzr   = r * sin(phi);
            // Cada ring pulsa num ritmo ligeiramente diferente
            float ringA = alpha * (0.50f + 0.38f * sin(animTick * 0.13f + ring * 0.95f));
            float cr = 0.30f, cg = 0.75f, cb = 1.0f;

            Tesselator tess = Tesselator.getInstance();
            BufferBuilder buf = tess.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

            for (int j = 0; j < div; j++) {
                float t1 = 2f * (float) Math.PI * j / div;
                float t2 = 2f * (float) Math.PI * (j + 1) / div;

                // Banda: outer = xzr+hw, inner = xzr-hw  (no plano XZ)
                float x1o = (xzr + hw) * cos(t1), z1o = (xzr + hw) * sin(t1);
                float x1i = (xzr - hw) * cos(t1), z1i = (xzr - hw) * sin(t1);
                float x2o = (xzr + hw) * cos(t2), z2o = (xzr + hw) * sin(t2);
                float x2i = (xzr - hw) * cos(t2), z2i = (xzr - hw) * sin(t2);

                buf.addVertex(mat, x1i, yr, z1i).setColor(cr, cg, cb, ringA);
                buf.addVertex(mat, x1o, yr, z1o).setColor(cr, cg, cb, ringA);
                buf.addVertex(mat, x2i, yr, z2i).setColor(cr, cg, cb, ringA);

                buf.addVertex(mat, x1o, yr, z1o).setColor(cr, cg, cb, ringA);
                buf.addVertex(mat, x2o, yr, z2o).setColor(cr, cg, cb, ringA);
                buf.addVertex(mat, x2i, yr, z2i).setColor(cr, cg, cb, ringA);
            }
            BufferUploader.drawWithShader(buf.buildOrThrow());
        }
    }

    // ── Meridianos (linhas verticais) ──────────────────────────────────────────
    // Cada meridiano é uma banda fina de triângulos na superfície da esfera.

    private static void drawLongitudeLines(Matrix4f mat, float r, float alpha) {
        int div       = 48;
        int meridians = 12;
        float hw      = 0.013f;

        for (int m = 0; m < meridians; m++) {
            float theta  = (float) Math.PI * m / (meridians / 2f);
            float meridA = alpha * (0.50f + 0.38f * sin(animTick * 0.11f + m * 0.75f));
            float cr = 0.30f, cg = 0.75f, cb = 1.0f;

            // Vetor tangente horizontal perpendicular ao meridiano
            float tx = -sin(theta), tz = cos(theta);

            Tesselator tess = Tesselator.getInstance();
            BufferBuilder buf = tess.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

            for (int i = 0; i < div; i++) {
                float phi1 = (float) Math.PI * i / div;
                float phi2 = (float) Math.PI * (i + 1) / div;

                float x1 = r * sin(phi1) * cos(theta), y1 = r * cos(phi1), z1 = r * sin(phi1) * sin(theta);
                float x2 = r * sin(phi2) * cos(theta), y2 = r * cos(phi2), z2 = r * sin(phi2) * sin(theta);

                buf.addVertex(mat, x1 + tx * hw, y1, z1 + tz * hw).setColor(cr, cg, cb, meridA);
                buf.addVertex(mat, x1 - tx * hw, y1, z1 - tz * hw).setColor(cr, cg, cb, meridA);
                buf.addVertex(mat, x2 + tx * hw, y2, z2 + tz * hw).setColor(cr, cg, cb, meridA);

                buf.addVertex(mat, x1 - tx * hw, y1, z1 - tz * hw).setColor(cr, cg, cb, meridA);
                buf.addVertex(mat, x2 - tx * hw, y2, z2 - tz * hw).setColor(cr, cg, cb, meridA);
                buf.addVertex(mat, x2 + tx * hw, y2, z2 + tz * hw).setColor(cr, cg, cb, meridA);
            }
            BufferUploader.drawWithShader(buf.buildOrThrow());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static float sin(float a) { return (float) Math.sin(a); }
    private static float cos(float a) { return (float) Math.cos(a); }
}
