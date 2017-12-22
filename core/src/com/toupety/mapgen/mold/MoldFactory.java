package com.toupety.mapgen.mold;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Json;

public class MoldFactory {

	List<Mold> molds = new ArrayList<>();
	
	public MoldFactory() {
		
		List<String> lines;
		
		try {
			lines = Files.readAllLines(Paths.get("./mold", "teste1", "mold.txt"));
			
			Json json = new Json();
			json.setIgnoreUnknownFields(true);
			MoldMeta meta = json.fromJson(MoldMeta.class, new FileReader(Paths.get("./mold", "teste1", "info.yaml").toFile()));
			meta.setData(lines);
			
			molds.add(new Mold(meta));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Mold getAny() {
		return this.molds.get(0);
	}
}
