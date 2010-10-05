package flatworld;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Matrix4x4;
import toxi.geom.Quaternion;
import toxi.geom.Rect;
import toxi.geom.Triangle2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WingedEdge;

public class UnwrappedFace {

    private static final Matrix4x4 matrix = new Matrix4x4();

    protected WEFace face;
    protected Triangle2D tri;
    protected Triangle2D collTri;

    public Vec2D a, b, c;

    public WingedEdge[] edges = new WingedEdge[3];

    public Vec2D sheetPos;

    public final int id;

    private UnwrapEdge edgeAB;
    private UnwrapEdge edgeBC;
    private UnwrapEdge edgeCA;

    public UnwrappedFace(WEFace f, int id, float scale, UnwrapEdge eab,
            UnwrapEdge ebc, UnwrapEdge eca) {
        this.id = id;
        this.edgeAB = eab;
        this.edgeBC = ebc;
        this.edgeCA = eca;
        Quaternion.getAlignmentQuat(Vec3D.Z_AXIS, f.normal).toMatrix4x4(matrix);
        Vec3D centroid = f.getCentroid();
        Vec3D aa = matrix.applyToSelf(f.a.sub(centroid));
        Vec3D bb = matrix.applyToSelf(f.b.sub(centroid));
        Vec3D cc = matrix.applyToSelf(f.c.sub(centroid));
        a = aa.to2DXY().scaleSelf(scale);
        b = bb.to2DXY().scaleSelf(scale);
        c = cc.to2DXY().scaleSelf(scale);
    }

    public void setPosition(final Vec2D pos, final float theta) {
        sheetPos = pos;
        Vec2D sa = a.getRotated(theta).addSelf(pos);
        Vec2D sb = b.getRotated(theta).addSelf(pos);
        Vec2D sc = c.getRotated(theta).addSelf(pos);
        tri = new Triangle2D(sa, sb, sc);
        if (tri.isClockwise()) {
            tri.flipVertexOrder();
        }
        collTri =
                tri.copy().adjustTriangleSizeBy(edgeAB.getOffset(),
                        edgeBC.getOffset(), edgeCA.getOffset());
    }

    public boolean intersectsFace(UnwrappedFace f) {
        return collTri.intersectsTriangle(f.collTri);
    }

    public float getArea() {
        return tri.getArea();
    }

    public Rect getBounds() {
        return tri.getBounds();
    }

    public void draw(PGraphics g, boolean useLabels) {
        edgeAB.draw(g, this, tri.a, tri.b, useLabels);
        edgeBC.draw(g, this, tri.b, tri.c, useLabels);
        edgeCA.draw(g, this, tri.c, tri.a, useLabels);
        // g.stroke(255, 0, 0);
        // g.noFill();
        // g.beginShape(PConstants.TRIANGLES);
        // g.vertex(collTri.a.x, collTri.a.y);
        // g.vertex(collTri.b.x, collTri.b.y);
        // g.vertex(collTri.c.x, collTri.c.y);
        // g.endShape();
        if (useLabels) {
            g.fill(0);
            g.text(id, sheetPos.x, sheetPos.y);
        }
    }
}
