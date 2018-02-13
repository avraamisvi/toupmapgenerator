package com.toupety.mapgen.chunkeditor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFileChooser;

import com.badlogic.gdx.utils.Json;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.Configuration.AreaDefinition;
import com.toupety.mapgen.Configuration.Brush;
import com.toupety.mapgen.Configuration.ElementDefinition;
import com.toupety.mapgen.Configuration.ItemDefinition;
import com.toupety.mapgen.chunkeditor.Chunk.Block;
import com.toupety.mapgen.mold.MoldBlock;

public class ProjectSaver {

	private static Path moldPath = null;
	private static Path infoPath = null;
	private static Path path = null;
	
	public static void save(Info info, Block grid[][]) {


		if (moldPath == null) {
			
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(Paths.get("./").toFile());
			fc.showSaveDialog(null);			
			File file = fc.getSelectedFile();

			if (file.getName() != null) {
				path = Paths.get(file.getAbsolutePath());

				try {

					if (!Files.exists(path)) {
						Files.createDirectory(path);
					}

					moldPath = path.resolve("mold.txt");
					infoPath = path.resolve("info.json");

					saveGrid(info, grid);

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else {
			try {
				saveGrid(info, grid);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static void saveGrid(Info info, Block grid[][]) throws IOException {
		
		info.name = path.getFileName().toString();

		StringBuilder builder = new StringBuilder();

		for (int j = 0; j < grid.length; j++) {
			for (int i = grid[0].length - 1; i >= 0; i--) {
				if(grid[i][j].filled != null) {
					builder.append(grid[i][j].filled.tile);
				} else {
					Brush sp = Configuration.brushes.get("empty");
					builder.append(sp.tile);
				}
			}
			builder.append("\n");
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter pretty = mapper.writerWithDefaultPrettyPrinter();
		
		Files.write(infoPath, pretty.writeValueAsBytes(info));
		Files.write(moldPath, builder.toString().getBytes());		
	}
	
	public static Info load(Block[][] grid) throws IOException {
		Info ret = null;
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(Paths.get("./").toFile());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.showOpenDialog(null);
		File file = fc.getSelectedFile();
		
		path = Paths.get(file.getAbsolutePath());
		moldPath = path.resolve("mold.txt");
		infoPath = path.resolve("info.json");
		
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		ret = json.fromJson(Info.class, new FileReader(infoPath.toFile()));
		
		List<String> lines = Files.readAllLines(moldPath);
		
		String line;
		for(int y = 0; y < grid[0].length; y++) {
			line = lines.get(y);
			for(int x = grid.length-1; x >= 0 ; x--) {
				grid[(grid.length-1) - x][y].filled = Configuration.brushesPerTile.get(line.charAt(x));
			}
		}
		
		return ret;
	}
	
	public static class Info {
		@JsonProperty
		String name;
		@JsonProperty
		List<ElementDefinition> elements;
		@JsonProperty
		List<ItemDefinition> items;
		@JsonProperty
		List<AreaDefinition> areas;		
		@JsonProperty
		List<String> tags;
		@JsonProperty
		int maxWidth = 30;
		@JsonProperty
		int maxHeigth = 30;
		@JsonProperty
		List<String> open;
//		"collision": []
	}
}
