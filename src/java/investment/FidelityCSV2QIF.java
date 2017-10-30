package investment;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import java.util.*;
import java.util.stream.Stream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FidelityCSV2QIF {
	private String inputCSV = null;
	private String outputQif = null;
	private String stagingCSV = null;
	
	static Map<String, String> HEADER_MAP = new HashMap<String, String>();
	static {
		HEADER_MAP.put("Run Date", "tradeDate");
		HEADER_MAP.put("Action", "action");
		HEADER_MAP.put("Security Description", "securityDesc");
		HEADER_MAP.put("Price ($)", "price");
		HEADER_MAP.put("Symbol", "symbol");
		HEADER_MAP.put("Security Type", "securityType");
		HEADER_MAP.put("Quantity", "quantity");
		HEADER_MAP.put("Commission ($)", "commission");
		HEADER_MAP.put("Fees ($)", "fees");
		HEADER_MAP.put("Accrued Interest ($)", "accruedInterest");
		HEADER_MAP.put("Amount", "amount");
		HEADER_MAP.put("Amount ($)", "amount");
		HEADER_MAP.put("Settlement Date", "settlementDate");
	}

	public void cleanCSVfile() throws IOException {
		try (PrintWriter  writer = new PrintWriter(Files.newBufferedWriter(Paths.get(stagingCSV)))) {
			try (Stream<String> stream = Files.lines(Paths.get(inputCSV))) {
			    stream
			         .map(String::trim)
			         .filter(line->!line.isEmpty())
			         .forEach(line -> writer.println(line))
			         ;
			   
			    stream.close();
			}
			writer.flush();
		    writer.close();
		}
		
	}
	
	public FidelityCSV2QIF(String inputCSVName) {
		super();
		inputCSV = inputCSVName;
		this.stagingCSV = this.inputCSV + ".staging.csv";
		this.outputQif = this.inputCSV + ".qif";
	}

	public void createQIF(String header) throws IOException {
		cleanCSVfile();
		CSVReader reader = new CSVReader(new FileReader(this.stagingCSV));
		List<InvestmentTransaction> transactions = parseCSV(reader);
		boolean needheader = true;
		File outputFile = new File(this.outputQif);
		if (outputFile.length() > 0) {
			needheader = false;
		}
		
		PrintWriter pw = new PrintWriter(new FileWriter(outputFile,true), true);
		if (needheader){
			pw.println(header);
		}
		int actualTransNo = 0;
		for (int i = 0; i < transactions.size(); i++) {
			InvestmentTransaction t = transactions.get(i);
			if (!t.isIgnorable()) {
				if ("FEE CHARGED".equals(t.getAction())) {
					t.setCategory("Bank Charges:Fee Charged");
				} else if ("FOREIGN TAX PAID".equals(t.getAction())) {
					t.setCategory("Taxes:FOREIGN TAX PAID");
				}
				pw.println(t.toQIFString());
				actualTransNo++;
			}
		}
		pw.close();
		System.out.println(actualTransNo + " transaction generated.");
	}
	



	public static void main(String[] args) throws IOException {
		FidelityCSV2QIF converter = new FidelityCSV2QIF(args[0]);
   		converter.createQIF("!Type:Invst");
	}

	public static List<InvestmentTransaction> parseCSV(CSVReader reader) {

		HeaderColumnNameTranslateMappingStrategy<InvestmentTransaction> strat = new HeaderColumnNameTranslateMappingStrategy<InvestmentTransaction>();
		strat.setType(InvestmentTransaction.class);

		strat.setColumnMapping(HEADER_MAP);

		CsvToBean<InvestmentTransaction> csv = new CsvToBean<InvestmentTransaction>();

		List<InvestmentTransaction> list = csv.parse(strat, reader);
		return list;
	}
	
	public String getInputCSV() {
		return inputCSV;
	}

	public void setInputCSV(String inputCSV) {
		this.inputCSV = inputCSV;
	}

	public String getOutputQif() {
		return outputQif;
	}

	public void setOutputQif(String outputQif) {
		this.outputQif = outputQif;
	}

	public String getStagingCSV() {
		return stagingCSV;
	}

	public void setStagingCSV(String stagingCSV) {
		this.stagingCSV = stagingCSV;
	}


}
