public class StrongBrickFactory extends BrickFactory {
    @Override
    public Brick createBrick(double posX, double posY, double width, double height, int screenWidth, int screenHeight) {
        return new StrongBrick(posX, posY, width, height, screenWidth ,screenHeight, 3);
    }
}
