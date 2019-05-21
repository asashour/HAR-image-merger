package harimage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Arguments {

    @Parameter(names = {
            "-src" }, description = "Specifies the source file", order = 1, required = true, converter = PathConverter.class)
    private Path source;

    @Parameter(names = { "-dest" }, description = "Specifies the destination file", order = 2, required = true)
    private Path destination;

    @Parameter(names = { "-o", "-overwrite" }, description = "Should overwrite the destination file?", order = 3)
    private boolean overwrite;

    public Path getSource() {
        return source;
    }

    public Path getDestination() {
        return destination;
    }

    public static class PathConverter implements IStringConverter<Path> {

        private final String name;

        PathConverter(String name) {
            this.name = name;
        }

        @Override
        public Path convert(String value) {
            Path path = Paths.get(value);
            if (!Files.exists(path)) {
                throw new ParameterException("Parameter " + name + " is not a valid path (found " + value + ")");
            }
            return path;
        }
    }

    public boolean isOverwrite() {
        return overwrite;
    }
}
