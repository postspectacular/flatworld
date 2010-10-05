package flatworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import toxi.geom.Vec2D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.WingedEdge;
import toxi.math.MathUtils;

public class Unwrapper {

    protected int sheetWidth, sheetHeight;

    protected List<UnwrapSheet> sheets = new ArrayList<UnwrapSheet>();
    protected int[] usedEdges;

    protected EdgeRenderStrategy edgeStrategy;

    private int maxSearchIterations=100;

    public Unwrapper(int width, int height) {
        this.sheetWidth = width;
        this.sheetHeight = height;
    }

    public void unwrapMesh(WETriangleMesh mesh, float scale, EdgeRenderStrategy edgeStrategy) {
        this.edgeStrategy=edgeStrategy;
        sheets.clear();
        int id = 1;
        usedEdges = new int[mesh.edges.size()];
        for (Face f : mesh.getFaces()) {
            WEFace ff = (WEFace) f;
            UnwrapEdge eab = getEdgeFor(ff, ff.a, ff.b);
            UnwrapEdge ebc = getEdgeFor(ff, ff.b, ff.c);
            UnwrapEdge eca = getEdgeFor(ff, ff.c, ff.a);
            UnwrappedFace face =
                    new UnwrappedFace(ff, id++, scale, eab, ebc, eca);
            boolean isPlaced = attemptToAddFaceToSheet(face, sheets);
            if (!isPlaced) {
                UnwrapSheet sheet = new UnwrapSheet(sheetWidth, sheetHeight);
                attemptToAddFaceToSheet(face, Collections.singletonList(sheet));
                sheets.add(sheet);
            }
        }
    }

    private UnwrapEdge getEdgeFor(WEFace f, Vertex v, Vertex w) {
        UnwrapEdge edge = null;
        for (WingedEdge e : f.edges) {
            if ((e.a == v && e.b == w) || (e.a == w && e.b == v)) {
                if (e.faces.size() > 1) {
                    usedEdges[e.id]++;
                }
                edge = new UnwrapEdge(e, usedEdges[e.id],edgeStrategy);
                break;
            }
        }
        return edge;
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
}
