import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatCard  } from '@angular/material';
import * as Highcharts from 'highcharts/highcharts';
import * as HC_map from 'highcharts/modules/map';
import * as HC_exporting from 'highcharts/modules/exporting';
import { MatRadioModule, MatSelectModule } from '@angular/material';

import { HeatmapService } from '../heatmap.service';
import { ProcedureFilter } from './procedure-filter';

HC_map(Highcharts);
HC_exporting(Highcharts);

Highcharts.setOptions({
    series: [{
      data: [1, 2, 3]
    }]
  });




@Component({
  // tslint:disable-next-line:component-selector
  selector: 'threei-procedure-heatmap',
  templateUrl: './procedure-heatmap.component.html',
  styleUrls: ['./procedure-heatmap.component.css']
})
export class ProcedureHeatmapComponent implements OnInit {
    Highcharts = Highcharts;
    @ViewChild('searchBox') searchBox;
    sortFieldSelected: string;
    keyword: '';
    constructs: string[]; // all constructs available including the brackets
    constructTypes: string[]; // for menu dropdown just conatains unique set with brackets part removed
    constructSelected: string;

//     cellSubTypeDropdowns: string[];
//     cellSubType: any;
//     readonly PROCEDURE_TYPE = 'procedure';
//   readonly CELL_TYPE = 'cell';
//   cellTypesForDropdown: string[];


        data: any[][] = [[]];
        headers: string[]; // http response headers
        columnHeaders: string[];
        rowHeaders: string[];
        response: Response;
        constructColumnData: any[]; // need any as string for construct and ints for col and row indexes

        updateDemo2 = false;
  usedIndex = 0;
  chartTitle = 'Procedure Heatmap'; // for init - change through titleChange

  procedureChart;
  resourceLoaded = false;

  procedureChartOptions = {

chart: {
      type: 'heatmap',
      marginTop: 200,
      marginBottom: 80,
      plotBorderWidth: 1,
      height: 17000,
      width: 1000
  },
  title: {
      text: 'Procedure Type Heatmap'
  },

  xAxis: {
    opposite: true,
      categories: [
            'Data loading...',
                 ],
      labels: {
          rotation: 90
      },
      reserveSpace: true,
    },
    yAxis: [
      {
     categories: ['gene blah1, gene blah2'],
     title: 'gene'
  }, {
     linkedTo: 0,
     title: 'construct',
     lineWidth: 2,
     categories: ['constr blah1, contr blah2'],
  }],
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
      // symbolHeight: 280
  },
  plotOptions: {
    series: {
        events: {
            click: function (e) {
            }
        }
    }
  },

  series: [{
    name: 'Data Loading....',
    borderWidth: 1,
    data: [[0, 0, 'blah'], [0, 1, 1]],
    dataLabels: {
        enabled: false,
        color: '#000000'
    }
  }],

};

  constructor(private heatmapService: HeatmapService) {

  }

  ngOnInit() {
    this.filterMethod();
    this.getConstructsDropdown();
  }


  clearFilter() {
      this.keyword = null, this.constructSelected = null, this.sortFieldSelected = null;
      const filter = new ProcedureFilter(this.keyword, this.constructSelected, this.sortFieldSelected);
    this.getHeatmapData(filter);
  }

  filterMethod() {
    const filter = new ProcedureFilter(this.keyword, this.constructSelected,  this.sortFieldSelected);
    this.getHeatmapData(filter);
  }

  getHeatmapData(filter: ProcedureFilter) {
    this.resourceLoaded = false;
    this.heatmapService.getProcedureHeatmapResponse(filter).subscribe(resp => {
      // display its headers
      this.response = { ... resp.body};
      this.data = this.response['data'];
      this.columnHeaders = this.response['columnHeaders'];
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
      this.rowHeaders = this.response['rowHeaders'];
      // here we need to add a whole column populated with the construct as java has no way of mixing strings and ints in an array
      this.constructs = this.response['constructs'];

    //   for (let index = 0; index < this.constructs.length; index++) {
    //     let cell:[string, number, number];//a new cell for each construct
    //       const construct = this.constructs[index];
    //       cell=[construct, 0, index];
    //       this.data.push(cell);
    //   }
    if (this.heatmapService.defaultProcedureHeatmapChart !== undefined &&
      filter.keyword === undefined && filter.construct === undefined &&
      filter.sort === undefined) {
        this.procedureChartOptions = this.heatmapService.defaultProcedureHeatmapChart;
        console.log('using cached procedureheatmap');
        this.resourceLoaded = true;
        this.updateDemo2 = true;
      } else {
      this.displayProcedureChart();
      }
    });
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



  displayProcedureChart() {
console.log('calling display procedure chart method');
const spaceForHeaders = 350;
    const chartHeight = spaceForHeaders + this.rowHeaders.length * 20;
this.procedureChart = {


    chart: {
      type: 'heatmap',
      marginTop: 200,
      marginBottom: 80,
      plotBorderWidth: 1,
      height: chartHeight,
      width: 1000
    },

    xAxis: {
      opposite: true,
        categories: this.columnHeaders,
        labels: {
            rotation: 90
        },
        reserveSpace: true,
      },
// example of multiple columns working http://jsfiddle.net/0qmt0mkq/
yAxis: [{
    categories: this.rowHeaders,
    title: null
  }, {
  categories: this.constructs,
  title: null
  } ],

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
        // y: 25,
        // symbolHeight: 280
    },

    tooltip: {
        formatter: function () {
            return '<b>' + this.series.xAxis.categories[this.point.x] + '</b><br/>' +
            this.series.colorAxis.dataClasses[this.point.dataClass].name + '</b><br>'+
            '<b>' + this.series.yAxis.categories[this.point.y] + '</b>';
        }
    },
    plotOptions: {
      series: {
          events: {
              click: function (e) {
                  const gene = e.point.series.yAxis.categories[e.point.y];
                  // var construct=e.point.series.yAxis.categories[e.point.y];
                  const procedure = e.point.series.xAxis.categories[e.point.x];
                  const text = 'gene: ' + gene +
                           ' Procedure: ' + procedure + ' significance=' + e.point.value ;
                  // may have to use routerLink like for menus to link to our new not created yet parameter page
                    const url = 'http://starwars.com';
                    const routerLink = 'details?' + 'procedure=' + procedure + '&gene=' + gene;
                    window.open(routerLink,'_blank');
                    //   this.chart.clickLabel.attr({
                    //       text: text
                    //   });
                      console.log('text on click=' + text);
              }
          }
      }
  },

    series: [{
        name: 'Procedures with significant parameters',
        borderWidth: 1,
        data: this.data,
        dataLabels: {
            enabled: false,
            color: '#000000'
        }
    }]
};
this.resourceLoaded = true;
      // this.Highcharts.setOptions(this.cellChartOptions);
      this.procedureChartOptions = this.procedureChart;
      this.resourceLoaded = true;
      if (this.heatmapService.defaultProcedureHeatmapChart === undefined) { // only update the default on first
       // call to the this method - once default is set always default for this user.
      this.heatmapService.defaultProcedureHeatmapChart = this.procedureChart;
      }
      this.updateDemo2 = true;
  }// end of display method


  getConstructsDropdown(): string[] {
    // console.log('calling assay dropdown');
    // this.resourceLoaded=false;
    // if(this.data.length<=1){
    this.heatmapService.getConstructsResponse().subscribe(resp => {
      // display its headers
      const lResponse = { ... resp.body};
      this.constructTypes = lResponse['types'];
  });
  return this.constructTypes;
  }
}
