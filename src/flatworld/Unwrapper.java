package flatworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import toxi.geom.Matrix4x4;
import toxi.geom.Quaternion;
import toxi.geom.Triangle2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.WingedEdge;
import toxi.math.MathUtils;

public class Unwrapper {

    protected int sheetWidth, sheetHeight;

    protected List<UnwrapSheet> sheets = new ArrayList<UnwrapSheet>();
    protected int[] usedEdges;

    protected EdgeRenderStrategy edgeStrategy;

    private int maxSearchIterations = 100;

    public Unwrapper(int width, int height) {
        this.sheetWidth = width;
        this.sheetHeight = height;
    }

    private boolean attemptToAddFaceToSheet(UnwrappedFace face,
            List<UnwrapSheet> sheets) {
        UnwrapSheet bestSheet = null;
        Vec2D bestPos = null;
        float bestTheta = 0;
        float minDist = Float.MAX_VALUE;
        face.setPosition(new Vec2D(), 0);
        float faceArea = face.getArea();
        boolean isPlaced = false;
        for (UnwrapSheet sheet : sheets) {
            if (sheet.getFreeArea() >= faceArea) {
                for (int i = 0; i < maxSearchIterations; i++) {
                    Vec2D pos = sheet.getRandomPos();
                    float theta = MathUtils.random(MathUtils.TWO_PI);
                    face.setPosition(pos, theta);
                    if (sheet.isFacePlacable(face)) {
                        float dist = getMinDistanceOnSheet(face, sheet);
                        if (dist <= minDist) {
                            bestSheet = sheet;
                            bestPos = pos;
                            bestTheta = theta;
                            minDist = dist;
                        }
                    }
                }
            }
        }
        if (bestSheet != null) {
            face.setPosition(bestPos, bestTheta);
            bestSheet.add(face);
            isPlaced = true;
        }
        return isPlaced;
    }

    private UnwrapEdge getEdgeFor(WEFace f, Vec3D v, Vec3D w) {
        UnwrapEdge edge = null;
        for (WingedEdge e : f.edges) {
            if ((e.a.equals(v) && e.b.equals(w))
                    || (e.a.equals(w) && e.b.equals(v))) {
                if (e.faces.size() > 1) {
                    usedEdges[e.id]++;
                }
                edge = new UnwrapEdge(e.id, usedEdges[e.id], edgeStrategy);
                break;
            }
        }
        return edge;
    }

    /**
     * @return the edgeStrategy
     */
    public EdgeRenderStrategy getEdgeStrategy() {
        return edgeStrategy;
    }

    /**
     * @return the maxSearchIterations
     */
    public int getMaxSearchIterations() {
        return maxSearchIterations;
    }

    private float getMinDistanceOnSheet(UnwrappedFace face, UnwrapSheet sheet) {
        float minDist = Float.MAX_VALUE;
        for (UnwrappedFace f : sheet.faces) {
            float d = face.sheetPos.distanceToSquared(f.sheetPos);
            if (d < minDist) {
                minDist = d;
            }
        }
        return minDist;
    }

    public List<UnwrapSheet> getSheets() {
        return sheets;
    }

    /**
     * @param edgeStrategy
     *            the edgeStrategy to set
     */
    public void setEdgeStrategy(EdgeRenderStrategy edgeStrategy) {
        this.edgeStrategy = edgeStrategy;
    }

    /**
     * @param maxSearchIterations
     *            the maxSearchIterations to set
     */
    public void setMaxSearchIterations(int maxSearchIterations) {
        this.maxSearchIterations = maxSearchIterations;
    }

    public void unwrapMesh(WETriangleMesh mesh, float scale) {
        sheets.clear();
        int id = 1;
        usedEdges = new int[mesh.edges.size()];
        Matrix4x4 matrix = new Matrix4x4();
        for (Face f : mesh.getFaces()) {
            WEFace ff = (WEFace) f;
            Quaternion.getAlignmentQuat(Vec3D.Z_AXIS, f.normal).toMatrix4x4(
                    matrix);
            matrix.scaleSelf(scale);
            Vec3D centroid = f.getCentroid();
            Vec3D fa = f.a;
            Vec3D fb = f.b;
            Vec3D fc = f.c;
            Vec2D aa = matrix.applyToSelf(fa.sub(centroid)).to2DXY();
            Vec2D bb = matrix.applyToSelf(fb.sub(centroid)).to2DXY();
            Vec2D cc = matrix.applyToSelf(fc.sub(centroid)).to2DXY();
            Triangle2D tri = new Triangle2D(aa, bb, cc);
            // 2d triangle needs to be anti-clockwise for area & intersection
            // tests
            if (tri.isClockwise()) {
                tri.flipVertexOrder();
                Vec3D t = fa;
                fa = fc;
                fc = t;
            }
            UnwrapEdge eab = getEdgeFor(ff, fa, fb);
            UnwrapEdge ebc = getEdgeFor(ff, fb, fc);
            UnwrapEdge eca = getEdgeFor(ff, fc, fa);
            UnwrappedFace face =
                    new UnwrappedFace(tri, id++, scale, eab, ebc, eca);
            boolean isPlaced = attemptToAddFaceToSheet(face, sheets);
            if (!isPlaced) {
                UnwrapSheet sheet = new UnwrapSheet(sheetWidth, sheetHeight);
                attemptToAddFaceToSheet(face, Collections.singletonList(sheet));
                sheets.add(sheet);
            }
        }
    }
}
