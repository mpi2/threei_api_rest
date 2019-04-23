import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'threei-map-of-immune',
  templateUrl: './map-of-immune.component.html',
  styleUrls: ['./map-of-immune.component.css']
})
export class MapOfImmuneComponent implements OnInit {

  video: string = "https://www.mousephenotype.org/data/threeIAnalysis";
  constructor() { }

  ngOnInit() {
  }

}
