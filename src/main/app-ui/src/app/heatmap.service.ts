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

  procedureHeatmapResponse: any;
  cellHeatmapResponse: Observable<HttpResponse<Response>>;// cache the normal response here os if filters are empty 
  // just return this without making a request to the rest api
  procedureBusy: boolean;
  cellBusy: boolean;
  restDataBaseUrl = 'http://localhost:8080/data';
  restBaseUrl = environment.restBaseUrl;

  constructs: string[]; // all constructs available including the brackets
  constructTypes: string[]; // for menu dropdown just conatains unique set with brackets part removed
  cells: string[];
  cellSubTypes: string[];
  assays: string[];

  defaultCellHeatmapChart;
  defaultProcedureHeatmapChart;


  data: any[][] = [[]];
  headers: string[]; // http response headers
  columnHeaders: string[];
  rowHeaders: string[];

  constructor(private http: HttpClient) { }

  getCellHeatmapResponse(filter: CellFilter):
      Observable<HttpResponse<Response>> {
      console.log('calling heatmap service method');
      if (this.defaultCellHeatmapChart !== undefined && filter.keyword === undefined && filter.construct === undefined
        && filter.cellType === undefined && filter.cellSubType === undefined &&
        filter.cellSubType === undefined && filter.sort === undefined) {
        // && filter.cellSubType==undefined && filter.cellSubType== undefine
        console.log('using cached cellheatmapResponse');
        return this.cellHeatmapResponse;

      } else {// if response not been made already or filters are not default then make a request to the api
      const filterString = this.getFilterString(filter);
      const urlstring = this.restBaseUrl + '/cell_heatmap' + filterString;
        this.cellHeatmapResponse = this.http.get<Response>(
          urlstring, { observe: 'response' });
          return this.cellHeatmapResponse;
        }
    }

    getProcedureHeatmapResponse(filter: ProcedureFilter):
      Observable<HttpResponse<Response>> {
      console.log('calling procedure heatmap service method');
      if ( this.procedureHeatmapResponse !== undefined && filter === undefined ) {
        // && filter.cellSubType==undefined && filter.cellSubType== undefine
        console.log('using cached procedureheatmapResponse');
        return this.procedureHeatmapResponse;

      } else {
        const filterString = this.getProcedureFilterString(filter);
        const urlstring = this.restBaseUrl + '/procedure_heatmap' + filterString;
        this.procedureHeatmapResponse = this.http.get<Response>(
          urlstring, { observe: 'response' });
        return this.procedureHeatmapResponse;
      }
      }

    getDetailsPageResponse(gene: string, procedure: string, construct: string):
      Observable<HttpResponse<Response>> {
      console.log('calling heatmap service method getDetailsPageResponse');
      let filterString = 'gene=' + gene;
      if (procedure) { filterString += '&procedure=' + procedure; }
      if (construct) {filterString += '&construct=' + construct; }
      const urlstring = this.restBaseUrl + '/procedure_page?' + filterString;
        return this.http.get<Response>(
          urlstring, { observe: 'response' });
    }

  getFilterString(filter: CellFilter) {
    let filterQuery = '';
    if (filter) {
      console.log('query button clicked with CellFilter search=' + filter.keyword + ' constructSeleted ' + filter.construct +
      ' cell selected=' + filter.cellType + ' cellSubtypeSelected=' + filter.cellSubType + 'sortField=' + filter.sort);
      filterQuery += '?';
      if (filter.sort) {
        filterQuery += '&sort=' + filter.sort;
      }
      if (filter.keyword) {
        filterQuery += '&keyword=' + filter.keyword;
      }
      if (filter.construct) {
        filterQuery += '&construct=' + filter.construct;
      }
      if (filter.cellType) {
        filterQuery += '&cellType=' + filter.cellType;
      }
      if (filter.cellSubType) {
        filterQuery += '&cellSubType=' + filter.cellSubType;
      }
      if (filter.assay) {
        filterQuery += '&assay=' + filter.assay;
      }
    }
    return filterQuery;
    }

    getProcedureFilterString(filter: ProcedureFilter) {
      let filterQuery = '';
      if (filter) {
        console.log('query button clicked with Procedure search=' + filter.keyword + ' constructSeleted ' +
        filter.construct + 'sortField=' + filter.sort);
        filterQuery += '?';
        if (filter.sort) {
          filterQuery += '&sort=' + filter.sort;
        }
        if (filter.keyword) {
          filterQuery += '&keyword=' + filter.keyword;
        }
        if (filter.construct) {
          filterQuery += '&construct=' + filter.construct;
        }

      }
      return filterQuery;
      }

    getCellTypeResponse():
      Observable<HttpResponse<Response>> {
      // console.log('calling heatmap service method');

      return this.http.get<Response>(
        this.restBaseUrl + '/cellTypes', { observe: 'response' });
    }

    getCellSubTypeResponse():
    Observable<HttpResponse<Response>> {
    // console.log('calling cellsubtype method');

    return this.http.get<Response>(
      this.restBaseUrl + '/cellSubTypes', { observe: 'response' });
  }

  getAssaysResponse():
    Observable<HttpResponse<Response>> {
    console.log('calling cellsubtype method');

    return this.http.get<Response>(
      this.restBaseUrl + '/assays', { observe: 'response' });
  }

  getConstructsResponse():
    Observable<HttpResponse<Response>> {
    console.log('calling constrcts method');

    return this.http.get<Response>(
      this.restBaseUrl + '/constructs', { observe: 'response' });
  }


  setCellBusy() {
    this.cellBusy = true;
  }
  setCellNotBusy() {
    this.cellBusy = false;
  }

  setProcedureBusy() {
    this.procedureBusy = false;
  }

  setProcedureNotBusy() {
    this.procedureBusy = false;
  }


}
