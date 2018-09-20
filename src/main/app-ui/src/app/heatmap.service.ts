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

  restDataBaseUrl = 'http://localhost:8080/data';
  restBaseUrl= environment.restBaseUrl;
  constructor(private http: HttpClient) { }

  getCellHeatmapResponse(filter: CellFilter):
      Observable<HttpResponse<Response>> {
      console.log('calling heatmap service method');
      let filterString=this.getFilterString(filter);
      let urlstring=this.restBaseUrl +'cell_heatmap'+filterString;
      console.log('urlSTring='+urlstring);
        return this.http.get<Response>(
          urlstring, { observe: 'response' });
    }

    getDetailsPageResponse(gene: string, procedure: string, construct: string):
      Observable<HttpResponse<Response>> {
      console.log('calling heatmap service method getDetailsPageResponse');
      let filterString='gene='+gene;
      if(procedure)filterString+='&procedure='+procedure;
      if(construct)filterString+='&construct='+construct;
      let urlstring=this.restBaseUrl +'procedure_page?'+filterString;
      console.log('urlSTring='+urlstring);
        return this.http.get<Response>(
          urlstring, { observe: 'response' });
    }

  getFilterString(filter: CellFilter) {
    let filterQuery = '';
    if (filter) {
      console.log('query button clicked with search=' + filter.keyword + ' constructSeleted ' + filter.construct + ' cell selected=' + filter.cellType + ' cellSubtypeSelected=' + filter.cellSubType + 'sortField=' + filter.sort);
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

    getProcedureHeatmapResponse(filter: ProcedureFilter):
      Observable<HttpResponse<Response>> {
      console.log('calling procedure heatmap service method');
      
      return this.http.get<Response>(
        this.restBaseUrl +'procedure_heatmap', { observe: 'response' });
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

  
   

}
