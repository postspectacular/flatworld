package flatworld;

import processing.core.PGraphics;
import toxi.geom.Vec2D;

public abstract class EdgeRenderStrategy {

    public abstract void drawEdge(PGraphics g, UnwrappedFace face,
            UnwrapEdge edge, Vec2D a, Vec2D b, boolean useLabels);

    public void drawLabel(PGraphics g, UnwrapEdge edge, Vec2D pos) {
        g.fill(0);
        g.text(edge.getID() + "/" + edge.getUseCount(), pos.x, pos.y);
    }

    public abstract float getWidthForEdge(UnwrapEdge edge);
}
