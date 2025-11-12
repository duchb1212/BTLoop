import java.util.ArrayList;
import java.util.Iterator;

/**
 * GameEngine orchestrates game objects and high-level game state.
 * <p>
 * Notes on compatibility with the rest of your codebase:
 * - Uses ArrayList<GameObject> everywhere to match existing signatures.
 * - Bricks are instances of Brick (NormalBrick, StrongBrick, UnbreakableBrick).
 * - Ball.update(...) performs swept-AABB based movement; GameEngine still
 * manages scoring, lives, and simple discrete collision responses for bricks/paddle
 * (since Ball does not currently apply game logic like scoring or destroying bricks).
 */
public class GameEngine {
    private Paddle paddle;

    private ArrayList<Brick> bricks;
    private ArrayList<GameObject> buffs;
    private ArrayList<Ball> balls;

    private int score;
    private int lives;
    private boolean gameOver;
    private boolean gameWon;
    private boolean paused;
    private final int Max_Ball = 6;

    int brickWidth = 60;
    int brickHeight = 20;
    int brickPadding = 5;
    int offSetX = 35;
    int offSetY = 50;

    private int screenWidth;
    private int screenHeight;

    private LevelLoader levelLoader = new LevelLoader(brickWidth,brickHeight,brickPadding,offSetX,offSetY,screenWidth,screenHeight);
    private int currentLevel =1;

    private static final int initialLives = 3;
    private static final int pointNormalBricks = 10;
    private static final int pointStrongBricks = 20;
    private static final double EPS = 1e-3;
    private static final double PUSH_OUT = 0.5;
    private static final double LARGE_PUSH = 3.0;

    public GameEngine(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bricks = new ArrayList<>();
        this.buffs = new ArrayList<>();
        this.balls = new ArrayList<>();
        initGame();
    }

    // --- Getters / setters (kept similar naming) ---
    public Paddle getPaddle() {
        return paddle;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public ArrayList<Brick> getBricks() {
        return bricks;
    }

    public void setBricks(ArrayList<Brick> bricks) {
        this.bricks = bricks;
    }

    public ArrayList<GameObject> getPowerUps() {
        return buffs;
    }

    public void setPowerUps(ArrayList<GameObject> powerUps) {
        this.buffs = powerUps;
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public void setBalls(ArrayList<Ball> balls) {
        this.balls = balls;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    // --- Initialization ---

    private void initGame() {
        // Create Paddle.
        int paddleWidth = 100;
        int paddleHeight = 20;
        int paddleX = (screenWidth - paddleWidth) / 2;
        int paddleY = screenHeight - 50;
        double paddleSpeed = 400.0; // pixels per second (tweak as needed)

        // Paddle constructor in your code expects: (posX,posY,width,height,velX,velY,speed,screenX)
        // We pass initial velX=0, velY=0
        this.paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight, 0.0, 0.0, paddleSpeed, screenWidth);

        // Create Ball.
        int ballSize = 15;
        int ballX = (screenWidth - ballSize) / 2;
        int ballY = screenHeight / 2;
        double ballSpeed = 300.0; // pixels per second (tweak)

        // Ball constructor: (posX,posY,width,height,speed,dirX,dirY,screenWidth,screenHeight)
        balls.clear();
        Ball ball = new Ball(ballX, ballY, ballSize, ballSize, ballSpeed, -1.0, -1.0, screenWidth, screenHeight);
        ball.setLaunched(false);
        this.balls.add(ball);
        // Initialize variable.
        score = 0;
        lives = initialLives;
        gameOver = false;
        gameWon = false;
        paused = false;

        createBricks();
        buffs.clear();
    }

    private void createBricks() {
        bricks.clear();

        // Load level from XML file
        String levelPath = LevelLoader.getLevelName(currentLevel);
        ArrayList<Brick> loadedBricks = levelLoader.loadLevel(levelPath);

        if (loadedBricks != null) {
            bricks = loadedBricks;
        } else {
            System.err.println("Error loading level " + levelPath);
        }
    }

    /**
     * Main update called from the game loop.
     *
     * @param deltaTime seconds elapsed since last frame
     */
    public void update(double deltaTime) {
        if (gameOver || gameWon || paused) return;
        if (balls.getFirst().getBuffs().containsKey(Buff.BuffType.EnlargedPaddle_Ball)) {
            paddle.setWidth(200);
            paddle.enlarge();
        } else {
            paddle.setWidth(100);
            paddle.minimize();
        }
        // Update paddle (it clamps itself inside update)
        paddle.update(deltaTime, new ArrayList<>());
        for (Iterator<GameObject> it =  buffs.iterator(); it.hasNext(); ) {
            GameObject obj = it.next();
            // Assume PowerUpBall extends GameObject and has update, isMarkedForRemoval(), getPowerUpType()
            if (obj instanceof Buff pup) {
                pup.update(deltaTime, new ArrayList<>());

                // If collected by paddle
                if (paddle.intersects(pup)) {
                    // apply effect
                    applyBuff(pup.getBuffType());
                    // mark / remove
                    it.remove();
                    continue;
                }

                // If flagged for removal (e.g. fell below screen)
                if (pup.isMarkedForRemoval()) {
                    it.remove();
                }
            } else {
            }
        }

            // Update ball using swept-AABB movement against allObjects
        // Update all balls
        for (Iterator<Ball> it = balls.iterator(); it.hasNext(); ) {
            Ball ball = it.next();

            if (!ball.isLaunched()) {
                // Attached to paddle
                ball.setPosX(paddle.getPosX() + (paddle.getWidth() - ball.getWidth()) / 2.0);
                ball.setPosY(paddle.getPosY() - ball.getHeight());
                ball.setVelX(0.0);
                ball.setVelY(0.0);
                continue;
            }
            ArrayList<GameObject> allObjects = new ArrayList<>();
            allObjects.add(paddle);
            for (Brick brick : bricks) {
                if (!brick.isDestroyed()) allObjects.add(brick);
            }

            // Ball in-flight
            ball.update(deltaTime, allObjects);

            // Process swept collision with bricks
            CollisionResult c = ball.lastCollision;
            if (c != null && c.targetObject instanceof Brick brick) {
                processBrickHit(brick);
            }
            ball.lastCollision = null;

            // Paddle collision
            if (ball.intersects(paddle)) {
                handlePaddleCollisionArkanoid(ball, paddle);
            }

            // Brick discrete collision fallback
            if (ball.getBuffs().containsKey(Buff.BuffType.Fire_Ball)) {
                for (Brick brick : bricks) {
                    if (brick.isDestroyed()) continue;

                    if (ball.intersects(brick)) {
                        processBrickHit(brick);
                    }
                }
            } else {
                for (Brick brick : bricks) {
                    if (brick.isDestroyed()) continue;
                    if (ball.intersects(brick)) {
                        processBrickHit(brick);
                        ball.setVelY(-ball.getVelY());
                        break;
                    }
                }
            }

            // Check if ball fell below screen
            if (ball.checkWallCollision()) {
                it.remove();
            }
        }

        bricks.removeIf(Brick::isDestroyed);

        if (balls.isEmpty()) {
            lives--;
            if (lives <= 0) gameOver = true;
            else {
                resetBall();
            }
        }

        if (allBricksDestroyed()) {
            gameWon = true;
        }
    }

    private void processBrickHit(Brick brick) {

        if (brick.isDestroyed()) {
            return;
        }
        boolean destroyed = brick.takeDamage(1);
        if (destroyed) {
            GameObject spawn = brick.onDestroyed();
            if (spawn != null) {
                buffs.add(spawn);
            }
            SoundManager.playSoundEffect("src/sounds/Brick Sound.wav");
            score += (brick instanceof NormalBrick) ? pointNormalBricks : pointStrongBricks;
        }
    }

 private void handlePaddleCollisionArkanoid(Ball ball, Paddle paddle) {

        double overlapX = Math.max(0.0,
                Math.min(ball.getPosX() + ball.getWidth(), paddle.getPosX() + paddle.getWidth())
                        - Math.max(ball.getPosX(), paddle.getPosX()));
        double overlapY = Math.max(0.0,
                Math.min(ball.getPosY() + ball.getHeight(), paddle.getPosY() + paddle.getHeight())
                        - Math.max(ball.getPosY(), paddle.getPosY()));



        if (overlapX < overlapY && overlapX < 8) {

            ball.setVelX(-ball.getVelX());
            ball.setVelY(-Math.abs(ball.getVelY()));
            if (ball.centerX() < paddle.centerX()) {
                ball.setPosX(paddle.getPosX() - ball.getWidth() - LARGE_PUSH);
            } else {
                ball.setPosX(paddle.getPosX() + paddle.getWidth() + LARGE_PUSH);
            }

            ball.setPosY(paddle.getPosY() - ball.getHeight() - LARGE_PUSH);
            return;
        }


        double hitPos = (ball.centerX() - paddle.centerX()) / (paddle.getWidth() * 0.5);
        hitPos = Math.max(-1.0, Math.min(1.0, hitPos));


        double angleDegrees = 90.0 - (hitPos * 60.0);
        double angleRadians = Math.toRadians(angleDegrees);

        double speed = ball.getSpeed();
        double newVelX = speed * Math.cos(angleRadians);
        double newVelY = -speed * Math.sin(angleRadians);


        ball.setVelocity(newVelX, newVelY);
        ball.setPosY(paddle.getPosY() - ball.getHeight() - LARGE_PUSH);
    }

    public void applyBuff(Buff.BuffType type) {
        if (balls.isEmpty()) return;

        if (type == Buff.BuffType.Split_Ball) {
            ArrayList<Ball> newballs = new ArrayList<>(balls);
            for (Ball newball : newballs) {
                if (newball.isLaunched() && newballs.size() < Max_Ball - 1) {
                    splitBall(newball);
                }
            }
        } else if (type == Buff.BuffType.Heart_Ball) {
            lives ++;
        } else {
            balls.forEach(ball -> ball.getBuffs().put(type, 5.0));
        }
    }

    public void splitBall(Ball origin) {
        if (balls.size() >= Max_Ball - 1) return;
        double speed = origin.getSpeed();
        double angle = Math.atan2(origin.getVelY(), origin.getVelX()); // current angle
        // create two new directions +/- 25 degrees
        double a1 = angle + Math.toRadians(25);
        double a2 = angle - Math.toRadians(25);

        Ball b1 = origin.cloneAt(origin.getPosX(), origin.getPosY(), speed*Math.cos(a1), speed*Math.sin(a1));
        Ball b2 = origin.cloneAt(origin.getPosX(), origin.getPosY(), speed*Math.cos(a2), speed*Math.sin(a2));

        balls.add(b1);
        if (balls.size() < Max_Ball) balls.add(b2);
    }

    public void resetBall() {
        balls.clear();
        int ballSize = 15;
        int ballX = (int) (paddle.getPosX() + (paddle.getWidth() / 2));
        int ballY =(int)  (paddle.getPosY() - paddle.getHeight());
        Ball newball = new Ball(ballX, ballY, ballSize, ballSize, 300.0, -1.0, -1.0, screenWidth, screenHeight);
        newball.setLaunched(false);
        balls.add(newball);
    }

    private boolean allBricksDestroyed() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) return false;
        }
        return true;
    }

        public void restart() {
        initGame();
    }

    public void togglePaused() {
        paused = !paused;
    }
    public void nextLevel() {
        currentLevel++;
        String nextLevelPath = LevelLoader.getLevelName(currentLevel);
        if (!XMLHandler.exists(nextLevelPath)) {
            gameWon = true;
            System.out.println(" All levels completed!");
            return;
        }
        createBricks();
        resetBall();
    }
}
