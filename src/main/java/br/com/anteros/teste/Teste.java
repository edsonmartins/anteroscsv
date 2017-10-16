package br.com.anteros.teste;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.csv.AnterosCSVWriter;

public class Teste {
	
	public static void main(String[] args) throws IOException {
		String csv = "/Users/edson/output2.csv";
		AnterosCSVWriter writer = new AnterosCSVWriter(new FileWriter(csv));

		List<String[]> data = new ArrayList<String[]>();
		data.add(new String[] {"India", "New Delhi"});
		data.add(new String[] {"United States", "Washington D.C"});
		data.add(new String[] {"Germany", "Berlin"});

		writer.writeAll(data);

		writer.close();
	}

}
