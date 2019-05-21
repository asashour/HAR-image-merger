package harimage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import de.sstoehr.harreader.model.HarContent;
import de.sstoehr.harreader.model.HarEntry;

class TilesParser {

    private Map<String, List<Tile>> tilesMap = new TreeMap<>(TilesParser::comparePrefix);

    private static final Pattern PATTERN = Pattern.compile("(.*)/(\\d+)/(\\d+)@(.*)");

    TilesParser(List<HarEntry> entries) throws IOException {
        var map = new HashMap<String, HarContent>();
        for (var e : entries) {
            var content = e.getResponse().getContent();
            if (content.getMimeType().startsWith("image/")) {
                map.put(e.getRequest().getUrl(), content);
            }
        }

        for (var url : new ArrayList<>(map.keySet())) {
            var m = PATTERN.matcher(url);
            if (m.matches()) {
                var prefix = m.group(1);
                var suffix = m.group(4);
                if (!tilesMap.containsKey(prefix)) {
                    tilesMap.put(prefix, getTiles(map, prefix, suffix));
                }
            }
        }
    }

    private List<Tile> getTiles(Map<String, HarContent> map, String prefix, String suffix) throws IOException {
        var list = new ArrayList<Tile>();
        for (var it = map.entrySet().iterator(); it.hasNext();) {
            var e = it.next();
            var m = PATTERN.matcher(e.getKey());
            if (m.matches()) {
                var pref = m.group(1);
                var suff = m.group(4);
                if (comparePrefix(pref, prefix) == 0 && suff.equals(suffix)) {
                    var x = Integer.valueOf(m.group(2));
                    var y = Integer.valueOf(m.group(3));
                    list.add(new Tile(x, y, toImage(e.getValue())));
                    it.remove();
                }
            }
        }
        return list;
    }

    private static int comparePrefix(String value, String other) {
        int length = value.length();
        if (length == other.length()) {
            for (int i = length - 1; i >= 0; i--) {
                var c1 = value.charAt(i);
                var c2 = other.charAt(i);
                if (c1 != c2) {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if ('a' > c1 || c1 > 'd' || 'a' > c2 || c2 > 'd') {
                        return c1 - c2;
                    }
                }
            }
            return 0;
        }
        return value.compareTo(other);
    }

    private static BufferedImage toImage(HarContent content) throws IOException {
        var encoding = content.getEncoding();
        BufferedImage image = null;
        if ("base64".equals(encoding)) {
            var arr = Base64.getDecoder().decode(content.getText());
            image = ImageIO.read(new ByteArrayInputStream(arr));
        }
        return image;

    }

    public Map<String, List<Tile>> getTilesMap() {
        return tilesMap;
    }
}
