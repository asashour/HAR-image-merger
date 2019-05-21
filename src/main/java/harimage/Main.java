package harimage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import de.sstoehr.harreader.HarReader;
import de.sstoehr.harreader.HarReaderException;

public class Main {

    public static void main(String[] args) {
        var arguments = new Arguments();
        var commander = JCommander.newBuilder().addObject(arguments).build();
        try {
            commander.parse(args);
            if (Files.exists(arguments.getDestination()) && !arguments.isOverwrite()) {
                error(commander,
                        "Destination file " + arguments.getDestination() + " already exists, use -overwrite if needed");
            } else {
                var harReader = new HarReader();
                var har = harReader.readFromFile(arguments.getSource().toFile());
                var parser = new TilesParser(har.getLog().getEntries());
                var map = parser.getTilesMap();
                Files.createDirectories(arguments.getDestination());
                var i = 1;
                for (var e : map.entrySet()) {
                    save(e.getValue(), arguments.getDestination().resolve(i++ + ".png"));
                }
            }

        } catch (ParameterException e) {
            error(commander, e.getMessage());
        } catch (HarReaderException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void error(JCommander commander, String message) {
        System.err.println(message);
        commander.usage();
    }

    private static void save(List<Tile> tiles, Path path) throws IOException {
        var minX = tiles.stream().min(Comparator.comparing(Tile::getX)).get().getX();
        var minY = tiles.stream().min(Comparator.comparing(Tile::getY)).get().getY();
        var maxX = tiles.stream().max(Comparator.comparing(Tile::getX)).get().getX();
        var maxY = tiles.stream().max(Comparator.comparing(Tile::getY)).get().getY();
        var tileWidth = tiles.get(0).getImage().getWidth();
        var tileHeight = tiles.get(0).getImage().getHeight();
        var image = new BufferedImage(((maxX - minX) + 1) * tileWidth, ((maxY - minY) + 1) * tileHeight,
                BufferedImage.TYPE_INT_ARGB);
        var g = image.createGraphics();
        for (var tile : tiles) {
            g.drawImage(tile.getImage(), (tile.getX() - minX) * tileWidth, (tile.getY() - minY) * tileHeight, null);
        }
        ImageIO.write(image, "png", path.toFile());
    }
}
