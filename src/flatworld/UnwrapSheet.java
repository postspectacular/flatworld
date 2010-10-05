package flatworld;

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.math.MathUtils;

public class UnwrapSheet {

    protected Rect bounds;

    protected List<UnwrappedFace> faces = new ArrayList<UnwrappedFace>();

    protected float usedArea;

    protected float totalArea;
    protected float bleed = 10;

    protected float faceBleed = 20;

    public UnwrapSheet(int w, int h) {
        this.bounds = new Rect(bleed, bleed, w - 2 * bleed, h - 2 * bleed);
        this.totalArea = w * h;
    }

    public void add(UnwrappedFace face) {
        faces.add(face);
        usedArea += face.getArea();
    }

    public void draw(PGraphics g, boolean showFaceLabels, boolean showEdgeLabels) {
        for (UnwrappedFace f : faces) {
            f.draw(g, showFaceLabels, showEdgeLabels);
        }
    }

    public Rect getBounds() {
        return bounds;
    }

    public float getFillRatio() {
        return usedArea / totalArea;
    }

    public float getFreeArea() {
        return totalArea - usedArea;
    }

    public Vec2D getRandomPos() {
        return new Vec2D(MathUtils.random(bounds.width),
                MathUtils.random(bounds.height));
    }

    public boolean isFacePlacable(UnwrappedFace face) {
        Rect b = face.getBounds();
        if (bounds.containsPoint(b.getTopLeft())
                && bounds.containsPoint(b.getBottomRight())) {
            for (UnwrappedFace f : faces) {
                if (f.intersectsFace(face)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
