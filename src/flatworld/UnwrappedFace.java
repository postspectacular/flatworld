package flatworld;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Matrix4x4;
import toxi.geom.Rect;
import toxi.geom.Triangle2D;
import toxi.geom.Vec2D;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WingedEdge;

public class UnwrappedFace {

    private static final Matrix4x4 matrix = new Matrix4x4();

    protected WEFace face;
    protected Triangle2D tri;
    protected Triangle2D collTri;
    protected Triangle2D origTri;

    public WingedEdge[] edges = new WingedEdge[3];

    public Vec2D sheetPos;

    public final int id;

    private UnwrapEdge edgeAB;
    private UnwrapEdge edgeBC;
    private UnwrapEdge edgeCA;

    private float area;

    private float minOffset = 2;

    public UnwrappedFace(Triangle2D t, int id, float scale, UnwrapEdge eab,
            UnwrapEdge ebc, UnwrapEdge eca) {
        this.origTri = t;
        this.id = id;
        this.edgeAB = eab;
        this.edgeBC = ebc;
        this.edgeCA = eca;
        area = origTri.getArea();
    }

    public void draw(PGraphics g, boolean showFaceLabels, boolean showEdgeLabels) {
        edgeAB.draw(g, this, tri.a, tri.b, showEdgeLabels);
        edgeBC.draw(g, this, tri.b, tri.c, showEdgeLabels);
        edgeCA.draw(g, this, tri.c, tri.a, showEdgeLabels);
        // drawCollisionTriangle(g);
        if (showFaceLabels) {
            g.fill(0);
            g.text(id, sheetPos.x, sheetPos.y);
        }
    }

    private void drawCollisionTriangle(PGraphics g) {
        g.stroke(255, 0, 0);
        g.noFill();
        g.beginShape(PConstants.TRIANGLES);
        g.vertex(collTri.a.x, collTri.a.y);
        g.vertex(collTri.b.x, collTri.b.y);
        g.vertex(collTri.c.x, collTri.c.y);
        g.endShape();
    }

    public float getArea() {
        return area;
    }

    public Rect getBounds() {
        return collTri.getBounds();
    }

    public boolean intersectsFace(UnwrappedFace f) {
        return collTri.intersectsTriangle(f.collTri);
    }

    public void setPosition(final Vec2D pos, final float theta) {
        sheetPos = pos;
        Vec2D sa = origTri.a.getRotated(theta).addSelf(pos);
        Vec2D sb = origTri.b.getRotated(theta).addSelf(pos);
        Vec2D sc = origTri.c.getRotated(theta).addSelf(pos);
        tri = new Triangle2D(sa, sb, sc);
        collTri = tri.copy();
        // if (collTri.isClockwise()) {
        // collTri.flipVertexOrder();
        // }
        collTri.adjustTriangleSizeBy(edgeAB.getOffset() + minOffset,
                edgeBC.getOffset() + minOffset, edgeCA.getOffset() + minOffset);
    }
}
