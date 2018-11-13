import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCard, MatButtonModule  } from '@angular/material';
import * as Highcharts from 'highcharts/highcharts';
import * as HC_map from 'highcharts/modules/map';
import * as HC_exporting from 'highcharts/modules/exporting';
// import * as HC_CustomEvents from 'highcharts-custom-events';
import { MatRadioModule, MatSelectModule, MatTabChangeEvent } from '@angular/material';

import { HeatmapService } from '../heatmap.service';
import { CellFilter } from './cell-filter';

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
  // tslint:disable-next-line:component-selector
  selector: 'threei-cell-heatmap',
  templateUrl: './cell-heatmap.component.html',
  styleUrls: ['./cell-heatmap.component.css']
})
export class CellHeatmapComponent implements OnInit {

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
    keyword: '';
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
    response: Response;
    chartTitle = 'Procedure Heatmap'; // for init - change through titleChange
    updatechart = false;
    sort: string;
    resourceLoaded = false;
    cellChart;
cellChartOptions =  {

  chart: {
    type: 'heatmap',
    marginTop: 200,
    marginBottom: 80,
    plotBorderWidth: 1,
    height: 1000,
    width: 1000
},
title: {
  text: 'Cell Type Heatmap'
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
    name: 'Not Significantly Different'
}, {
    from: 3,
    to: 4,
    color: '#c4463a',
    name: 'Significantly Different'
}
],
min: 0,
max: 4,
},
legend: {
  align: 'right',
  layout: 'vertical',
  // margin: 0,
  verticalAlign: 'top',
  backgroundColor: 'whitesmoke'
  // y: 25,
  // symbolHeight: 280
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


  constructor(private activatedRoute: ActivatedRoute, private heatmapService: HeatmapService) {
    console.log('constructor on cell heatmap fired');
  }

  filterMethod() {
    console.log( 'query button clicked with ' +
    ' cell selected=' + this.cellSelected + ' cellSubtypeSelected=' + this.cellSubtypeSelected );
      const filter = new CellFilter(this.keyword, this.cellSelected,
        this.cellSubtypeSelected, this.assaySelected, this.sortFieldSelected);
    this.getHeatmapData(filter);
  }

  clearFilter() {
    // console.log('query button clicked with constructSeleted '+this.constructSelected+'
    // cell selected='+this.cellSelected+' cellSubtypeSelected='+this.cellSubtypeSelected);
      this.keyword = null, this.cellSelected = null,
      this.cellSubtypeSelected = null, this.assaySelected = null, this.sortFieldSelected = null;
      const filter = new CellFilter(this.keyword, this.cellSelected,
        this.cellSubtypeSelected, this.assaySelected, this.defaultSortField);
      this.getHeatmapData(filter);
  }

  ngOnInit() {
    console.log('calling cell heatmap init method');
    this.sortFieldSelected = this.defaultSortField;
    this.filterMethod();
    this.getCellTypesDropdown();
    this.getCellSubTypesDropdown();
    this.getAssaysDropdown();
  }

  // tslint:disable-next-line:use-life-cycle-interface
  ngAfterViewInit() {
  }

  getHeatmapData(filter: CellFilter) {
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




// change in all places
titleChange = function(event) {
  const v = event;
  this.chartTitle = v;
  this.charts.forEach((el) => {
    el.hcOptions.title.text = v;
  });
  // trigger ngOnChanges
  this.updateDemo2 = true;
};


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
  this.defaultSortField = this.cellSubTypes[1];
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
  window.open('https://www.mousephenotype.org/data/search?kw=*', '_blank');
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
