package com.fujitsu.fnc.sdnfw.vidya.mula.restApi;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.fujitsu.fnc.sdnfw.vidya.mula.api.DataStoreService;
import com.fujitsu.fnc.sdnfw.vidya.mula.impl.DataStoreMgrImpl;

public class RestService 
{
	public RestService() {
		
	}
	
	public void startUp() {
		System.out.println("Starting Vidya Mula REST API Bean");
	}
	
	
	@Path("myName/")
	@Produces({MediaType.TEXT_PLAIN})
	@GET
	public String getMyName() {
		return "Vidya Mula REST API ";
	}
	
	@Path("populateTree/")
	@POST
	public void populateTree() {
		DataStoreMgrImpl.getInstance().constructNetworkTree();
	}
	
	@Path("printTree/")
	@GET
	public String printTree() {
		return DataStoreMgrImpl.getInstance().printNetworkTree();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("executeRequest/")
	@Produces({MediaType.APPLICATION_JSON })
	@Consumes({MediaType.APPLICATION_JSON})
	@POST
	public void executeRequest(RestInputRequest inputRequest)
	{
		BundleContext bundleCtxt = FrameworkUtil.getBundle(DataStoreService.class).getBundleContext();
		
		List<String> outputs = new ArrayList<String>();
		try {
			
			ServiceReference[] vidyaMulaSrvcs = bundleCtxt.getServiceReferences(DataStoreService.class.getName(),null);
			DataStoreService vidyaMulaService = (DataStoreService) bundleCtxt.getService(vidyaMulaSrvcs[0]);
			
			//Async Call
			List<String> inputs = new ArrayList<>();
			
			inputRequest.getInputCmds().forEach(input -> {inputs.add(input.getInputCategory()+":"+input.getInputStrategy());});
			outputs = vidyaMulaService.execute_AsyncRequest(inputRequest.getOperName(), inputs);
			outputs.forEach(output -> { System.out.println(output);} );
			
			//Sync Call
			List<String> inputs1 = new ArrayList<>();
			
			inputRequest.getInputCmds().forEach(input -> {inputs1.add(input.getInputCategory()+":"+input.getInputStrategy());});
			outputs = vidyaMulaService.execute_AsyncRequest(inputRequest.getOperName(), inputs1);
			outputs.forEach(output ->{System.out.println(output);});
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return;
	}
}