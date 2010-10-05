package flatworld;

import processing.core.PGraphics;
import toxi.geom.Line2D;
import toxi.geom.Vec2D;

public class GlueTabEdgeStrategy extends EdgeRenderStrategy {

    public float width;
    public float insetPos;

    public GlueTabEdgeStrategy(int w, float inset) {
        this.width = w;
        this.insetPos = inset;
    }

    public void drawEdge(PGraphics g, UnwrappedFace face, UnwrapEdge edge,
            Vec2D a, Vec2D b, boolean useLabels) {
        Line2D l = new Line2D(a, b);
        Vec2D m = l.getMidPoint();
        Vec2D n = l.getDirection().perpendicular();
        if (m.sub(face.sheetPos).dot(n) < 0) {
            n.invert();
        }
        g.stroke(0);
        if (1 == edge.getUseCount() % 2) {
            Vec2D off = n.normalizeTo(width);
            g.noFill();
            g.beginShape();
            g.vertex(a.x, a.y);
            Vec2D aa = a.interpolateTo(b, insetPos);
            g.vertex(aa.x + off.x, aa.y + off.y);
            Vec2D bb = b.interpolateTo(a, insetPos);
            g.vertex(bb.x + off.x, bb.y + off.y);
            g.vertex(b.x, b.y);
            g.endShape();
        }
        g.line(a.x, a.y, b.x, b.y);
        if (useLabels) {
            m.subSelf(n.normalizeTo(8));
            drawLabel(g, edge, m);
        }
    }

    @Override
    public float getWidthForEdge(UnwrapEdge edge) {
        return 1 == edge.getUseCount() % 2 ? width : 0;
    }

}
