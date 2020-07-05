package investment;

public class Transaction {
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
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = clean(tradeDate);
	}

	public String getAction() {
		return this.action;
	}
	
	public void setAction(String action) {
		this.action = clean(action);
	}
	public String getSecurityDesc() {
		return this.securityDesc;
	}
	public void setSecurityDesc(String securityDesc) {
		this.securityDesc = clean(securityDesc);
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = clean(price);
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = clean(symbol);
	}
	public String getSecurityType() {
		return securityType;
	}
	public void setSecurityType(String securityType) {
		this.securityType = clean(securityType);
	}
	public String getQuantity() {
		return this.quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
		if (quantity != null  && quantity.trim().startsWith("-")){
			this.quantity = quantity.trim().substring(1);
		} 
		this.quantity = clean(this.quantity);
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = clean(commission);
	}
	public String getFees() {
		return fees;
	}
	public void setFees(String fees) {
		this.fees = clean(fees);
	}
	public String getAccruedInterest() {
		return accruedInterest;
	}
	public void setAccruedInterest(String accruedInterest) {
		this.accruedInterest = clean(accruedInterest);
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
		if (amount != null  && amount.trim().startsWith("-")){
			this.amount = amount.trim().substring(1);
		}
		this.amount = clean(amount);
	}
	public String getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(String settlementDate) {
		this.settlementDate = clean(settlementDate);
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
		sb.append("|Action=" + this.getAction());
		sb.append("|Symbol=" + this.symbol);
		sb.append("|Commision=" + this.commission);
		sb.append("|Quantity=" + this.quantity);
		sb.append("|Price=" + this.price);
		sb.append("|Amount=" + this.amount);
		return sb.toString();
	}
	
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = clean(category);
	}
}
