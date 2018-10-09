import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { catchError, retry } from 'rxjs/operators';
import { Observable, Subject, ReplaySubject, from, of, range } from 'rxjs';
import { ProcedureFilter } from './procedure-heatmap/procedure-filter';
import { CellFilter } from './cell-heatmap/cell-filter';
import { environment } from '../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class HeatmapService {

  cellHeatmapResponse: Observable<HttpResponse<Response>>;//cache the normal response here os if filters are empty just return this without making a request to the rest api
  procedureBusy: boolean;
  cellBusy: boolean;
  restDataBaseUrl = 'http://localhost:8080/data';
  restBaseUrl= environment.restBaseUrl;

  constructs: string[];//all constructs available including the brackets
  constructTypes: string[];//for menu dropdown just conatains unique set with brackets part removed
  cells: string[];
  cellSubTypes: string[];
  assays: string[];

  cellHeatmapChart;
  procedureHeatmapChart;

  constructor(private http: HttpClient) { }

  getCellHeatmapResponse(filter: CellFilter):
      Observable<HttpResponse<Response>> {
      console.log('calling heatmap service method');
      if(this.cellHeatmapResponse!=undefined && filter.keyword==undefined && filter.construct==undefined && filter.cellType==undefined && filter.sort==undefined){//&& filter.cellSubType==undefined && filter.cellSubType== undefine
        console.log('using cached cellheatmap');
        return this.cellHeatmapResponse;
        
      }else{//if response not been made already or filters are not default then make a request to the api
      let filterString=this.getFilterString(filter);
      let urlstring=this.restBaseUrl +'/cell_heatmap'+filterString;
        this.cellHeatmapResponse= this.http.get<Response>(
          urlstring, { observe: 'response' });
          return this.cellHeatmapResponse;
        }
    }

    getDetailsPageResponse(gene: string, procedure: string, construct: string):
      Observable<HttpResponse<Response>> {
      console.log('calling heatmap service method getDetailsPageResponse');
      let filterString='gene='+gene;
      if(procedure)filterString+='&procedure='+procedure;
      if(construct)filterString+='&construct='+construct;
      let urlstring=this.restBaseUrl +'/procedure_page?'+filterString;
        return this.http.get<Response>(
          urlstring, { observe: 'response' });
    }

  getFilterString(filter: CellFilter) {
    let filterQuery = '';
    if (filter) {
      console.log('query button clicked with CellFilter search=' + filter.keyword + ' constructSeleted ' + filter.construct + ' cell selected=' + filter.cellType + ' cellSubtypeSelected=' + filter.cellSubType + 'sortField=' + filter.sort);
      filterQuery += '?';
      if (filter.sort) {
        filterQuery += '&sort=' + filter.sort;
      }
      if(filter.keyword){
        filterQuery+='&keyword='+filter.keyword;
      }
      if(filter.construct){
        filterQuery+='&construct='+filter.construct;
      }
      if(filter.cellType){
        filterQuery+='&cellType='+filter.cellType;
      }
      if(filter.cellSubType){
        filterQuery+='&cellSubType='+filter.cellSubType;
      }
      if(filter.assay){
        filterQuery+='&assay='+filter.assay;
      }
    }
    return filterQuery;
    }

    getProcedureFilterString(filter: ProcedureFilter) {
      let filterQuery = '';
      if (filter) {
        console.log('query button clicked with Procedure search=' + filter.keyword + ' constructSeleted ' + filter.construct + 'sortField=' + filter.sort);
        filterQuery += '?';
        if (filter.sort) {
          filterQuery += '&sort=' + filter.sort;
        }
        if(filter.keyword){
          filterQuery+='&keyword='+filter.keyword;
        }
        if(filter.construct){
          filterQuery+='&construct='+filter.construct;
        }
       
      }
      return filterQuery;
      }

    getProcedureHeatmapResponse(filter: ProcedureFilter):
      Observable<HttpResponse<Response>> {
      console.log('calling procedure heatmap service method');
      let filterString=this.getProcedureFilterString(filter);
      let urlstring=this.restBaseUrl +'/procedure_heatmap'+filterString;
      return this.http.get<Response>(
        urlstring, { observe: 'response' });
    }

    getCellTypeResponse():
      Observable<HttpResponse<Response>> {
      //console.log('calling heatmap service method');
      
      return this.http.get<Response>(
        this.restBaseUrl +'/cellTypes', { observe: 'response' });
    }

    getCellSubTypeResponse():
    Observable<HttpResponse<Response>> {
    //console.log('calling cellsubtype method');
    
    return this.http.get<Response>(
      this.restBaseUrl +'/cellSubTypes', { observe: 'response' });
  }

  getAssaysResponse():
    Observable<HttpResponse<Response>> {
    console.log('calling cellsubtype method');
    
    return this.http.get<Response>(
      this.restBaseUrl +'/assays', { observe: 'response' });
  }

  getConstructsResponse():
    Observable<HttpResponse<Response>> {
    console.log('calling constrcts method');
    
    return this.http.get<Response>(
      this.restBaseUrl +'/constructs', { observe: 'response' });
  }

  
  setCellBusy(){
    this.cellBusy=true;
  }
  setCellNotBusy(){
    this.cellBusy=false;
  }

  setProcedureBusy(){
    this.procedureBusy=false;
  }

  setProcedureNotBusy(){
    this.procedureBusy=false;
  }





//methods below for cell heatmap and some for both

  getConstructsDropdown(){
    //console.log('calling assay dropdown');
    //this.resourceLoaded=false;
    //if(this.data.length<=1){
    this.getConstructsResponse().subscribe(resp => {
      // display its headers
      var lResponse = { ... resp.body};
      //console.log('response='+JSON.stringify(resp));
      //this.data = this.response['response']['docs']
      //console.log('response from json file here: '+JSON.stringify(this.response['_embedded'].Data[0]['data']));
      
      this.constructTypes=lResponse['types'];
      //console.log("assays being returned="+this.assays);
      //let headerData=this.response['_embedded'].Data[0]['columnHeaders'];      
  });
  }


  getCellTypesDropdown(){
    this.getCellTypeResponse().subscribe(resp => {
      var lResponse = { ... resp.body};
      this.cells=lResponse['types'];
  });
}


getCellSubTypesDropdown(){
  //console.log('calling cellSubType dropdown');
  //this.resourceLoaded=false;
  //if(this.data.length<=1){
  this.getCellSubTypeResponse().subscribe(resp => {
    // display its headers
    var lResponse = { ... resp.body};
    this.cellSubTypes=lResponse['types']; 
});
}

getAssaysDropdown(){
  //console.log('calling assay dropdown');
  //this.resourceLoaded=false;
  //if(this.data.length<=1){
  this.getAssaysResponse().subscribe(resp => {
    // display its headers
    var lResponse = { ... resp.body};
    this.assays=lResponse['types'];
});
}




}
