package investment;
import java.util.*;
import java.util.stream.Collectors;

public class InvestmentTransaction {
	private String tradeDate = null;
	private String action = null;
	private String securityDesc = null;
	private String price = null;
	private String symbol = null;
	private String securityType = null;
	private String quantity = null;
	private String commission = null;
	private String fees = null;
	private String accruedInterest = null;
	private String amount = null;
	private String settlementDate = null;
	private String category = null;
	
	private String clean(String inStr){
		if (inStr == null) {
			return "";
		}
		return inStr.trim();
	}
	public String getTradeDate() {
		return clean(tradeDate);
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getMoneyAction() {
		return ACTION_MAP.get(getAction());
	}
	
	public String getAction() {
		String act = clean(action);
		if (act.startsWith("YOU BOUGHT")) {
			return "YOU BOUGHT";
		}
		if (act.startsWith("YOU SOLD")) {
			return "YOU SOLD";
		}
		int index = act.indexOf(" as of");
		if (index > 0) {
			return act.substring(0, index);
		}
		return act;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	public String getSecurityDesc() {
		return clean(securityDesc);
	}
	public void setSecurityDesc(String securityDesc) {
		this.securityDesc = securityDesc;
	}
	public String getPrice() {
		return clean(price);
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSymbol() {
		return clean(symbol);
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getSecurityType() {
		return clean(securityType);
	}
	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}
	public String getQuantity() {
		if (this.quantity == null || quantity.length() == 0) {
			return "0";
		}
		if (this.quantity.trim().startsWith("-")){
			return quantity.trim().substring(1);
		}
		return clean(quantity);
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getCommission() {
		return clean(commission);
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	public String getFees() {
		return clean(fees);
	}
	public void setFees(String fees) {
		this.fees = fees;
	}
	public String getAccruedInterest() {
		return clean(accruedInterest);
	}
	public void setAccruedInterest(String accruedInterest) {
		this.accruedInterest = accruedInterest;
	}
	public String getAmount() {
		return clean(amount);
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSettlementDate() {
		return clean(settlementDate);
	}
	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}
	
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
		ACTION_MAP.put("REDEMPTION PAYOUT", "Redeem CD/Bond");
		ACTION_MAP.put("INTEREST", "Interest");
		ACTION_MAP.put("INTEREST EARNED", "Interest");
	}
	
	static Set<String> IGNORABLE_ACTION = new HashSet<String>();
	static {
		IGNORABLE_ACTION.add("downloaded");
	}
	
	static Set<String> BANKING_ACTION = new HashSet<String>();
	static {
		BANKING_ACTION.add("INTEREST");
		BANKING_ACTION.add("DEPOSIT");
		BANKING_ACTION.add("TRANSFER");
		BANKING_ACTION.add("PURCHASE INTO CORE ACCOUNT");
		BANKING_ACTION.add("REDEMPTION FROM CORE ACCOUNT");
		BANKING_ACTION.add("JOURNALED");		
	}
	static HashMap<String, String> CATEGORY = new HashMap<String, String>();	
	static {
		CATEGORY.put("INTEREST", "Investment Income");
		CATEGORY.put("REINVESTMENT", "Investment Income");
		CATEGORY.put("DIVIDEND RECEIVED",  "Investment Income");
	}

	
	
	
	public boolean isIgnorable(){
	    if (this.action == null || this.amount == null) {
	    	return true;
	    }
	    return false;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(this.getTradeDate());
		sb.append(this.getMoneyAction());
		sb.append(this.symbol);
		sb.append(this.commission);
		sb.append(this.quantity);
		sb.append(this.price);
		sb.append(this.amount);
		return sb.toString();
	}
	
	public static boolean containsWordsArray(String inputString, Set<String> words) {
		List<String> found = words.stream()
				.filter(word ->  inputString.toLowerCase().indexOf(word.toLowerCase()) >= 0 )
				.collect(Collectors.toList()); 
	  
	    return !found.isEmpty();
	}
	
	public boolean isBankingAction() {
		return (containsWordsArray(this.getAction(), BANKING_ACTION)) 
				|| (this.getSecurityDesc().indexOf("CASH") >= 0)
				|| (this.symbol.equals("FDRXX"));
			
	}
	
	public String toQIFInvestmentString() {
		StringBuffer sb = new StringBuffer();
		sb.append("D");
		sb.append(this.getTradeDate());
		sb.append("\nM");
		sb.append(this.getAction());
		sb.append("\nT");
		sb.append(this.getAmount());
		sb.append("\nN");
		sb.append(this.getMoneyAction());
		sb.append("\nY");
		sb.append(this.getSecurityDesc());
		sb.append("\nI");
		sb.append(this.getPrice());
		sb.append("\nQ");
		sb.append(this.getQuantity());
		sb.append("\nO");
		sb.append(this.getCommission());
		sb.append("\nL");
		sb.append(this.getCategory());
		sb.append("\n^");
		return sb.toString();	
	}
		
	public String toQIFBankString() {
		StringBuffer sb = new StringBuffer();
		sb.append("D");
		sb.append(this.getTradeDate());
		sb.append("\nM");
		sb.append(this.getAction());
		sb.append("\nT");
		sb.append(this.getAmount());
		sb.append("\nP");
		sb.append("\nL");
		sb.append(CATEGORY.get(getAction()));
		sb.append("\n^");
		return sb.toString();
		
		
//	       // Standard fields
//        typemap.put("D", "date");
//        typemap.put("T", "amount");
//        typemap.put("P", "description");
//        typemap.put("C", "cleared"); 
//        typemap.put("N", "checknumber");
//        typemap.put("A", "address");
//        typemap.put("M", "memo");
//        typemap.put("L", "category");
//        typemap.put("F", "reimbursable");
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
	public String getCategory() {
		return clean(category);
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
