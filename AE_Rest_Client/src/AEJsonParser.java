import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AEJsonParser {
	public JSONObject returnJsonObject(String inputString) throws JSONException
	{
		JSONObject myJsonObject = new JSONObject(inputString);
		//System.out.println(myJsonObject);
		return myJsonObject;
		
		
	}
	public List<String> parseAEJsonObject(JSONObject aejson) throws JSONException
	{
		
		List<String> aeObjectList = new ArrayList<String>();
		String boxName = aejson.getString("boxName");
		String jobName = aejson.getString("name");
		String jobType = aejson.getString("jobType");
		String runMachine = aejson.getString("machine");
		
		aeObjectList.add("box_name: "+boxName);
		aeObjectList.add("job_name: "+jobName);
		aeObjectList.add("job_type: "+jobType);
		aeObjectList.add("run_machine: "+runMachine);
		
		
		return aeObjectList;
	}

}
