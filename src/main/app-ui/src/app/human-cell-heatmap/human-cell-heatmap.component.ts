import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCard, MatButtonModule, MatButtonToggleChange,
  MatButtonToggleDefaultOptions, MatButtonToggleAppearance, MatProgressBar,
  MatRadioModule, MatSelectModule, MatTabChangeEvent } from '@angular/material';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

import * as Highcharts from 'highcharts/highcharts';
import * as HC_map from 'highcharts/modules/map';
import * as HC_exporting from 'highcharts/modules/exporting';
// import * as HC_CustomEvents from 'highcharts-custom-events';

import { HeatmapService } from '../heatmap.service';
import {HumanCellFilter} from './human-cell-filter';
import {HumanHeatmapService} from "../human-heatmap.service";

HC_map(Highcharts);
// require('../../js/worldmap')(Highcharts);

HC_exporting(Highcharts);
// HC_ce(Highcharts);

// stops undefined being thrown straight off
Highcharts.setOptions({
  series: [{
    data: [1, 2, 3]
  }]
});





@Component({
  selector: 'threei-human-cell-heatmap',
  templateUrl: './human-cell-heatmap.component.html',
  styleUrls: ['./human-cell-heatmap.component.css']
})
export class HumanCellHeatmapComponent implements OnInit {
  doAutosuggest = true;
  selectControl = new FormControl(true);
  showEmtpyResultMessage = false;
  cellHeatmapChart: { chart: { type: string; marginTop: number; marginBottom: number;
      plotBorderWidth: number; height: number; width: number; }; title: { text: string; };
    colorAxis: { dataClasses: { from: number; to: number; color: string; name: string; }[]; min: number; max: number; };
    legend: { align: string; layout: string; verticalAlign: string; backgroundColor: string; };
    xAxis: { opposite: boolean; categories: string[]; labels: { rotation: number; }; reserveSpace: boolean; };
    yAxis: ({ categories: string[]; title?: undefined; } | { categories: string[]; title: any; })[]
    tooltip: { useHTML: boolean; formatter: () => string; };
    plotOptions: { series: { events: { click: (e: any) => void; }; }; };
    series: { name: string; borderWidth: number; data: any[][]; dataLabels: { enabled: boolean; color: string; }; }[]; };


  sortFieldSelected: string;
  defaultSortField = 'γδ T cells';
  Highcharts = Highcharts;
  constructs: string[]; // all constructs available including the brackets
  cells: string[];
  cellSelected: string;
  cellSubTypes: string[];
  cellSubtypeSelected: string;
  assays: string[];
  assaySelected;
  readonly CELL_TYPE = 'cell';
  data: any[][] = [];
  headers: string[]; // http response headers
  columnHeaders: string[];
  rowHeaders: string[];
  geneSymbols: string[] = [];
  response: Response;
  chartTitle = 'Procedure Heatmap'; // for init - change through titleChange
  updatechart = false;
  sort: string;
  resourceLoaded = false;
  cellChart;
  cellChartOptions =  {

    chart: {
      type: 'heatmap',
      marginTop: 220,
      marginBottom: 80,
      plotBorderWidth: 1,
      height: 1000,
      width: 1100,
    },
    title: {
      text: ''
    },
    colorAxis: {

      dataClasses: [{
        from: 0,
        to: 1,
        color: '#ffffff',
        name: 'No Data'
      }, {
        from: 1,
        to: 2,
        color: '#808080',
        name: 'Not enough data'
      }, {
        from: 2,
        to: 3,
        color: '#0000ff',
        name: 'Not Significantly Different WT vs KO'
      }, {
        from: 3,
        to: 4,
        color: '#c4463a',
        name: 'Significantly Different WT vs KO'
      }
      ],
      min: 0,
      max: 4,
    },
    legend: {
      align: 'left',
      // layout: 'vertical',
      margin: 3,
      verticalAlign: 'top',
      backgroundColor: 'whitesmoke',
      itemStyle: {
        fontSize: '16px',
        // font: '20pt Trebuchet MS, Verdana, sans-serif',
        // color: '#A0A0A0'
      },

    },

    xAxis: {
      opposite: true,
      categories: this.columnHeaders,
      labels: {
        // useHTML: true,
        rotation: 90
      },
      reserveSpace: true,
    },

    yAxis:
    // [
      {
        categories: this.rowHeaders,
        title: 'gene'
      },
    // {
    //  linkedTo : 0,
    //   title: 'construct',
    //   lineWidth: 2,
    //   categories: this.constructs
    // }
    // ],

    tooltip: {
      // shadow: false,
      useHTML: true,
      formatter: function () {
        return '<b>' + this.series.xAxis.categories[this.point.x] + '</b><br/>' +
          this.series.colorAxis.dataClasses[this.point.dataClass].name + '</b><br>' +
          '<b>' + this.series.yAxis.categories[this.point.y] + '</b>';
      }
    },

    plotOptions: {
      series: {
        cursor: 'pointer',
        events: {
          click: function (e) {
            const gene = e.point.series.yAxis.categories[e.point.y];

            const procedure = e.point.series.xAxis.categories[e.point.x];

            const text = 'gene: ' + gene +
              ' Procedure: ' + procedure + ' significance=' + e.point.value;

            // may have to use routerLink like for menus to link to our new not created yet parameter page
            const routerLink = 'details?' + 'procedure=' + procedure + '&gene=' + gene;
            window.open(routerLink, '_blank');
          }
        },
      },
    },

    series: [{
      name: 'Cell types with significant parameters',
      borderWidth: 1,
      // data: this.data,
      data: this.data,
      dataLabels: {
        enabled: false,
        color: '#000000'
      }
    }],
  };


  searchControl = new FormControl();
  options: string[] = ['One', 'Two', 'Three'];
  filteredOptions: Observable<string[]>;

  constructor(private activatedRoute: ActivatedRoute, private heatmapService: HumanHeatmapService) {
    console.log('constructor on human cell heatmap fired');
  }

  ngOnInit() {
    console.log('calling human cell heatmap init method');
    this.sortFieldSelected = this.defaultSortField;
    this.filterMethod();
    this.getCellTypesDropdown();
    this.getCellSubTypesDropdown();
    this.getAssaysDropdown();

    this.selectControl.valueChanges.subscribe(val => {
      console.log('val=' + val);
      if (val !== undefined && val !== 'None' && val.length > 0) {
        this.searchControl.disable({
          onlySelf: true,
          emitEvent: false
        });
      } else {
        this.searchControl.enable({
          onlySelf: true,
          emitEvent: false
        });
      }
    });
  }

  private _filter(value: string): string[] {
    console.log('value for search=' + value + ' length=' + value.length);

    const filterValue = value.toLowerCase();
    if (filterValue.length > 0) {
      this.selectControl.disable({
        onlySelf: true,
        emitEvent: false
      });
    }
    if (filterValue.length === 0) {
      console.log('enabling select');
      this.selectControl.enable({
        onlySelf: true,
        emitEvent: false
      });
    }
    return this.geneSymbols.filter(option => option.toLowerCase().includes(filterValue));
  }

  filterMethod() {
    console.log( 'query button clicked with ' +
      ' cell selected=' + this.cellSelected + ' cellSubtypeSelected=' + this.cellSubtypeSelected );
    const filter = new HumanCellFilter(this.searchControl.value, this.cellSelected,
      this.cellSubtypeSelected, this.assaySelected, this.sortFieldSelected);
    this.getHeatmapData(filter);
  }

  clearFilter() {
    // console.log('query button clicked with constructSeleted '+this.constructSelected+'
    // cell selected='+this.cellSelected+' cellSubtypeSelected='+this.cellSubtypeSelected);
    this.searchControl.reset('');
    this.searchControl.enable({
      onlySelf: true,
      emitEvent: false
    });
    this.selectControl.enable({
      onlySelf: true,
      emitEvent: false
    });
    this.cellSelected = null,
      this.cellSubtypeSelected = null, this.assaySelected = null, this.sortFieldSelected = null;
    const filter = new HumanCellFilter(this.searchControl.value, this.cellSelected,
      this.cellSubtypeSelected, this.assaySelected, this.defaultSortField);
    this.sortFieldSelected = this.defaultSortField;
    this.getHeatmapData(filter);
  }

  // tslint:disable-next-line:use-life-cycle-interface
  ngAfterViewInit() {
  }

  getHeatmapData(filter: HumanCellFilter) {
    this.resourceLoaded = false;
    // if(this.data.length<=1){
    this.heatmapService.getCellHeatmapResponse(filter).subscribe(resp => {
      // display its headers
      this.response = { ... resp.body};
      this.data = this.response['data'];
      this.columnHeaders = this.response['columnHeaders'];
      this.rowHeaders = this.response['rowHeaders'];
      // here we need to add a whole column populated with the construct as java has no way of mixing strings and ints in an array
      this.constructs = this.response['constructs'];
      this.displayCellChart();
      // only do this once on load so we have full gene list all the time for autosuggest
      if (this.doAutosuggest) {
        console.log('setting symbols');
        this.geneSymbols = this.rowHeaders;
        this.filteredOptions = this.searchControl.valueChanges.pipe(
          startWith(''),
          map(value => this._filter(value))
        );
        this.doAutosuggest = false; // set to false so we only do this once
      }
    });
  }

  addLinksToColumnHeaders(columnHeaders: string[]) {
    const tempHeaders = new Array<string>();
    for (const header of columnHeaders) {
      // console.log('column header='+header);
      const tempHeader = '<a href="/data?sort=' + header + '">' + header + '</a>';
      // console.log('tempHeader='+tempHeader);
      tempHeaders.push(tempHeader);
    }
    return tempHeaders;
  }


  getCellTypesDropdown() {
    this.heatmapService.getCellTypeResponse().subscribe(resp => {
      const lResponse = { ... resp.body};
      this.cells = lResponse['types'];
    });
  }


  getCellSubTypesDropdown() {
// console.log('calling cellSubType dropdown');
// this.resourceLoaded=false;
// if(this.data.length<=1){
    this.heatmapService.getCellSubTypeResponse().subscribe(resp => {
      // display its headers
      const lResponse = { ... resp.body};
      this.cellSubTypes = lResponse['types'];
    });
  }

  getAssaysDropdown() {
    this.heatmapService.getAssaysResponse().subscribe(resp => {
      // display its headers
      const lResponse = { ... resp.body};
      this.assays = lResponse['types'];
    });
  }

  openImpc() {
    window.open('https://www.mousephenotype.org/data/search/gene?kw=' + this.searchControl.value, '_blank');
  }


  displayCellChart() {

    console.log('calling display cell chart method');
    let spaceForHeaders = 350;
    if (this.data.length === 0) {
      this.showEmtpyResultMessage = true;
      spaceForHeaders = 0;
    } else {
      this.showEmtpyResultMessage = false;
    }
    const chartHeight = spaceForHeaders + this.rowHeaders.length * 20;
    this.cellChartOptions.chart.height = chartHeight;
    this.cellChartOptions.xAxis.categories = this.columnHeaders;
    this.cellChartOptions.yAxis.categories = this.rowHeaders,
      // this.cellChartOptions.series[0].name = 'Cell types with significant parameters',
      this.cellChartOptions.series[0].data = this.data;
    this.resourceLoaded = true;
    this.updatechart = true;
  }// end of display method

}
