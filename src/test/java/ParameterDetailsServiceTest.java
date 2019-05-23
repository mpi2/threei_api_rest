/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.ebi.threei.Application;
import uk.ac.ebi.threei.rest.CellHeatmapRow;
import uk.ac.ebi.threei.rest.controllers.Filter;
import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.services.CellHeatmapService;
import uk.ac.ebi.threei.rest.services.ParameterDetailsService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
@AutoConfigureMockMvc
public class ParameterDetailsServiceTest {

	@Autowired
	private ParameterDetailsService parameterDetailsServce;

	
	
	@Test
	public void filterByGene() throws Exception {

		String construct="tm1a";
		List<ParameterDetails> rows = parameterDetailsServce.getParameterDetailsByGene("Adal" );
		System.out.println("ParemeterDetails in test="+rows);
		assertTrue(rows.get(0).getConstruct().startsWith("tm1"));
		assertTrue(rows.get(0).getGene().startsWith("Ada"));
		assertTrue(rows.get(0).getParameterId()!=null);
		System.out.println("parameter id="+rows.get(0).getParameterId());
		assertTrue(rows.size()>0);
	}
	

	@Test
	public void filterByMultipleProperties() throws Exception {

		String construct="tm1a";
		List<ParameterDetails> rows = parameterDetailsServce.getParameterDetailsByGeneAndProcedureAndConstruct("Adal", "Homozygous Fertility", construct );
		System.out.println("ParemeterDetails in test="+rows);
		assertTrue(rows.get(0).getConstruct().startsWith("tm1"));
		assertTrue(rows.get(0).getGene().startsWith("Ada"));
		assertTrue(rows.get(0).getParameterId()!=null);
		System.out.println("parameter id="+rows.get(0).getParameterId());
		assertTrue(rows.size()>0);
	}
	
//	@Test
//	public void filterForSingleRow() throws Exception {
//
//		List<CellHeatmapRow> rows = parameterDetailsService.queryForSingleRow();
//		System.out.println("filtered rows in test="+rows);
//		assertTrue(rows.size()<10);
//		assertTrue(rows.size()==1);
//		assertTrue(rows.get(0).getGene().equals("Zranb1"));
//	}

	
}