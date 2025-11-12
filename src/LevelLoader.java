import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LevelLoader {
    private int brickWidth;
    private int brickHeight;
    private int brickPadding;
    private int offsetX;
    private int offsetY;

    private int screenWidth;
    private int screenHeight;


    public LevelLoader(int brickWidth, int brickHeight, int brickPadding, int offsetX, int offsetY, int screenWidth, int screenHeight) {
        this.brickWidth = brickWidth;
        this.brickHeight = brickHeight;
        this.brickPadding = brickPadding;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public ArrayList<Brick> loadLevel(String fileName) {
        Document doc = XMLHandler.read(fileName);
        if(doc == null) {
            System.err.println("Error reading file " + fileName);
            return null;
        }
        Element root = XMLHandler.getRootElement(doc);
        return loadLevelFromElement(root);
    }

    private ArrayList<Brick> loadLevelFromElement(Element levelElement) {
        ArrayList<Brick> bricks = new ArrayList<>();
        String levelName = XMLHandler.getAttribute(levelElement, "name", "Unknown");
        System.out.println("Loading level " + levelName);

        List<Element> rowElements = XMLHandler.getElements(levelElement, "Row");
        for (int rowIndex = 0; rowIndex < rowElements.size(); rowIndex++) {
            Element rowElement = rowElements.get(rowIndex);
            String rowData = rowElement.getTextContent().trim();
            for(int col =0 ; col < rowData.length() ; col++) {
                char brickType = rowData.charAt(col);
                if(brickType == '.') {
                    continue;
                }

                int x = offsetX + col * (brickWidth+brickPadding);
                int y  = offsetY + rowIndex* (brickHeight+brickPadding);

                Brick brick = CreateBrick(brickType, x, y);
                if(brick != null) {
                    bricks.add(brick);
                }
            }
        }
        return bricks;
    }

    private Brick CreateBrick(char brickType,int x,int y){
        BrickFactory factory = null;
        switch (brickType){
            case 'N' : factory = new NormalBrickFactory();
            break;
            case 'S': factory = new StrongBrickFactory();
            break;
            case 'U': factory = new UnbreakableBrickFactory();
            break;
            case 'I' : factory = new IceBrickFactory();
            break;
        }
        return factory.createBrick(x,y,brickWidth,brickHeight,screenWidth,screenHeight);
    }

   public static String getLevelDir(){
        return "src/levels/";
   }

   public static String getLevelName(int levelName){
        return getLevelDir()+"level"+levelName +".xml";
   }
}
