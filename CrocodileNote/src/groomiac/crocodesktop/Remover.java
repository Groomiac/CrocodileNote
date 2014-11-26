package groomiac.crocodesktop;

import java.io.File;

public class Remover {
	private File src;

	public Remover(File src) {
		this.src = src;
	}

	public void removeDir(){
		for (File f : src.listFiles()) {
			f.delete();
		}

		src.delete();
	}

}
