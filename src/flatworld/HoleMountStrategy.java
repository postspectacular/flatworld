package flatworld;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.color.NamedColor;
import toxi.color.ReadonlyTColor;
import toxi.geom.Line2D;
import toxi.geom.Vec2D;

public class HoleMountStrategy extends EdgeRenderStrategy {

    protected float radius;
    protected float inset;
    protected float distance;

    protected ReadonlyTColor holeColor = NamedColor.CYAN;

    public HoleMountStrategy(float radius, float inset, float distance) {
        this.radius = radius;
        this.inset = inset;
        this.distance = distance;
    }

    @Override
    public void drawEdge(PGraphics g, UnwrappedFace face, UnwrapEdge edge,
            Vec2D a, Vec2D b, boolean useLabels) {
        g.noFill();
        g.ellipseMode(PConstants.RADIUS);
        g.stroke(edgeColor.toARGB());
        g.line(a.x, a.y, b.x, b.y);
        Line2D l = new Line2D(a.copy(), b.copy());
        float len = l.getLength();
        float innerLen = (int) ((len - 2 * distance) / distance) * distance;
        l.offsetAndGrowBy(-inset, -(len - innerLen) / 2, face.sheetPos);
        Vec2D prev = null;
        float minDist = (distance * 0.5f);
        minDist *= minDist;
        g.stroke(holeColor.toARGB());
        for (Vec2D p : l.splitIntoSegments(null, distance, true)) {
            if (prev == null
                    || (prev != null && p.distanceToSquared(prev) > minDist)) {
                g.ellipse(p.x, p.y, radius, radius);
                prev = p;
            }
        }
        if (useLabels) {
            drawLabel(g, face, edge, a, b);
        }
    }

    /**
     * @return the holeColor
     */
    public ReadonlyTColor getHoleColor() {
        return holeColor;
    }

    @Override
    public float getOffsetWidthForEdge(UnwrapEdge edge) {
        return 0;
    }

    /**
     * @param holeColor
     *            the holeColor to set
     */
    public void setHoleColor(ReadonlyTColor holeColor) {
        this.holeColor = holeColor;
    }

}
