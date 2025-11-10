import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.*;

/**
 * GameSaveManager handles saving and loading game state for BTLoop.
 * Separates save/load logic from GameEngine for better organization.
 */
public class SaveManager {

    private static final String DEFAULT_SAVE_DIR = "saves/";

    /**
     * Save the entire game state to an XML file.
     *
     * @param filePath Path to save file (e.g., "saves/slot1.xml")
     * @param engine GameEngine instance to save
     * @return true if save was successful, false otherwise
     */
    public static boolean saveGame(String filePath, GameEngine engine) {
        try {
            // Create XML document
            Document doc = XMLHandler.createDocument("BTLoopSave");
            Element root = XMLHandler.getRootElement(doc);

            // Add metadata
            root.setAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
            root.setAttribute("version", "1.0");
            root.setAttribute("date", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

            // Save game stats
            saveGameStats(doc, root, engine);

            // Save paddle
            savePaddle(doc, root, engine.getPaddle());

            // Save balls
            saveBalls(doc, root, engine.getBalls());

            // Save bricks
            saveBricks(doc, root, engine.getBricks());

            // Save power-ups (if any are active)
            savePowerUps(doc, root, engine.getPowerUps());

            // Write to file
            boolean success = XMLHandler.write(filePath, doc);
            if (success) {
                System.out.println("Game saved to: " + filePath);
            }
            return success;

        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load game state from an XML file.
     *
     * @param filePath Path to save file
     * @param engine GameEngine instance to load into
     * @param screenWidth Screen width for recreating objects
     * @param screenHeight Screen height for recreating objects
     * @return true if load was successful, false otherwise
     */
    public static boolean loadGame(String filePath, GameEngine engine, int screenWidth, int screenHeight) {
        try {
            Document doc = XMLHandler.read(filePath);
            if (doc == null) {
                System.err.println("Could not read save file: " + filePath);
                return false;
            }

            Element root = XMLHandler.getRootElement(doc);

            // Check version compatibility
            String version = root.getAttribute("version");
            if (!version.equals("1.0")) {
                System.err.println("Incompatible save file version: " + version);
                return false;
            }

            // Load game stats
            loadGameStats(root, engine);

            // Load paddle
            Paddle paddle = loadPaddle(root, screenWidth);
            if (paddle != null) {
                engine.setPaddle(paddle);
            }

            // Load balls
            ArrayList<Ball> balls = loadBalls(root, screenWidth, screenHeight);
            engine.setBalls(balls);

            // Load bricks
            ArrayList<Brick> bricks = loadBricks(root, screenWidth, screenHeight);
            engine.setBricks(bricks);

            // Load power-ups
            ArrayList<GameObject> powerUps = loadPowerUps(root);
            engine.setPowerUps(powerUps);

            System.out.println("Game loaded from: " + filePath);
            return true;

        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Quick save to default slot.
     */
    public static boolean quickSave(GameEngine engine) {
        return saveGame(DEFAULT_SAVE_DIR + "quicksave.xml", engine);
    }

    /**
     * Quick load from default slot.
     */
    public static boolean quickLoad(GameEngine engine, int screenWidth, int screenHeight) {
        return loadGame(DEFAULT_SAVE_DIR + "quicksave.xml", engine, screenWidth, screenHeight);
    }

    /**
     * Save to a numbered slot (1-9).
     */
    public static boolean saveToSlot(int slot, GameEngine engine) {
        if (slot < 1 || slot > 9) {
            System.err.println("Invalid save slot: " + slot + ". Must be 1-9.");
            return false;
        }
        return saveGame(DEFAULT_SAVE_DIR + "slot" + slot + ".xml", engine);
    }

    /**
     * Load from a numbered slot (1-9).
     */
    public static boolean loadFromSlot(int slot, GameEngine engine, int screenWidth, int screenHeight) {
        if (slot < 1 || slot > 9) {
            System.err.println("Invalid save slot: " + slot + ". Must be 1-9.");
            return false;
        }
        return loadGame(DEFAULT_SAVE_DIR + "slot" + slot + ".xml", engine, screenWidth, screenHeight);
    }

    /**
     * Check if a save file exists.
     */
    public static boolean saveExists(String filePath) {
        return XMLHandler.exists(filePath);
    }

    /**
     * Delete a save file.
     */
    public static boolean deleteSave(String filePath) {
        return XMLHandler.delete(filePath);
    }

    /**
     * Get list of all save files in the default directory.
     */
    public static List<String> listSaveFiles() {
        List<String> saves = new ArrayList<>();
        java.io.File saveDir = new java.io.File(DEFAULT_SAVE_DIR);

        if (saveDir.exists() && saveDir.isDirectory()) {
            java.io.File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                for (java.io.File file : files) {
                    saves.add(file.getName());
                }
            }
        }

        return saves;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private static void saveGameStats(Document doc, Element root, GameEngine engine) {
        XMLHandler.addElement(doc, root, "score", String.valueOf(engine.getScore()));
        XMLHandler.addElement(doc, root, "lives", String.valueOf(engine.getLives()));
        XMLHandler.addElement(doc, root, "gameOver", String.valueOf(engine.isGameOver()));
        XMLHandler.addElement(doc, root, "gameWon", String.valueOf(engine.isGameWon()));
        XMLHandler.addElement(doc, root, "paused", String.valueOf(engine.isPaused()));
    }

    private static void loadGameStats(Element root, GameEngine engine) {
        engine.setScore(XMLHandler.getElementInt(root, "score", 0));
        engine.setLives(XMLHandler.getElementInt(root, "lives", 3));
        engine.setGameOver(XMLHandler.getElementBoolean(root, "gameOver", false));
        engine.setGameWon(XMLHandler.getElementBoolean(root, "gameWon", false));
        engine.setPaused(XMLHandler.getElementBoolean(root, "paused", false));
    }

    private static void savePaddle(Document doc, Element root, Paddle paddle) {
        Element paddleEl = doc.createElement("Paddle");
        XMLHandler.addElement(doc, paddleEl, "posX", String.valueOf(paddle.getPosX()));
        XMLHandler.addElement(doc, paddleEl, "posY", String.valueOf(paddle.getPosY()));
        XMLHandler.addElement(doc, paddleEl, "width", String.valueOf(paddle.getWidth()));
        XMLHandler.addElement(doc, paddleEl, "height", String.valueOf(paddle.getHeight()));
        XMLHandler.addElement(doc, paddleEl, "speed", String.valueOf(paddle.getSpeed()));
        root.appendChild(paddleEl);
    }

    private static Paddle loadPaddle(Element root, int screenWidth) {
        Element paddleEl = (Element) root.getElementsByTagName("Paddle").item(0);
        if (paddleEl == null) return null;

        double posX = XMLHandler.getElementDouble(paddleEl, "posX", 0);
        double posY = XMLHandler.getElementDouble(paddleEl, "posY", 0);
        double width = XMLHandler.getElementDouble(paddleEl, "width", 100);
        double height = XMLHandler.getElementDouble(paddleEl, "height", 20);
        double speed = XMLHandler.getElementDouble(paddleEl, "speed", 400);

        return new Paddle(posX, posY, width, height, 0, 0, speed, screenWidth);
    }

    private static void saveBalls(Document doc, Element root, ArrayList<Ball> balls) {
        Element ballsEl = doc.createElement("Balls");

        for (Ball ball : balls) {
            Map<String, String> ballAttrs = new HashMap<>();
            ballAttrs.put("posX", String.valueOf(ball.getPosX()));
            ballAttrs.put("posY", String.valueOf(ball.getPosY()));
            ballAttrs.put("width", String.valueOf(ball.getWidth()));
            ballAttrs.put("height", String.valueOf(ball.getHeight()));
            ballAttrs.put("speed", String.valueOf(ball.getSpeed()));
            ballAttrs.put("launched", String.valueOf(ball.isLaunched()));
            XMLHandler.addElementWithAttributes(doc, ballsEl, "Ball", ballAttrs);
        }

        root.appendChild(ballsEl);
    }

    private static ArrayList<Ball> loadBalls(Element root, int screenWidth, int screenHeight) {
        ArrayList<Ball> balls = new ArrayList<>();
        List<Element> ballElements = XMLHandler.getElements(root, "Ball");

        for (Element ballEl : ballElements) {
            double posX = XMLHandler.getAttributeDouble(ballEl, "posX", 0);
            double posY = XMLHandler.getAttributeDouble(ballEl, "posY", 0);
            double width = XMLHandler.getAttributeDouble(ballEl, "width", 10);
            double height = XMLHandler.getAttributeDouble(ballEl, "height", 10);
            double speed = XMLHandler.getAttributeDouble(ballEl, "speed", 300);
            boolean launched = Boolean.parseBoolean(XMLHandler.getAttribute(ballEl, "launched", "false"));

            Ball ball = new Ball(posX, posY, width, height, speed, 1, -1, screenWidth, screenHeight);
            ball.setLaunched(launched);
            balls.add(ball);
        }

        return balls;
    }

    private static void saveBricks(Document doc, Element root, ArrayList<Brick> bricks) {
        Element bricksEl = doc.createElement("Bricks");

        for (Brick brick : bricks) {
            // Only save bricks that are not destroyed
            if (!brick.isDestroyed()) {
                Map<String, String> brickAttrs = new HashMap<>();
                brickAttrs.put("type", brick instanceof StrongBrick ? "strong" : "normal");
                brickAttrs.put("posX", String.valueOf(brick.getPosX()));
                brickAttrs.put("posY", String.valueOf(brick.getPosY()));
                brickAttrs.put("width", String.valueOf(brick.getWidth()));
                brickAttrs.put("height", String.valueOf(brick.getHeight()));
                brickAttrs.put("hitPoints", String.valueOf(brick.getHitPoints()));
                XMLHandler.addElementWithAttributes(doc, bricksEl, "Brick", brickAttrs);
            }
        }

        root.appendChild(bricksEl);
    }

    private static ArrayList<Brick> loadBricks(Element root, int screenWidth, int screenHeight) {
        ArrayList<Brick> bricks = new ArrayList<>();
        List<Element> brickElements = XMLHandler.getElements(root, "Brick");

        for (Element brickEl : brickElements) {
            String type = XMLHandler.getAttribute(brickEl, "type", "normal");
            double posX = XMLHandler.getAttributeDouble(brickEl, "posX", 0);
            double posY = XMLHandler.getAttributeDouble(brickEl, "posY", 0);
            double width = XMLHandler.getAttributeDouble(brickEl, "width", 60);
            double height = XMLHandler.getAttributeDouble(brickEl, "height", 20);
            int hitPoints = XMLHandler.getAttributeInt(brickEl, "hitPoints", 1);

            Brick brick;
            if (type.equals("strong")) {
                brick = new StrongBrick(posX, posY, width, height, screenWidth, screenHeight, hitPoints);
            } else {
                brick = new NormalBrick(posX, posY, width, height, screenWidth, screenHeight, hitPoints);
            }
            bricks.add(brick);
        }

        return bricks;
    }

    private static void savePowerUps(Document doc, Element root, ArrayList<GameObject> powerUps) {
        Element powerUpsEl = doc.createElement("PowerUps");

        for (GameObject powerUp : powerUps) {
            if (powerUp instanceof Buff) {
                Buff pup = (Buff) powerUp;
                Map<String, String> attrs = new HashMap<>();
                attrs.put("type", pup.getBuffType().toString());
                attrs.put("posX", String.valueOf(pup.getPosX()));
                attrs.put("posY", String.valueOf(pup.getPosY()));
                attrs.put("width", String.valueOf(pup.getWidth()));
                attrs.put("height", String.valueOf(pup.getHeight()));
                XMLHandler.addElementWithAttributes(doc, powerUpsEl, "PowerUp", attrs);
            }
        }

        root.appendChild(powerUpsEl);
    }

    private static ArrayList<GameObject> loadPowerUps(Element root) {
        ArrayList<GameObject> powerUps = new ArrayList<>();
        // Power-ups are dynamic and may not need to be saved/loaded
        // This is a placeholder for future implementation
        return powerUps;
    }
}