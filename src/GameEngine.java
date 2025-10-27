import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

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
        this.bricks = new ArrayList<Brick>();
        InitGame();
    }

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

    public List<Brick> getBricks() {
        return bricks;
    }

    public void setBricks(List<Brick> bricks) {
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

    public void InitGame() {
        //Create Paddle.
        int paddleWidth = 100;
        int paddleHeight = 20;
        int paddleX = (screenWidth - paddleWidth) / 2;
        int paddleY = screenHeight - 50;
        double speed = 7.0;

        paddle = new Paddle(paddleX, paddleY
                , paddleWidth, paddleHeight, speed, screenWidth);

        //Create Ball.
        int ballSize = 15;
        int ballX = (screenWidth - ballSize) / 2;
        int ballY = screenHeight / 2;

        ball = new Ball(ballX, ballY, ballSize, ballSize, 4.0, -1, -1, screenWidth, screenHeight);

        //Initialize variable.
        score = 0;
        lives = initialLives;
        gameOver = false;
        gameWon = false;
        paused = false;

        CreateBrick();
    }

    public void CreateBrick() {
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
                    bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                } else {
                    bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                }
            }
        }
    }

    public void update() {
        if (gameOver || gameWon || paused) {
            return;
        }
        paddle.update();
        ball.update();
        if (ball.getPosY() + ball.getHeight() >= screenHeight) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                ResetBall();
            }
        }
        if(ball.CheckCollision(paddle)) {
            ball.Bounce(paddle);
        }
        for (Brick brick : bricks) {
            if(!brick.isDestroyed()&&ball.CheckCollision(brick)) {
                brick.TakeHit();
                ball.Bounce(brick);
                if(brick.getType().equals("Normal")) {
                    score+=pointNormalBricks;
                }
                else if(brick.getType().equals("Strong")) {
                    score+=pointStrongBricks;
                }
                break;
            }
            if (allBrickDestroyed()) {
                gameWon = true;
            }
        }
    }

    public void ResetBall() {
        int ballSize = ball.getWidth();
        int ballX = (screenWidth - ballSize) / 2;
        int ballY = screenHeight / 2;
        ball.Reset(ballX, ballY);
    }

    private boolean allBrickDestroyed() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public void Restart() {
        InitGame();
    }

    public void TogglePaused() {
        paused = !paused;
    }

}
