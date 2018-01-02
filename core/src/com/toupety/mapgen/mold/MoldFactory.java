package com.toupety.mapgen.mold;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Json;

public class MoldFactory {

	private static MoldFactory instance;
	List<Mold> molds = new ArrayList<>();
	Mold empty = null;
	RandomXS128 rand = new RandomXS128();
	
	public static MoldFactory get() {
		
		if(instance == null)
			instance = new MoldFactory();
		
		return instance;
	}
	
	private MoldFactory() {
		
		try {
			
			List<Path> listDirs = Files.list(Paths.get("./mold")).filter(dir -> {
				return dir.toFile().isDirectory();
			}).collect(Collectors.toList());
			
			
			listDirs.forEach(path -> {
				try {//Paths.get("./mold", "teste1", "mold.txt")
					List<String> lines = Files.readAllLines(path.resolve("mold.txt"));
					
					Json json = new Json();
					json.setIgnoreUnknownFields(true);
					MoldMeta meta = json.fromJson(MoldMeta.class, new FileReader(path.resolve("info.json").toFile()));
					
					Mold mold = new Mold(meta, lines);
					molds.add(mold);
					
					if(meta.name.equals("empty")) {
						empty = mold;
					}
				} catch(Exception ex) {
					throw new RuntimeException(ex);
				}
			});
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Mold getAny() {
		return this.molds.get(rand.nextInt(this.molds.size()));
	}
	
	public Mold getAny(Predicate<Mold> filter) {
		
		mudar logica do get any para ao inves de filtrar molds diretamente, filtrar grupos de molds por aberto (openleft etc)
		cada mold vai ter o seu percentual de chance de ser escolhido, posso criar isso de duas formas, o percentual pode ser aleatorio
		ou predefinido no arquivo
		
		List<Mold> filtered = this.molds.stream().filter(filter).collect(Collectors.toList());
		
		if(filtered.size() > 0)
			return filtered.get(rand.nextInt(filtered.size()));
		else 
			return null;
		
	}	
	
	public Mold getEmpty() {
		return empty;
	}
}
