package flatworld.test;

import java.util.Iterator;

import processing.core.PApplet;
import processing.pdf.PGraphicsPDF;
import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;
import toxi.math.conversion.UnitTranslator;
import toxi.util.DateUtils;
import flatworld.EdgeRenderStrategy;
import flatworld.GlueTabEdgeStrategy;
import flatworld.HoleMountStrategy;
import flatworld.PackedLayout;
import flatworld.UnwrapSheet;
import flatworld.Unwrapper;

public class UnwrapTest extends PApplet {

    public static void main(String[] args) {
        PApplet.main(new String[] { "flatworld.test.UnwrapTest" });
    }

    private Unwrapper unwrap;
    private int currSheetID;
    private EdgeRenderStrategy edgeStrategy;
    private boolean useLabels;

    private int WIDTH = (int) UnitTranslator.millisToPoints(270);
    private int HEIGHT = (int) UnitTranslator.millisToPoints(190);
    private int TAB_WIDTH = (int) UnitTranslator.millisToPoints(6);

    private String pdfPath;
    private PGraphicsPDF pdf;

    public void draw() {
        if (pdfPath != null) {
            pdf = (PGraphicsPDF) beginRecord(PDF, pdfPath);
            pdf.textMode(SHAPE);
            pdf.textFont(createFont("Arial", 9));
            pdf.textAlign(CENTER);
        }
        background(255);
        if (pdfPath != null) {
            for (Iterator<UnwrapSheet> i = unwrap.getSheets().iterator(); i
                    .hasNext();) {
                i.next().draw(pdf, true, useLabels);
                if (i.hasNext()) {
                    pdf.nextPage();
                }
            }
            endRecord();
            pdfPath = null;
            println("done...");
        } else {
            unwrap.getSheets().get(currSheetID).draw(g, true, useLabels);
        }
    }

    public void keyPressed() {
        if (key == '[' && currSheetID > 0) {
            currSheetID--;
        }
        if (key == ']' && currSheetID < unwrap.getSheets().size() - 1) {
            currSheetID++;
        }
        if (key == 'r') {
            reset();
        }
        if (key == 'l') {
            useLabels = !useLabels;
        }
        if (key == 's') {
            pdfPath = "flatworld-" + DateUtils.timeStamp() + ".pdf";
        }
    }

    private void reset() {
        WETriangleMesh mesh;
        // mesh = new WETriangleMesh().addMesh(new Plane().toMesh(200));
        mesh = new WETriangleMesh().addMesh(new Sphere(200).toMesh(5));
        // mesh = new WETriangleMesh().addMesh(new AABB(100).toMesh());
        println(mesh);
        unwrap = new Unwrapper(width, height);
        unwrap.setLayout(new PackedLayout());
        unwrap.setEdgeStrategy(edgeStrategy);
        unwrap.unwrapMesh(mesh, 1);
        currSheetID = 0;
    }

    public void setup() {
        size(WIDTH, HEIGHT);
        smooth();
        textFont(createFont("SansSerif", 9));
        textAlign(CENTER);
        edgeStrategy = new GlueTabEdgeStrategy(TAB_WIDTH, 0.1f);
        edgeStrategy = new HoleMountStrategy(2, 8, 40);
        edgeStrategy.setLabelInset(20);
        reset();
    }
}
