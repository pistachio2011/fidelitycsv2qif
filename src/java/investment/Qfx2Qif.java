package investment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Qfx2Qif {

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: " + Qfx2Qif.class.getSimpleName() + " inFile [outFile]");
            System.exit(1);
        }

        String inFileName = args[0];
        File inFile = new File(inFileName);
        if (!inFile.exists() || !inFile.canRead()) {
            System.out.println("Can't read from " + inFile.getAbsolutePath());
            System.exit(1);
        }
     
        Qfx2Qif app = new Qfx2Qif();

        app.createInvestmentQif(args[0], inFile.getName() + "-buy.qif", "INVBUY" );
        app.createInvestmentQif(args[0], inFile.getName() + "-sell.qif", "INVSELL" );
               
        
    }

    public int createBankQif(InputStream is, OutputStream os) throws IOException {
        Document doc = Jsoup.parse(is, StandardCharsets.US_ASCII.name(), "");

        Writer w = new OutputStreamWriter(os);
        w.write("!Type:Bank\n");

        int count = 0;
        for (Element el : doc.body().select("STMTTRN")) {
            // 20181128020000[-5:EST]
            String dtPosted =  el.select("DTPOSTED").first().childNode(0).outerHtml().replace("\n", "");
            String name =  el.select("NAME").text();
            String amount =  el.select("TRNAMT").first().childNode(0).outerHtml().replace("\n", "").trim();

            //System.out.println(dtPosted + " : " + name + " : " + amount);
            String year = dtPosted.substring(0, 4);
            String month = dtPosted.substring(4, 6);
            String day = dtPosted.substring(6, 8);

            w.write("D" + month + "/" + day + "/" + year + "\n");
            w.write("P" + name + "\n");
            w.write("T" + amount + "\n");
            w.write("^\n");

            count++;
        }
        w.flush();
        return count;
    }
    
    public int createInvestmentQif(String inFileName, String outFileName, String actionType) throws IOException {
    	 File inFile = new File(inFileName);
		InputStream is = new FileInputStream(inFile);
		File outFile = new File(outFileName);
		OutputStream os = new FileOutputStream(outFile);

        Document doc = Jsoup.parse(is, StandardCharsets.US_ASCII.name(), "");

        Writer w = new OutputStreamWriter(os);
        w.write("!Type:Invst\n");

//        <BUYMF>
//        	<INVBUY>
//        		<INVTRAN>
//        			<FITID>FU20PT58AC5320181217</FITID>
//        			<DTTRADE>20181217160000[-5:EST]</DTTRADE>
//        			<MEMO>The Hartford Equity Income R6</MEMO>
//        		</INVTRAN>
//        		<SECID>
//        			<UNIQUEID>41664R283</UNIQUEID>
//        			<UNIQUEIDTYPE>CUSIP</UNIQUEIDTYPE>
//        			</SECID>
//        		<UNITS>0.0150000000</UNITS>
//        		<UNITPRICE>17.4800000000</UNITPRICE>
//        		<COMMISSION>0.00</COMMISSION>
//        		<TOTAL>-0.2622</TOTAL>
//        		<SUBACCTSEC>CASH</SUBACCTSEC>
//        		<SUBACCTFUND>CASH</SUBACCTFUND>
//        		</INVBUY>
//        <BUYTYPE>BUY</BUYTYPE>
//        </BUYMF>
        int count = 0;
        for (Element el : doc.body().select(actionType)) {
        	Transaction trans = new Transaction();
        	
            // 20181128020000[-5:EST]
            String dtPosted =  el.select("INVTRAN").select("DTTRADE").text();
      
            //System.out.println(dtPosted + " : " + name + " : " + amount);
            String year = dtPosted.substring(0, 4);
            String month = dtPosted.substring(4, 6);
            String day = dtPosted.substring(6, 8);

            trans.setTradeDate(month + "/" + day + "/" + year );
            if ("INVBUY".equalsIgnoreCase(actionType)) {
            	trans.setAction("Buy");
            } else if ("INVSell".equalsIgnoreCase(actionType)) {
            	trans.setAction("Sell");
            } else {
            	System.out.println("Unknown action type: " + actionType);
            }
            trans.setAmount(el.select("TOTAL").text());
            trans.setSecurityDesc(el.select("INVTRAN").select("MEMO").text());
            trans.setPrice(el.select("UNITPRICE").text());
            trans.setQuantity(el.select("UNITS").text());
            trans.setCommission(el.select("COMMISSION").text());
            trans.setSymbol(el.select("COMMISSION").text());
            
            w.write(QifWriter.toQIFInvestmentString(trans));
            
            w.write("^\n");

            count++;
        }
        w.flush();
        is.close();
        os.close();
        System.out.println(count + " records written into " + outFile.getAbsolutePath());
        
        return count;
    }


}
