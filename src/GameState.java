public class GameState {
    private GameEngine engine;

    public GameState(GameEngine engine) {
        this.engine = engine;
    }

    public GameEngine getEngine() {
        return engine;
    }

    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    public void save(String filePath) {
        SaveManager.saveGame(filePath, engine);
    }

    public boolean load(String filePath, int screenWidth, int screenHeight) {
        return SaveManager.loadGame(filePath, engine, screenWidth, screenHeight);
    }

    public boolean quickSave() {
        return SaveManager.quickSave(engine);
    }

    public boolean quickLoad(int screenWidth, int screenHeight) {
        return SaveManager.quickLoad(engine, screenWidth, screenHeight);
    }
}
