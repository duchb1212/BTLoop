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
    private Ball ball;
    private ArrayList<Brick> bricks;
    private ArrayList<GameObject> powerUps;

    private int score;
    private int lives;
    private boolean gameOver;
    private boolean gameWon;
    private boolean paused;

    private int screenWidth;
    private int screenHeight;

    private static final int initialLives = 3;
    private static final int pointNormalBricks = 10;
    private static final int pointStrongBricks = 20;
    private static final double EPS = 1e-3;

    public GameEngine(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bricks = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        initGame();
    }

    // --- Getters / setters (kept similar naming) ---
    public Paddle getPaddle() {
        return paddle;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public ArrayList<Brick> getBricks() {
        return bricks;
    }

    public void setBricks(ArrayList<Brick> bricks) {
        this.bricks = bricks;
    }

    public ArrayList<GameObject> getPowerUps() {
        return powerUps;
    }

    public void setPowerUps(ArrayList<GameObject> powerUps) {
        this.powerUps = powerUps;
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
        this.ball = new Ball(ballX, ballY, ballSize, ballSize, ballSpeed, -1.0, -1.0, screenWidth, screenHeight);

        // Initialize variable.
        score = 0;
        lives = initialLives;
        gameOver = false;
        gameWon = false;
        paused = false;

        createBricks();
        powerUps.clear();
    }

    private void createBricks() {
        bricks.clear();

        int brickWidth = 60;
        int brickHeight = 20;
        int brickPadding = 5;
        int offSetX = 35;
        int offSetY = 50;

        int rows = 5;
        int cols = 10;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = offSetX + j * (brickWidth + brickPadding);
                int y = offSetY + i * (brickHeight + brickPadding);

                if (i == 0 || i == 1) {
                    // Strong brick typically with 3 HP
                    BrickFactory factory = new StrongBrickFactory();
                    bricks.add(factory.createBrick(x, y, brickWidth, brickHeight, screenWidth, screenHeight));
                } else {
                    // Normal brick typically with 1 HP
                    BrickFactory factory = new NormalBrickFactory();
                    bricks.add(factory.createBrick(x, y, brickWidth, brickHeight, screenWidth, screenHeight));
                }
            }
        }
    }

    /**
     * Main update called from the game loop.
     *
     * @param deltaTime seconds elapsed since last frame
     */
    public void update(double deltaTime) {
        if (gameOver || gameWon || paused) return;

        // Build the list of all objects for collision queries (Ball expects ArrayList<GameObject>)
        ArrayList<GameObject> allObjects = new ArrayList<>();

        // Add paddle and all bricks (bricks are Brick which extends GameObject)
        allObjects.add(paddle);
        allObjects.addAll(bricks);
        allObjects.addAll(powerUps);
        // Update paddle (it clamps itself inside update)
        paddle.update(deltaTime, allObjects);

        // Update ball using swept-AABB movement against allObjects
        if (!ball.isLaunched()) {
            // Ball is attached to paddle: snap it to paddle top and don't run full physics
            double ballX = paddle.getPosX() + (paddle.getWidth() - ball.getWidth()) / 2.0;
            double ballY = paddle.getPosY() - ball.getHeight();
            ball.setPosX(ballX);
            ball.setPosY(ballY);
            ball.setVelX(0.0);
            ball.setVelY(0.0);
        } else {
            // Ball in-flight: update physics with swept-AABB
            ball.update(deltaTime, allObjects);
        }
        for (Iterator<GameObject> it = powerUps.iterator(); it.hasNext(); ) {
            GameObject obj = it.next();
            // Assume PowerUpBall extends GameObject and has update, isMarkedForRemoval(), getPowerUpType()
            if (obj instanceof PowerUpBall pup) {
                pup.update(deltaTime, allObjects);

                // If collected by paddle
                if (paddle.intersects(pup)) {
                    // apply effect
                    ball.getPowerUps().put(pup.getPowerUpType(), 5.00000);
                    // mark / remove
                    it.remove();
                    continue;
                }

                // If flagged for removal (e.g. fell below screen)
                if (pup.isMarkedForRemoval()) {
                    it.remove();
                }
            } else {
                // If other dynamic objects, you can update them here
            }
        }
        CollisionResult c = ball.lastCollision;
        if (c != null && c.targetObject instanceof Brick) {
            Brick brick = (Brick) c.targetObject;
            if (brick.isDestroyed()) {
                if (brick instanceof NormalBrick) score += pointNormalBricks;
                else if (brick instanceof StrongBrick) score += pointStrongBricks;
                GameObject spawn = brick.onDestroyed();
                if (spawn != null) {
                    powerUps.add(spawn);
                }
            }
            // Clear to avoid double processing
            ball.lastCollision = null;
        }
        if (ball.checkWallCollision()) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                resetBall();
            }
        }
        // DISCRETE FALLBACK: paddle collision handling
        // DISCRETE FALLBACK: paddle collision handling (refactored, drop unused vars & fix small-bug)
        if (ball.intersects(paddle)) {
            if (ball.isLaunched()) {
                // compute overlap extents (only need overlap values)
                double overlapX = Math.max(0.0, Math.min(ball.getPosX() + ball.getWidth(), paddle.getPosX() + paddle.getWidth())
                        - Math.max(ball.getPosX(), paddle.getPosX()));
                double overlapY = Math.max(0.0, Math.min(ball.getPosY() + ball.getHeight(), paddle.getPosY() + paddle.getHeight())
                        - Math.max(ball.getPosY(), paddle.getPosY()));

                final double PUSH_OUT = 0.5;

                // Decide side vs top collision: side if horizontal penetration is significantly smaller
                if (overlapX > EPS && overlapX + EPS < overlapY) {
                    // Side collision: reverse X velocity
                    ball.setVelX(-ball.getVelX());
                    // push ball horizontally out of paddle
                    if (ball.centerX() < paddle.centerX()) {
                        ball.setPosX(paddle.getPosX() - ball.getWidth() - PUSH_OUT);
                    } else {
                        ball.setPosX(paddle.getPosX() + paddle.getWidth() + PUSH_OUT);
                    }
                } else {
                    // Top collision: bounce or snap above paddle
                    double ballVelY = ball.getVelY();
                    if (ballVelY > EPS) {
                        // compute hit position and avoid strictly vertical directions
                        double hitPos = (ball.centerX() - paddle.centerX()) / (paddle.getWidth() * 0.5);
                        hitPos = Math.max(-1.0, Math.min(1.0, hitPos));
                        double newDirX = (Math.abs(hitPos) < 0.1) ? Math.copySign(0.1, hitPos == 0 ? 1.0 : hitPos) : hitPos;
                        double speed = ball.getSpeed();
                        ball.setVelX(speed * newDirX);
                        ball.setVelY(-Math.abs(ball.getVelY()));
                        // push slightly above paddle to prevent re-overlap
                        ball.setPosY(paddle.getPosY() - ball.getHeight() - PUSH_OUT);
                    } else {
                        // edge cases: near-zero vertical velocity -> snap above paddle and carry paddle horizontal vel
                        ball.setPosY(paddle.getPosY() - ball.getHeight());
                        ball.setVelX(paddle.getVelX());
                        ball.setVelY(0.0);
                    }
                }
            }
            // if not launched, ball should already have been snapped above paddle earlier — nothing to do here
        }
        // Bricks check
        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;

            if (ball.intersects(brick)) {
                // Apply 1 damage by default
                boolean destroyed = brick.takeDamage(1);
                if (destroyed) {
                    GameObject spawn = brick.onDestroyed();
                    if (spawn != null) powerUps.add(spawn);
                }
                // Bounce ball (reverse Y) as a simple response (more sophisticated handling
                // would examine collision normal)
                ball.setVelY(-ball.getVelY());

                if (brick instanceof NormalBrick) {
                    score += pointNormalBricks;
                } else if (brick instanceof StrongBrick) {
                    score += pointStrongBricks;
                }

                // Stop after first hit this frame to avoid multi-hits in the same frame
                break;
            }
        }
        double ballBottom = ball.getPosY() + ball.getHeight();
        if (ballBottom >= screenHeight - EPS) {
            // If ball intersects paddle in same frame, treat as catch (we've already attempted bounce/snap above)
            if (ball.intersects(paddle)) {
                // If moving downward, ensure bounce; otherwise snap on top
                if (ball.getVelY() > EPS) {
                    ball.setVelY(-Math.abs(ball.getVelY()));
                    ball.setPosY(paddle.getPosY() - ball.getHeight() - 0.5);
                } else {
                    ball.setPosY(paddle.getPosY() - ball.getHeight());
                    ball.setVelX(paddle.getVelX());
                    ball.setVelY(0.0);
                }
            } else {
                // Missed: lose life
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                } else {
                    resetBall();
                }
            }
        }

        if (allBricksDestroyed()) {
            gameWon = true;
        }
    }

    public void resetBall() {
        int ballSize = (int) ball.getWidth();
        int ballX = (int) (paddle.getPosX() + (paddle.getWidth() / 2));
        int ballY =(int)  (paddle.getPosY() - paddle.getHeight());
        ball.reset(ballX, ballY);
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
}