package uk.ac.ebi.threei.rest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.SignificanceType;
import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.procedure.ParameterRow;
import uk.ac.ebi.threei.rest.procedure.Result;
import uk.ac.ebi.threei.rest.procedure.SexType;
import uk.ac.ebi.threei.rest.procedure.ZygosityType;
import uk.ac.ebi.threei.rest.services.DetailsService;
/**
 * Controller to get the procedure information page that is linked to be the heatmap cells - gives back parameter objects for the procedure and gene combination 
 * @author jwarren
 *
 */
@RestController
public class ProcedureController {

	private DetailsService detailsService;
	private Object dataRepo;

	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/parameters")
	@ResponseBody
	public HttpEntity<Data> dataController(Model model, @RequestParam(value = "heatmapType", required = false, defaultValue="procedure") String heatmapType, @RequestParam(value = "keywords", required = false) String keyword,
			@RequestParam(value = "construct", required = false) String construct) {
		System.out.println("calling data controller with heatmapType"+ heatmapType+" keywords=" + keyword + " construct=" + construct);
		
		//should extract these into methods in a data service for unit testing purposes
		List<Data> dataList = dataRepo.findAll();
		Data data = new Data();
		if(heatmapType.equals("cell")){
			data=dataList.get(1);
		}else if(heatmapType.equals("procedure")){
			data=dataList.get(0);
		}
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}
	
	@RequestMapping("/procedure")
    public String linkPage(Model model,
                           @RequestParam("gene") String gene,
                           @RequestParam("construct") String construct,
                           @RequestParam("procedure") String procedure) {

        Set<ParameterDetails> parameters = detailsService.getParametersForGeneAndDisplayName(gene,construct, procedure);
        String accession=detailsService.getAccessionForGene(gene);
//        System.out.println("parameters.size="+parameters.size());
//        System.out.println("parameters="+parameters);

		Set<String> headers = generateUniqueColumnHeaders(parameters);
		// now we need to just keep the most signficant hits for each column header and
		// store these in a row object with blanks where non exist for table display and
		// column alignment

		List<ParameterRow> rows=new ArrayList<>();
		for (ParameterDetails param : parameters) {
			ParameterRow row=new ParameterRow(param.getName(), param.getParameterId());
			for (String header : headers) {
				boolean headerFound = false;
				List<Result> resultsForParam = param.getMostSignificantResults();
				for (Result result : resultsForParam) {
					if (header.equalsIgnoreCase(result.getHeaderKey())) {
						//System.out.println("headerkey found");
						headerFound = true;
						row.addResult(result);

					}
				}
				if(!headerFound) {
					Result blankResult=new Result();
					blankResult.setSignificant(SignificanceType.no_data);
					String[] sexNZyg=header.split(" ");
					blankResult.setSexType(SexType.getByDisplayName(sexNZyg[0]));
					blankResult.setZygosityType(ZygosityType.valueOf(sexNZyg[1]));
					row.addResult(blankResult);
				}
			}
			rows.add(row);
		}
        
        //this data now below should be enough to give back as a rest api to display the page in angular
		//instead of model attributes we should give this back as json which we can then use angular to display
        model.addAttribute("rows", rows);
        //System.out.println("headers="+headers);
        model.addAttribute("accession", accession);
        model.addAttribute("headers", headers);
        model.addAttribute("parameters", parameters);
        model.addAttribute("gene", gene);
        model.addAttribute("construct", construct);
        model.addAttribute("procedure", procedure);
        return "procedure";

    }
	
	private Set<String> generateUniqueColumnHeaders(Set<ParameterDetails> parameters) {
		Set<String> headers=new TreeSet<String>();
        for(ParameterDetails details:parameters){
        	//System.out.println("parameterDetails="+details);
        	Set<String> headerKeys=details.getHeaderKeysForParameter();
        	headers.addAll(headerKeys);
        }
		return headers;
	}

}
