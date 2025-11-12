public class SuperBrickFactory extends BrickFactory {
    @Override
    public Brick createBrick(double posX, double posY, double width, double height, int screenWidth, int screenHeight) {
        return new SuperBrick(posX, posY, width, height, screenWidth, screenHeight, 10);
    }
}
