package com.rmt.ps;

import java.util.HashMap;
import java.util.Map;

public class PSAdapterParmeters 
{
	Map<String, String> psParameterMap = new HashMap<String, String>();
	static String UNSUPPORTED_FAIL="Not supported. Fails Job Conversion";
	
	public void PSAdapterParameters()
	{
		psParameterMap.put("sr", "ps_process_type: SQL Report");
		psParameterMap.put("su", "ps_process_type: SQR Report For WF Delivery");
		psParameterMap.put("sp", "ps_process_type: SQR Process");
		psParameterMap.put("ae", "ps_process_type: Application Engine");
		psParameterMap.put("cr", "ps_process_type: Crystal");
		psParameterMap.put("rb", "ps_process_type: nVision-Report");
		psParameterMap.put("da", "ps_process_type: Database Agent");
		psParameterMap.put("jb", "ps_process_type: PS Job");
		psParameterMap.put("cb", "ps_process_type: COBOL SQL");
		psParameterMap.put("pr", UNSUPPORTED_FAIL+"ps_process_type: pr");
		psParameterMap.put("is", UNSUPPORTED_FAIL+"ps_process_type: is");
		psParameterMap.put("cu", UNSUPPORTED_FAIL+"ps_process_type: cu");
		
		// output type parameters
		
		psParameterMap.put("File", "ps_dest_type: FILE");
		psParameterMap.put("FILE", "ps_dest_type: FILE");
		psParameterMap.put("F", "ps_dest_type: FILE");
		psParameterMap.put("f", "ps_dest_type: FILE");
		psParameterMap.put("2", "ps_dest_type: FILE");
		
		psParameterMap.put("3", "ps_dest_type: PRINTER");
		psParameterMap.put("Printer", "ps_dest_type: PRINTER");
		psParameterMap.put("P", "ps_dest_type: PRINTER");
		
		psParameterMap.put("5", "ps_dest_type: EMAIL");
		psParameterMap.put("Email", "ps_dest_type: EMAIL");
		psParameterMap.put("E", "ps_dest_type: EMAIL");
		
		psParameterMap.put("6", "ps_dest_type: WEB");
		psParameterMap.put("Web", "ps_dest_type: WEB");
		psParameterMap.put("W", "ps_dest_type: WEB");
		
		//output Format parameters
		psParameterMap.put("Any", "ps_dest_format: Any");
		psParameterMap.put("0", "ps_dest_format: 1");
		psParameterMap.put("None", "ps_dest_format: None");
		psParameterMap.put("1", "ps_dest_format: 2");
		psParameterMap.put("PDF", "ps_dest_format: PDF");
		psParameterMap.put("2", "ps_dest_format: 3");
		psParameterMap.put("CSV", "ps_dest_format: CSV");
		psParameterMap.put("3", "ps_dest_format: 4");
		psParameterMap.put("HP", "ps_dest_format: HP");
		psParameterMap.put("4", "ps_dest_format: 5");
		psParameterMap.put("HTM", "ps_dest_format: HTM");
		psParameterMap.put("5", "ps_dest_format: 6");
		psParameterMap.put("LP", "ps_dest_format: LP");
		psParameterMap.put("6", "ps_dest_format: 7");
		psParameterMap.put("WKS", "ps_dest_format: WKS");
		psParameterMap.put("7", "ps_dest_format: 8");
		psParameterMap.put("XLS", "ps_dest_format: XLS");
		psParameterMap.put("8", "ps_dest_format: 9");
		psParameterMap.put("DOC", "ps_dest_format: DOC");
		psParameterMap.put("9", "ps_dest_format: 10");
		psParameterMap.put("PS", "ps_dest_format: PS");
		psParameterMap.put("10", "ps_dest_format: 11");
		psParameterMap.put("RPT", "ps_dest_format: RPT");
		psParameterMap.put("11", "ps_dest_format: 12");
		psParameterMap.put("12", "ps_dest_format: 13");
		psParameterMap.put("RTF", "ps_dest_format: RTF");
		psParameterMap.put("13", "ps_dest_format: 14");
		psParameterMap.put("SPF", "ps_dest_format: SPF");
		psParameterMap.put("14", "ps_dest_format: 15");
		psParameterMap.put("TXT", "ps_dest_format: TXT");
		psParameterMap.put("15", "ps_dest_format: 16");
		psParameterMap.put("OTHER", "ps_dest_format: OTHER");
		psParameterMap.put("16", "ps_dest_format: 17");
		psParameterMap.put("DEFAULT", "ps_dest_format: DEFAULT");
		
		// -f Directory parameter maps to ps_output_dest for FILE and PRINTER and ps_email_address for EMAIL
		psParameterMap.put("-b", "ps_email_subject: ");
		psParameterMap.put("-g", "ps_email_text: ");
		psParameterMap.put("-b", "ps_email_log: ");
		psParameterMap.put("-c", "ps_dlist_users: ");
		psParameterMap.put("-c", "ps_dlist_roles: ");
	}

}
