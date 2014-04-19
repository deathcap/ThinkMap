package uk.co.thinkofdeath.mapviewer.shared.model;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.LightInfo;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.glmatrix.Quat;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

    private static final RenderChecker ALWAYS_RENDER = new RenderChecker() {
        @Override
        public boolean shouldRenderAgainst(Block other) {
            return true;
        }
    };
    private static final TextureGetter NO_REPLACE_TEXTURE = new TextureGetter() {
        @Override
        public String getTexture(String texture) {
            return texture;
        }
    };

    private List<ModelFace> faces = new ArrayList<>();

    /**
     * Creates a new model
     */
    public Model() {

    }

    //TODO: Render method

    private static LightInfo calculateLight(World world, float x, float y, float z, Face face) {
        int emittedLight = world.getEmittedLight((int) x, (int) y, (int) z);
        int skyLight = world.getSkyLight((int) x, (int) y, (int) z);

        float count = 0;

        // TODO: Clean this up and fix it
        int pox = face.getOffsetX() != 0 ? (face.getOffsetX() == 1 ? 1 : 0) : 2;
        int poy = face.getOffsetY() != 0 ? (face.getOffsetY() == 1 ? 1 : 0) : 2;
        int poz = face.getOffsetZ() != 0 ? (face.getOffsetZ() == 1 ? 1 : 0) : 2;
        // Note: I think the issue is in the following 3 lines
        int nox = face.getOffsetX() != 0 ? (face.getOffsetX() == 1 ? 0 : -1) : -1;
        int noy = face.getOffsetY() != 0 ? (face.getOffsetY() == 1 ? 0 : -1) : -1;
        int noz = face.getOffsetZ() != 0 ? (face.getOffsetZ() == 1 ? 0 : -1) : -1;

        for (int ox = nox; ox < pox; ox++) {
            for (int oy = noy; oy < poy; oy++) {
                for (int oz = noz; oz < poz; oz++) {
                    int bx = (int) (x + ox);
                    int by = (int) (x + oy);
                    int bz = (int) (x + oz);
                    count++;
                    emittedLight += world.getEmittedLight(bx, by, bz);
                    skyLight += world.getSkyLight(bx, by, bz);
                }
            }
        }
        if (count == 0) return new LightInfo((int) emittedLight, (int) skyLight);
        return new LightInfo((int) (emittedLight / count), (int) (skyLight / count));
    }

    private static final List<Face> rotationHelper = Arrays.asList(
            Face.LEFT,
            Face.FRONT,
            Face.RIGHT,
            Face.BACK
    );

    /**
     * Rotates the model around the Y axis by the specified amount of degrees
     *
     * @param deg
     *         The amount to rotate by
     * @return This model
     */
    public Model rotateY(float deg) {
        rotate(deg, 0, 1, 0);
        for (ModelFace face : faces) {
            int idx = rotationHelper.indexOf(face.getFace());
            if (idx != -1) {
                int nIDX = (idx + Math.round(deg / 90)) % rotationHelper.size();
                face.setFace(rotationHelper.get(nIDX));
                if (idx == (nIDX + 2) % rotationHelper.size()) {
                    face.offset = 16 - face.offset;
                }
            }
        }
        return this;
    }


    /**
     * Rotates the model around the X axis by the specified amount of degrees
     *
     * @param deg
     *         The amount to rotate by
     * @return This model
     */
    public Model rotateX(float deg) {
        rotate(deg, 1, 0, 0);
        return this;
    }


    /**
     * Rotates the model around the Z axis by the specified amount of degrees
     *
     * @param deg
     *         The amount to rotate by
     * @return This model
     */
    public Model rotateZ(float deg) {
        rotate(deg, 0, 0, 1);
        return this;
    }

    private void rotate(float deg, float x, float y, float z) {
        // TODO: Pretty sure all these are not needed
        Quat q = Quat.create();
        Quat t1 = Quat.create();
        Quat t2 = Quat.create();
        q.setAxisAngle((float) Math.toRadians(deg), x, y, z);
        t1.conjugate(q);
        for (ModelFace face : faces) {
            for (ModelVertex vertex : face.vertices) {
                float[] vec = {
                        vertex.getX() - 0.5f,
                        vertex.getY() - 0.5f,
                        vertex.getZ() - 0.5f,
                        0
                };
                t2.multiply(t2.multiply(t1, vec));
                vertex.setX((float) (t2.numberAt(0) + 0.5));
                vertex.setY((float) (t2.numberAt(1) + 0.5));
                vertex.setZ((float) (t2.numberAt(2) + 0.5));
            }
        }
    }

    /**
     * Resets the texture coordinates for the model based on the position of its vertices' position
     */
    public void realignTextures() {
        for (ModelFace face : faces) {
            // Correct texture positions
            for (ModelVertex vertex : face.vertices) {
                if (face.getFace() != Face.LEFT && face.getFace() != Face.RIGHT) {
                    vertex.setTextureX(vertex.getX());
                }
                if (face.getFace() == Face.LEFT || face.getFace() == Face.RIGHT) {
                    vertex.setTextureX(vertex.getZ());
                } else if (face.getFace() == Face.TOP || face.getFace() == Face.BOTTOM) {
                    vertex.setTextureY(1 - vertex.getZ());
                }
                if (face.getFace() != Face.TOP && face.getFace() != Face.BOTTOM) {
                    vertex.setTextureY(1 - vertex.getY());
                }
            }
        }
    }

    /**
     * Flips the model upside down
     */
    public void flipModel() {
        for (ModelFace face : faces) {
            for (ModelVertex vertex : face.vertices) {
                vertex.setY(1 - vertex.getY());
            }
            if (face.getFace() == Face.TOP) {
                face.setFace(Face.BOTTOM);
            } else if (face.getFace() == Face.BOTTOM) {
                face.setFace(Face.TOP);
            }
            ModelVertex temp = face.vertices[2];
            face.vertices[2] = face.vertices[1];
            face.vertices[1] = temp;
        }
    }

    /**
     * Joins this model with the other model
     *
     * @param other
     *         The model to join with
     * @return This model
     */
    public Model join(Model other) {
        return join(other, 0, 0, 0);
    }

    /**
     * Joins this model with the other model offset by the passed values
     *
     * @param other
     *         The model to join with
     * @param offsetX
     *         The amount to offset by on the x axis
     * @param offsetY
     *         The amount to offset by on the y axis
     * @param offsetZ
     *         The amount to offset by on the z axis
     * @return This model
     */
    public Model join(Model other, float offsetX, float offsetY, float offsetZ) {
        for (ModelFace face : other.faces) {
            ModelFace newFace = new ModelFace(face.getFace());
            newFace.texture = face.texture;
            newFace.r = face.r;
            newFace.g = face.g;
            newFace.b = face.b;
            faces.add(newFace);
            for (int i = 0; i < 4; i++) {
                ModelVertex newVertex = face.vertices[i].clone();
                newVertex.setX(newVertex.getX() + (offsetX / 16));
                newVertex.setY(newVertex.getY() + (offsetY / 16));
                newVertex.setZ(newVertex.getZ() + (offsetZ / 16));
                newFace.vertices[i] = newVertex;
            }
        }
        return this;
    }

    /**
     * Creates a copy of the model
     *
     * @return The copy
     */
    @Override
    public Model clone() {
        return clone(NO_REPLACE_TEXTURE);
    }

    /**
     * Creates a copy of the model using the TextureGetter to replace the textures of the copy
     *
     * @param textureGetter
     *         The TextureGetter to use for replacing the textures
     * @return The copy
     */
    public Model clone(TextureGetter textureGetter) {
        Model model = new Model();
        for (ModelFace face : faces) {
            ModelFace newFace = new ModelFace(face.getFace());
            newFace.texture = textureGetter.getTexture(face.texture);
            newFace.r = face.r;
            newFace.g = face.g;
            newFace.b = face.b;
            model.faces.add(newFace);
            for (int i = 0; i < 4; i++) {
                newFace.vertices[i] = face.vertices[i].clone();
            }
        }
        return model;
    }

    /**
     * Used for checking whether this model can render against certain blocks
     */
    public static interface RenderChecker {
        /**
         * Returns whether this should render against the other block
         *
         * @param other
         *         The block being rendered against
         * @return Whether it should render
         */
        public boolean shouldRenderAgainst(Block other);
    }

    /**
     * Used for replacing textures in a model
     */
    public static interface TextureGetter {

        /**
         * Returns the texture that should be used in place of the passed texture
         *
         * @param texture
         *         The texture to check against
         * @return The new texture
         */
        public String getTexture(String texture);
    }
}