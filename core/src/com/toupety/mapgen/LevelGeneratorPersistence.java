package com.toupety.mapgen;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.toupety.mapgen.Configuration.AreaDefinition;
import com.toupety.mapgen.Configuration.ElementDefinition;
import com.toupety.mapgen.Configuration.ItemConfiguration;
import com.toupety.mapgen.Configuration.ItemDefinition;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.chunkeditor.ProjectSaver.Info;
import com.toupety.mapgen.room.RoomArea;
import com.toupety.mapgen.room.RoomElement;
import com.toupety.mapgen.room.RoomItem;

public class LevelGeneratorPersistence {

	private static boolean saved = false;
	
	public static void save(Level level) {

		if (!saved) {
			
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(Paths.get("./").toFile());
			fc.showSaveDialog(null);			
			File file = fc.getSelectedFile();

			if (file.getName() != null) {
				Path path = Paths.get(file.getAbsolutePath());

				try {

					if (!Files.exists(path)) {
						Files.createDirectory(path);
					}
					
					level.forEach(r -> saveRoom(r, path));

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private static void saveRoom(Room r, Path root) {
		
		try {
			Path roomPath = root.resolve("room_" + r.getIndex());
			
			Files.createDirectory(roomPath);
			
			
			Path mapPath = roomPath.resolve("map.txt");
			Path infoPath = roomPath.resolve("info.json");		
	
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter pretty = mapper.writerWithDefaultPrettyPrinter();
			
			Files.write(infoPath, pretty.writeValueAsBytes(r.generateRoomInfo()));					
			
			StringBuilder builder = new StringBuilder();
			
			for (int j = 0; j < r.getGrid().getH(); j++) {
				for (int i = 0; i < r.getGrid().getW(); i++) {
					RoomBlock b = r.getGrid().getAt(i, j).get();
					builder.append(b.getMetaInfo().getType());
				}
				builder.append("\n");
			}
			
			Files.write(mapPath, builder.toString().getBytes());
			
		} catch(Throwable ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public RoomInfo processRoomInfo(Room room) {
		RoomInfo ret = new RoomInfo();
		
		
		
		
		return ret ;
	}
	
	public static class RoomInfo {
		@JsonProperty
		public int index;
		@JsonProperty
		public int x;
		@JsonProperty
		public int y;
		@JsonProperty
		public List<RoomItem> items;
		@JsonProperty
		public List<RoomArea> areas;
		@JsonProperty
		public List<RoomElement> elements;	
		@JsonProperty
		public List<String> tags;
		@JsonProperty
		public int width;
		@JsonProperty
		public int heigth;
		@JsonProperty
		public String key;
	}	
}
