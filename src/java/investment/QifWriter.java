package investment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QifWriter {
	   static Map<String, String> ACTION_MAP = new HashMap<String, String>();	
		static {
			ACTION_MAP.put("YOU SOLD", "Sell");
			ACTION_MAP.put("IN LIEU OF FRX SHARE", "Sell");
			ACTION_MAP.put("REINVESTMENT", "ReinvDiv");
			ACTION_MAP.put("DIVIDEND RECEIVED", "Div");
			ACTION_MAP.put("YOU BOUGHT", "Buy");
			ACTION_MAP.put("SHORT-TERM CAP GAIN", "CGShort");
			ACTION_MAP.put("FOREIGN TAX PAID", "MiscExp");
			ACTION_MAP.put("FEE CHARGED", "MiscExp");
			ACTION_MAP.put("Buy", "Buy");
			ACTION_MAP.put("BUY", "Buy");
			ACTION_MAP.put("Sell", "Sell");
			ACTION_MAP.put("SELL", "Sell");	
			ACTION_MAP.put("Shares In", "ReinvDiv");
			ACTION_MAP.put("Add Shares", "ShrsIn");
			ACTION_MAP.put("REDEMPTION PAYOUT", "RtrnCap");
			ACTION_MAP.put("INTEREST", "IntInc");
		}
		
		static Set<String> IGNORABLE_ACTION = new HashSet<String>();
		static {
			IGNORABLE_ACTION.add("downloaded");
		}
		
		static Set<String> BANKING_ACTION = new HashSet<String>();
		static {
			BANKING_ACTION.add("Electronic Funds Transfer Received");
			BANKING_ACTION.add("DEPOSIT");
			BANKING_ACTION.add("TRANSFER");
			BANKING_ACTION.add("PURCHASE INTO CORE ACCOUNT");
			BANKING_ACTION.add("REDEMPTION FROM CORE ACCOUNT");
			BANKING_ACTION.add("JOURNALED");	
			BANKING_ACTION.add("DIRECT DEPOSIT");
			BANKING_ACTION.add("Interest");
			BANKING_ACTION.add("Fee Distribution");
			BANKING_ACTION.add("Deduction");
			BANKING_ACTION.add("Contribution");
			
			
		}
		static HashMap<String, String> CATEGORY = new HashMap<String, String>();	
		static {
			CATEGORY.put("INTEREST", "Investment Income : Interest");
			CATEGORY.put("REINVESTMENT", "Investment Income");
			CATEGORY.put("DIVIDEND RECEIVED",  "Investment Income : Dividend");
			CATEGORY.put("FEE CHARGED",  "Bank Charges : Fee Charged");
			CATEGORY.put("FOREIGN TAX PAID",  "Taxes : FOREIGN TAX PAID");
			CATEGORY.put("Fee Distribution", "Bank Charges : Fee Charged");
			CATEGORY.put("Interest", "Investment Income : Interest");
		}

		public static List<String> containsWordsArray(String inputString, Set<String> words) {
			List<String> found = words.stream()
					.filter(word ->  inputString.toLowerCase().indexOf(word.toLowerCase()) >= 0 )
					.collect(Collectors.toList()); 
		  
		    return found;
		}
		
		public static String toQIFInvestmentString(Transaction tx) {
			StringBuffer sb = new StringBuffer();
			sb.append("D");
			sb.append(tx.getTradeDate());
			sb.append("\nM");
			sb.append(tx.getAction());
			sb.append("\nT");
			sb.append(tx.getAmount());
			sb.append("\nN");
			sb.append(getMoneyAction(tx.getAction()));
			sb.append("\nY");
			sb.append(tx.getSecurityDesc());
			sb.append("\nI");
			sb.append(tx.getPrice());
			sb.append("\nQ");
			sb.append(tx.getQuantity());
			sb.append("\nO");
			sb.append(tx.getCommission());
			sb.append("\nL");
			sb.append(tx.getCategory());
			sb.append("\n^");
			return sb.toString();	
		}
			
		public static String toQIFBankString(Transaction tx) {
			StringBuffer sb = new StringBuffer();
			sb.append("D");
			sb.append(tx.getTradeDate());
			sb.append("\nM");
			sb.append(tx.getAction());
			sb.append("\nT");
			sb.append(tx.getAmount());
			sb.append("\nP");
			sb.append(tx.getSecurityDesc());
			sb.append("\nL");
			sb.append(CATEGORY.get(tx.getAction()));
			sb.append("\n^");
			return sb.toString();
			
			
//		       // Standard fields
//	        typemap.put("D", "date");
//	        typemap.put("T", "amount");
//	        typemap.put("P", "description");
//	        typemap.put("C", "cleared"); 
//	        typemap.put("N", "checknumber");
//	        typemap.put("A", "address");
//	        typemap.put("M", "memo");
//	        typemap.put("L", "category");
//	        typemap.put("F", "reimbursable");
//	        
	        /*
	         * Going to have to do subclasses for Splits 
	         * and Investments because many of the fields are 
	         * re-defined. I think QIFTransaction will wind
	         * up just being a base class, and all of this
	         * work moved to subclasses. 
	         * 
	         */
	        
	        
	        /*
	        // Split / Investment common fields
	        typemap.put("$", "amount");
	        
	        // Splits
	        typemap.put("S", "splitcategory");
	        typemap.put("E", "splitmemo");
	        typemap.put("%", "splitpercent");
	        
	        // Investment types
	        
	        typemap.put("N", "action"); // Buy, sell...
	        typemap.put("Y", "securityname"); 
	        typemap.put("I", "price");
	        typemap.put("Q", "quantity");
	        typemap.put("O", "commission");
	        */

		}

		public static String getMoneyAction(String action) {
			List<String> actions = containsWordsArray(action, ACTION_MAP.keySet());
			if (actions.size() >0) {
	            	return ACTION_MAP.get(actions.get(0));
	            }
	            else { 
	            	return action;
	            }
		}
			
		public static String getMoneyCategory(String action) {
			return CATEGORY.get(action);
		}
		
		public static boolean isBankingAction(String action) {
			return !(containsWordsArray(action, BANKING_ACTION)).isEmpty() ;		
		}

}
