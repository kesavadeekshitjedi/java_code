import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.ca.autosys.services.common.Tools;
import com.ca.autosys.services.request.cat2.GetIntCodesReq;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetIntCodesRsp;

public class GetAutOSysStatuses 
{
	public static void main(String[] args)
	{
		AsApi appServer = new AsApi("LUMOS",9000,AsConstants.ENCRYPTION_TYPE_DEFAULT);
		GetIntCodesReq intReq = new GetIntCodesReq();
		intReq.setRequest();
		try
		{
			ApiResponseSet rspSet = (ApiResponseSet)intReq.execute(appServer);
			while(rspSet.hasNext()) 
			{
	             System.out.println("----- Begin Response -----");
	             GetIntCodesRsp rsp = (GetIntCodesRsp)rspSet.next();
	             System.out.println("Intcode text:  " + rsp.getText());
	             System.out.println("Intcode type:  " + rsp.getType());
	             System.out.println("Intcode value: " + String.valueOf(rsp.getValue()));
	             System.out.println(Tools.getValueFromIntCode(appServer,rsp.getValue(),rsp.getType()));
	             System.out.println("-----  End Response  -----\n");
	         }

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
