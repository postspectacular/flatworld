package flatworld;

import java.util.List;

import toxi.geom.Vec2D;
import toxi.math.MathUtils;

public class DefaultLayout implements UnwrapLayoutStrategy {

    protected int maxSearchIterations;

    public DefaultLayout() {
        this(100);
    }

    public DefaultLayout(int maxSearchIterations) {
        this.maxSearchIterations = maxSearchIterations;
    }

    protected float getMinDistanceOnSheet(UnwrappedFace face, UnwrapSheet sheet) {
        float minDist = Float.MAX_VALUE;
        for (UnwrappedFace f : sheet.faces) {
            float d = face.sheetPos.distanceToSquared(f.sheetPos);
            if (d < minDist) {
                minDist = d;
            }
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
}
