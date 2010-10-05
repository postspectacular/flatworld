package flatworld;

import processing.core.PGraphics;
import toxi.geom.Vec2D;

public class UnwrapEdge {

    protected int useCount;
    protected final EdgeRenderStrategy strategy;
    protected final int id;

    public UnwrapEdge(int id, int use, EdgeRenderStrategy strategy) {
        this.id = id;
        this.useCount = use;
        this.strategy = strategy;
    }

    public void draw(PGraphics g, UnwrappedFace face, Vec2D p, Vec2D q,
            boolean useLabels) {
        strategy.drawEdge(g, face, this, p, q, useLabels);
    }

    public int getID() {
        return id;
    }

    public float getOffset() {
        return strategy.getOffsetWidthForEdge(this);
    }

    public int getUseCount() {
        return useCount;
    }

    public String toString() {
        return id + "/" + useCount;
    }
}
