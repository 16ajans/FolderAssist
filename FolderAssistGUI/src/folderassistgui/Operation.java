package folderassistgui;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Operation {

	final Path orig;
	final Path dir;
	final Path target;

	Operation(Path orig) {
		this.orig = orig;
		this.dir = Path.of(orig.toString().substring(0, orig.toString().lastIndexOf(".")));
		this.target = Paths.get(dir.toString(), orig.getFileName().toString());;
	}

	Path getOrig() {
		return orig;
	}

	Path getDir() {
		return dir;
	}

	Path getTarget() {
		return target;
	}
}
