package investment;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

public class FidelityCSV2QIF {
	private String inputCSV = null;
	private String outputQif = null;
	private String stagingCSV = null;
// Fidelity	Joint Account:
//	Run Date,Action,Symbol,Security Description,Security Type,Exchange Quantity,Exchange Currency,Quantity,Currency,Price,Exchange Rate,Commission,Fees,Accrued Interest,Amount,Settlement Date
// Fidelity IRA
//Run Date,Action,Symbol,Security Description,Security Type,Quantity,Price ($),Commission ($),Fees ($),Accrued Interest ($),Amount ($),Settlement Date


	
	static Map<String, String> HEADER_MAP = new HashMap<String, String>();
	static {
		HEADER_MAP.put("Run Date", "tradeDate");
		HEADER_MAP.put("Action", "action");
		HEADER_MAP.put("Security Description", "securityDesc");
//		HEADER_MAP.put("Price", "price");
//		HEADER_MAP.put("Price ($)", "price");
		HEADER_MAP.put("Symbol", "symbol");
		HEADER_MAP.put("Security Type", "securityType");
		HEADER_MAP.put("Quantity", "quantity");
		HEADER_MAP.put("Commission", "commission");
		HEADER_MAP.put("Commission ($)", "commission");
		HEADER_MAP.put("Fees", "fees");
		HEADER_MAP.put("Fees ($)", "fees");
		HEADER_MAP.put("Accrued Interest ($)", "accruedInterest");
		HEADER_MAP.put("Amount", "amount");
		HEADER_MAP.put("Amount ($)", "amount");
		HEADER_MAP.put("Settlement Date", "settlementDate");

		// BOA FSA
//		Requested Date,Processed Date,Description,Method,Amount,Available Cash Balance,Applied to Contribution Maximum,Check Number,Check Date,Date of Service,Merchant Name,Adjustment Reason,Consumer Note,Tax Year,Tax Detail
		
		HEADER_MAP.put("Processed Date", "tradeDate");
		HEADER_MAP.put("Description", "action");
		HEADER_MAP.put("Amount", "amount");
	}

	public void cleanCSVfile(String inputCSV, String stagingCSV) throws IOException {
		try (PrintWriter  writer = new PrintWriter(Files.newBufferedWriter(Paths.get(stagingCSV)))) {
			try (Stream<String> stream = Files.lines(Paths.get(inputCSV))) {
			    stream
			         .map(String::trim)
			         .filter(line -> line != null && line.length() > 8)
			         .filter(line-> Character.isDigit(line.charAt(0))||line.contains("Amount"))
			         .forEach(line -> 
			         { writer.println(line.replace(", ", ","));
			         	System.out.println(line + " >" + line.replace(",", "")+">"+ line.replace(",", "").trim().length());
			        	 
			         })
			         ;
			   
			    stream.close();
			}
			writer.flush();
		    writer.close();
		}
		
	}
	
	public FidelityCSV2QIF() {
	}

	public void createQIF(String inputCSVName) throws IOException {
		cleanCSVfile(inputCSVName, "staging.csv");
		CSVReader reader = new CSVReader(new FileReader("staging.csv"));
		List<Transaction> transactions = parseCSV(reader);
		
		PrintWriter pwBank = new PrintWriter(new FileWriter(new File(inputCSVName + ".bank.qif"),true), true);
		pwBank.println("!Type:Bank");
		
		PrintWriter pwInvest = new PrintWriter(new FileWriter(new File(inputCSVName + ".invest.qif"),true), true);
		pwInvest.println("!Type:Invst");
		
		int actualTransNo = 0;
		for (int i = 0; i < transactions.size(); i++) {
			Transaction t = transactions.get(i);
			System.out.println(t);
			if (!t.isIgnorable()) {
				if (QifWriter.isBankingAction(t.getAction())) {
					pwBank.println(QifWriter.toQIFBankString(t));
				} else {
					pwInvest.println(QifWriter.toQIFInvestmentString(t));
				}
					
				actualTransNo++;
			}
		}
		pwBank.close();
		pwInvest.close();
		System.out.println(actualTransNo + " transaction generated.");
	}
	



	public static void main(String[] args) throws IOException {
		FidelityCSV2QIF converter = new FidelityCSV2QIF();
		Files.list(new File(args[0]).toPath())
        .filter(path -> path.toString().endsWith("csv"))
        .forEach(path -> {
            System.out.println(path.toString());
            try {
				converter.createQIF(path.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
   		
 	}

	public static List<Transaction> parseCSV(CSVReader reader) {

		HeaderColumnNameTranslateMappingStrategy<Transaction> strat = new HeaderColumnNameTranslateMappingStrategy<Transaction>();
		strat.setType(Transaction.class);

		strat.setColumnMapping(HEADER_MAP);

		CsvToBean<Transaction> csv = new CsvToBean<Transaction>();

		List<Transaction> list = csv.parse(strat, reader);
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
