package harimage;

import java.awt.image.BufferedImage;

public class Tile {

    private final int x;
    private final int y;
    private final BufferedImage image;

    Tile(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BufferedImage getImage() {
        return image;
    }
}
