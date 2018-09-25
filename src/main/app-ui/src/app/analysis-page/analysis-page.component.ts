import { Component, OnInit } from '@angular/core';

import { ViewEncapsulation, ViewChild, ElementRef, PipeTransform, Pipe } from '@angular/core';
import { DomSanitizer } from "@angular/platform-browser";

@Pipe({ name: 'safe' })
export class SafePipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) { }
  transform(url) {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}

@Component({
  selector: 'threei-analysis-page',
  templateUrl: './analysis-page.component.html',
  styleUrls: ['./analysis-page.component.css']
})
export class AnalysisPageComponent implements OnInit {

  video: string = "https://dev.mousephenotype.org/data/threeIAnalysis"

  constructor() { }

  ngOnInit() {

    
  }

}
