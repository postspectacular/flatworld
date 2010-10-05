package flatworld;

import java.util.List;

import toxi.geom.Vec2D;
import toxi.math.MathUtils;

public class PackedLayout implements UnwrapLayoutStrategy {

    protected float scanDelta;
    protected float scanBleed;
    protected float scanTheta;

    public PackedLayout() {
        this(20, 20, MathUtils.TWO_PI / 36);
    }

    public PackedLayout(float scanBleed, float scanDelta, float scanTheta) {
        this.scanBleed = scanBleed;
        this.scanDelta = scanDelta;
        this.scanTheta = scanTheta;
    }

    protected float getMinDistanceOnSheet(UnwrappedFace face, UnwrapSheet sheet) {
        float minDist = 0;
        for (UnwrappedFace f : sheet.faces) {
            minDist +=
                    f.collTri.getClosestPointTo(face.collTri.a)
                            .distanceToSquared(face.collTri.a);
            minDist +=
                    f.collTri.getClosestPointTo(face.collTri.b)
                            .distanceToSquared(face.collTri.b);
            minDist +=
                    f.collTri.getClosestPointTo(face.collTri.b)
                            .distanceToSquared(face.collTri.b);
        }
        return minDist;
    }

    public boolean placeFace(UnwrappedFace face, List<UnwrapSheet> sheets) {
        UnwrapSheet bestSheet = null;
        Vec2D bestPos = null;
        float bestTheta = 0;
        float minDist = Float.MAX_VALUE;
        face.setPosition(new Vec2D(), 0);
        float faceArea = face.getArea();
        boolean isPlaced = false;
        for (UnwrapSheet sheet : sheets) {
            float width = sheet.getBounds().width;
            float height = sheet.getBounds().height;
            if (sheet.getFreeArea() >= faceArea) {
                for (float x = scanBleed, y = scanBleed; y <= height
                        - scanBleed;) {
                    Vec2D pos = new Vec2D(x, y);
                    for (float theta = 0; theta < MathUtils.TWO_PI; theta +=
                            scanTheta) {
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
                    x += scanDelta;
                    if (x > width - scanBleed) {
                        x = scanBleed;
                        y += scanDelta;
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
}
