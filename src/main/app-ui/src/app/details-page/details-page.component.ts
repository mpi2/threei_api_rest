import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HeatmapService } from '../heatmap.service';
@Component({
  selector: 'threei-details-page',
  templateUrl: './details-page.component.html',
  styleUrls: ['./details-page.component.css']
})
export class DetailsPageComponent implements OnInit {
  
  rows: any;
  columnHeaders: any;
  response: Response;
  gene: string;
  procedure: string;
  construct: string;
  resourceLoaded ; boolean;
  
  constructor(private activatedRoute: ActivatedRoute, private heatmapService: HeatmapService) {
      this.activatedRoute.queryParams.subscribe(params => {
            this.procedure = params['procedure'];
            this.gene= params['gene'];
            console.log('procedure='+this.procedure+ ' gene='+this.gene); // Print the parameter to the console. 
        });
    }
   

  ngOnInit() {
    this.getDetailsPageData();
  }
  getDetailsPageData(){
    console.log('calling details page data method');
    this.resourceLoaded=false;
    //if(this.data.length<=1){
      this.heatmapService.getDetailsPageResponse(this.gene, this.procedure, this.construct).subscribe(resp => {
      // display its headers
      this.response = { ... resp.body};
      //console.log('response='+JSON.stringify(resp));
      //this.data = this.response['response']['docs']
      //console.log('response from json file here: '+JSON.stringify(this.response['_embedded'].Data[0]['data']));
      this.rows=this.response['rows'];
      this.columnHeaders=this.response['columnHeaders'];
    //   this.columnHeaders=[
    //     '<[routerLink]="" (click)="onGoToPage2()">Homozygous viability at P14</a>',
    //     "Homozygous Fertility",
    //     "Haematology",
    //     "Peripheral Blood Leukocytes",
    //     "Spleen",
    //     "Mesenteric Lymph Node",
    //     "Bone Marrow",
    //     "Ear Epidermis",
    //     "Anti-nuclear Antibodies",
    //     "Cytotoxic T Cell Function",
    //     "DSS Challenge",
    //     "Influenza",
    //     "Trichuris Challenge",
    //     "Salmonella Challenge"
    //     ];
     // this.rowHeaders=this.response['rowHeaders'];
      //here we need to add a whole column populated with the construct as java has no way of mixing strings and ints in an array
      //this.constructs=this.response['constructs'];
      //console.log('construct='+this.constructs+'||');
      //console.log('rowheaders='+this.rowHeaders+'||');
      this.displayTable();
    
      
    });
  }

  displayTable(): any {

    //console.log('rows='+this.rows);
    //console.log('columnHeaders='+this.columnHeaders);
  }

  getStyle(sig: number) {
    if(sig==0) {
      return "white";
    }
    if(sig==1) {
      return "grey";
    } 
    if(sig==2) {
      return "blue";
    }
    if(sig==3) {
      return "red";
    }
  }

}
