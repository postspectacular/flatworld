package flatworld;

import processing.core.PGraphics;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;
import toxi.geom.Line2D;
import toxi.geom.Vec2D;

public abstract class EdgeRenderStrategy {

    protected float labelInset = 15;

    protected ReadonlyTColor labelColor = TColor.RED;
    protected ReadonlyTColor edgeColor = TColor.BLACK;

    public abstract void drawEdge(PGraphics g, UnwrappedFace face,
            UnwrapEdge edge, Vec2D a, Vec2D b, boolean useLabels);

    public void drawLabel(PGraphics g, UnwrappedFace face, UnwrapEdge edge,
            Vec2D a, Vec2D b) {
        Line2D l = new Line2D(a.copy(), b.copy());
        l.offsetAndGrowBy(-labelInset, 1, face.sheetPos);
        float theta = l.getDirection().heading();
        Vec2D pos = l.getMidPoint();
        g.fill(labelColor.toARGB());
        g.pushMatrix();
        g.translate(pos.x, pos.y);
        g.rotate(theta);
        g.text(edge.getID(), 0, 0);
        g.popMatrix();
    }

    /**
     * @return the edgeColor
     */
    public ReadonlyTColor getEdgeColor() {
        return edgeColor;
    }

    /**
     * @return the labelColor
     */
    public ReadonlyTColor getLabelColor() {
        return labelColor;
    }

    /**
     * @return the labelInset
     */
    public float getLabelInset() {
        return labelInset;
    }

    public abstract float getOffsetWidthForEdge(UnwrapEdge edge);

    /**
     * @param edgeColor
     *            the edgeColor to set
     */
    public void setEdgeColor(ReadonlyTColor edgeColor) {
        this.edgeColor = edgeColor;
    }

    /**
     * @param labelColor
     *            the labelColor to set
     */
    public void setLabelColor(ReadonlyTColor labelColor) {
        this.labelColor = labelColor;
    }

    /**
     * @param labelInset
     *            the labelInset to set
     */
    public void setLabelInset(float labelInset) {
        this.labelInset = labelInset;
    }

}
