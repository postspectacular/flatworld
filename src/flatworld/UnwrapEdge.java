package flatworld;

import processing.core.PGraphics;
import toxi.geom.Vec2D;
import toxi.geom.mesh.WingedEdge;

public class UnwrapEdge {

    protected WingedEdge edge;
    protected int useCount;
    protected final EdgeRenderStrategy strategy;
    
    public UnwrapEdge(WingedEdge e, int use, EdgeRenderStrategy strategy) {
        this.edge=e;
        this.useCount=use;
        this.strategy = strategy;
    }
    
    public int getID() {
        return edge.id;
    }

    public int getUseCount() {
        return useCount;
    }

    public void reuse() {
        useCount++;
    }

    public void draw(PGraphics g, UnwrappedFace face, Vec2D p, Vec2D q, boolean useLabels) {
        strategy.drawEdge(g, face, this, p, q, useLabels);
    }

    public float getOffset() {
        return strategy.getWidthForEdge(this);
    }
}
