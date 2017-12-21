package com.toupety.mapgen.mold;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MoldFactory {

	List<Mold> molds = new ArrayList<>();
	
	public MoldFactory() {
		
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get("./mold", "teste1", "mold.txt"));
			molds.add(new Mold(lines));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Mold getAny() {
		return this.molds.get(0);
	}
}
