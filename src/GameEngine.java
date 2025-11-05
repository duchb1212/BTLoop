import java.util.ArrayList;

/**
 * GameEngine orchestrates game objects and high-level game state.
 * <p>
 * Notes on compatibility with the rest of your codebase:
 * - Uses ArrayList<GameObject> everywhere to match existing signatures.
 * - Bricks are instances of BrickFactory (NormalBrick, StrongBrick, UnbreakableBrick).
 * - Ball.update(...) performs swept-AABB based movement; GameEngine still
 * manages scoring, lives, and simple discrete collision responses for bricks/paddle
 * (since Ball does not currently apply game logic like scoring or destroying bricks).
 */
public class GameEngine {
    private Paddle paddle;
    private Ball ball;
    private ArrayList<BrickFactory> bricks;

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

    public GameEngine(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bricks = new ArrayList<>();
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

    public ArrayList<BrickFactory> getBricks() {
        return bricks;
    }

    public void setBricks(ArrayList<BrickFactory> bricks) {
        this.bricks = bricks;
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
                    bricks.add(new StrongBrick(x, y, brickWidth, brickHeight, 3));
                } else {
                    // Normal brick typically with 1 HP
                    bricks.add(new NormalBrick(x, y, brickWidth, brickHeight, 1));
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

        // Add paddle and all bricks (bricks are BrickFactory which extends GameObject)
        allObjects.add(paddle);
        allObjects.addAll(bricks);

        // Update paddle (it clamps itself inside update)
        paddle.update(deltaTime, allObjects);

        // Update ball using swept-AABB movement against allObjects
        ball.update(deltaTime, allObjects);
        CollisionResult c = ball.lastCollision;
        if (c != null && c.targetObject instanceof BrickFactory) {
            BrickFactory brick = (BrickFactory) c.targetObject;
            if (brick.isDestroyed()) {
                if (brick instanceof NormalBrick) score += pointNormalBricks;
                else if (brick instanceof StrongBrick) score += pointStrongBricks;
            }
            // Clear to avoid double processing
            ball.lastCollision = null;
        }

        // After ball movement we check:
        // 1) bottom of screen -> lose life
        if (ball.checkWallCollision()) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                resetBall();
            }
        }

        // 2) Discrete intersection checks to apply game effects:
        //    - If ball intersects a brick that is not destroyed -> apply damage and score
        //    - If ball intersects paddle -> bounce (simple discrete fallback)
        // Note: Ball.update already tries to handle continuous collisions; these checks are
        //       a simple fallback to ensure bricks get damaged and score updated.
        // Build minimal list of game objects to check (only alive bricks + paddle)
        // Paddle check:
        if (ball.intersects(paddle)) {

            // Simple bounce: reverse Y velocity and optionally tweak X based on hit position
            // Compute relative hit position (-1..1)
            double hitPos = ((ball.centerX()) - (paddle.centerX())) / (paddle.getWidth() * 0.5);

            // Clamp
            if (hitPos < -1.0) hitPos = -1.0;
            if (hitPos > 1.0) hitPos = 1.0;

            // Angle effect: adjust velX proportional to hitPos
            double newDirX = hitPos;

            // Ensure not zero to avoid purely vertical bounce
            if (Math.abs(newDirX) < 0.1) newDirX = Math.signum(newDirX) * 0.1;

            // Normalize direction and preserve ball speed
            double speed = ball.getSpeed();

            // newDirY should be upward
            double newDirY = -Math.abs(ball.getVelY()) / Math.abs(ball.getVelY()); // -1 or 1; we want -1
            if (Double.isNaN(newDirY) || Double.isInfinite(newDirY)) newDirY = -1.0;

            // Set directions and velocities
            ball.setVelX(speed * newDirX);
            ball.setVelY(-Math.abs(ball.getVelY())); // ensure upward
        }

        // Bricks check
        for (BrickFactory brick : bricks) {
            if (brick.isDestroyed()) continue;

            if (ball.intersects(brick)) {
                // Apply 1 damage by default
                boolean destroyed = brick.takeDamage(1);
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
        for (BrickFactory brick : bricks) {
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